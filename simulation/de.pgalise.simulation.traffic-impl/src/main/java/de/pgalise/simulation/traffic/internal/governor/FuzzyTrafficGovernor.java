/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.traffic.internal.governor;

import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.governor.TrafficGovernor;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Implementation of a TrafficGovernator using fuzzy rule logic. Input variables, output variables, fuzzyfication,
 * defuzzyfication, fuzzy rule set, used operators for fuzzy rules and gravity method are defined in a separate FCL-file
 * (Fuzzy Control Language). To gain further information about FLC see {@linkplain http
 * ://en.wikipedia.org/wiki/Fuzzy_Control_Language}.
 * 
 * @author Marcus
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.traffic.TrafficGovernor")
@Local(TrafficGovernor.class)
public final class FuzzyTrafficGovernor implements TrafficGovernor {

	/**
	 * Path to FCL-file (FCL = Fuzzy Control Language) for {@link Car}s. To gain further information about FLC see
	 * {@linkplain http://en.wikipedia.org/wiki/Fuzzy_Control_Language}
	 * 
	 * @see http://en.wikipedia.org/wiki/Fuzzy_Control_Language
	 */
	private final static String FILE_PATH = "/fuzzy.fcl";

	/**
	 * function name for traffic rules
	 */
	private final static String FUNCTION_BLOCK_NAME_TRAFFIC = "function_traffic";

	/**
	 * rule block name for cars
	 */
	private final static String RULE_BLOCK_NAME_CARS = "rule_block_cars";

	/**
	 * rule block name for trucks
	 */
	private final static String RULE_BLOCK_NAME_TRUCKS = "rule_block_trucks";

	/**
	 * rule block name for bicycles
	 */
	private final static String RULE_BLOCK_NAME_BICYCLES = "rule_block_bicycles";

	/**
	 * rule block name for motorcycles
	 */
	private final static String RULE_BLOCK_NAME_MOTORCYCLES = "rule_block_motorcycles";

	/**
	 * rule block name for bus passangers
	 */
	private final static String RULE_BLOCK_NAME_BUSPASSENGERS = "rule_block_buspassengers";

	/**
	 * name of input variable for brightness
	 */
	private final static String INPUT_LIGHT_INTENSITY = "light_intensity";

	/**
	 * name of input variable for day of week
	 */
	private final static String INPUT_DAY_OF_WEEK = "day_of_week";

	/**
	 * name of input variable for rain
	 */
	private final static String INPUT_PRECIPITATION_AMOUNT = "precipitation_amount";

	/**
	 * name of input variable for season
	 */
	private final static String INPUT_SEASON = "season";

	/**
	 * name of input variable for temperature
	 */
	private final static String INPUT_TEMPERATURE = "temperature";

	/**
	 * name of input variable for time of day
	 */
	private final static String INPUT_TIME_OF_DAY = "time_of_day";

	/**
	 * name of input variable for raistormn
	 */
	private final static String INPUT_WIND_STRENGTH = "wind_strength";

	/**
	 * name of input variable for rain
	 */
	private final static String OUTPUT_PERCENTAGE = "percentage";

	/**
	 * Helper class for binding input variables
	 * 
	 * @author marcus
	 */
	private abstract static class InputVariable {

		/**
		 * the input variable's name
		 */
		private final String variableName;

		/**
		 * @param variableName
		 *            the name of the input variable
		 */
		private InputVariable(final String variableName) {
			if (variableName == null) {
				throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("variableName"));
			}
			this.variableName = variableName;
		}

		/**
		 * sets this input variable for the passed {@link RuleBlock} at the passed time.
		 * 
		 * @param ruleBlock
		 *            the rule block for which the input variable is to be set
		 * @param cal
		 *            the simulation time wrapped in a {@link Calendar}
		 */
		private void set(final RuleBlock ruleBlock, final Calendar cal) {
			final double value = this.getValue(cal);
			// LOG.debug(this.variableName + ": " + value);
			ruleBlock.setVariable(this.variableName, value);
		}

		/**
		 * Returns the value of the input variable at a passed time.
		 * 
		 * @param cal
		 *            determines the time needed to get the value
		 * @return the value of the input variable at a passed time
		 */
		protected abstract double getValue(final Calendar cal);
	}

	/*
	 * Input variable definition
	 */

	/**
	 * {@link InputVariable} for light intensity
	 */
	private final InputVariable inputLightIntensity;

	/**
	 * {@link InputVariable} for day of week
	 */
	private final InputVariable inputDayOfWeek;

	/**
	 * {@link InputVariable} for rain
	 */
	private final InputVariable inputPrecipitationAmount;

	/**
	 * {@link InputVariable} for season
	 */
	private final InputVariable inputSeason;

	/**
	 * {@link InputVariable} for temperature
	 */
	private final InputVariable inputTemperature;

	/**
	 * {@link InputVariable} for tike of day
	 */
	private final InputVariable inputTimeOfDay;

	/**
	 * {@link InputVariable} for wind strength
	 */
	private final InputVariable inputWindStrength;

	/**
	 * the fuzzy interference system for cars
	 */
	private final FIS fis;

	/**
	 * name of input variable for rain
	 */
	private WeatherController weatherController;

	/**
	 * ServiceDictionary for resolving remote Services
	 */
	@EJB
	private ServiceDictionary serviceDictionary;

	/**
	 * Creates a FuzzyTrafficGovernor with definition from the FCL-file of the passed "filePath" argument.
	 * 
	 * @param fclFilePath
	 *            path to FCL-file
	 * @param weatherController
	 *            the necessary weatherController for setting the input variables
	 * @exception IllegalArgumentException
	 *                Is thrown if an argument is "null" or the file path does not exist or the file is no valid
	 *                FCL-File.
	 */
	private FuzzyTrafficGovernor(final String fclFilePath) {
		if (fclFilePath == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("fclFilePath"));
		}
		try {
			this.fis = FIS.load(FuzzyTrafficGovernor.class.getResourceAsStream(fclFilePath), true);
		} catch (final Exception ex) {
			throw new RuntimeException(ex);
		}

		/*
		 * Define the InputVariables
		 */
		this.inputLightIntensity = new InputVariable(FuzzyTrafficGovernor.INPUT_LIGHT_INTENSITY) {
			@Override
			protected double getValue(final Calendar cal) {
				return FuzzyTrafficGovernor.this.weatherController.getValue(WeatherParameterEnum.LIGHT_INTENSITY,
						cal.getTimeInMillis(), FuzzyTrafficGovernor.this.weatherController.getReferencePosition())
						.doubleValue();
			}
		};
		this.inputDayOfWeek = new InputVariable(FuzzyTrafficGovernor.INPUT_DAY_OF_WEEK) {
			@Override
			protected double getValue(final Calendar cal) {
				return (cal.get(Calendar.DAY_OF_WEEK) + 6) % 7;
			}
		};
		this.inputPrecipitationAmount = new InputVariable(FuzzyTrafficGovernor.INPUT_PRECIPITATION_AMOUNT) {
			@Override
			protected double getValue(final Calendar cal) {
				return FuzzyTrafficGovernor.this.weatherController.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
						cal.getTimeInMillis(), FuzzyTrafficGovernor.this.weatherController.getReferencePosition())
						.doubleValue();
			}
		};
		this.inputSeason = new InputVariable(FuzzyTrafficGovernor.INPUT_SEASON) {
			@Override
			protected double getValue(final Calendar cal) {
				return cal.get(Calendar.MONTH);
			}
		};
		this.inputTemperature = new InputVariable(FuzzyTrafficGovernor.INPUT_TEMPERATURE) {
			@Override
			protected double getValue(final Calendar cal) {
				return FuzzyTrafficGovernor.this.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
						cal.getTimeInMillis(), FuzzyTrafficGovernor.this.weatherController.getReferencePosition())
						.doubleValue();
			}
		};
		this.inputTimeOfDay = new InputVariable(FuzzyTrafficGovernor.INPUT_TIME_OF_DAY) {
			@Override
			protected double getValue(final Calendar cal) {
				return cal.get(Calendar.HOUR_OF_DAY) + (cal.get(Calendar.MINUTE) * (1D / 60D));
			}
		};
		this.inputWindStrength = new InputVariable(FuzzyTrafficGovernor.INPUT_WIND_STRENGTH) {
			@Override
			protected double getValue(final Calendar cal) {
				return FuzzyTrafficGovernor.this.weatherController.getValue(WeatherParameterEnum.WIND_STRENGTH,
						cal.getTimeInMillis(), FuzzyTrafficGovernor.this.weatherController.getReferencePosition())
						.doubleValue();
			}
		};
	}

	/**
	 * Creates a default FuzzyTrafficGovernor from file {@link src /main/resources/traffic.fcl}
	 * 
	 * @param weatherController
	 *            the necessary weatherController for setting the input variable
	 * @exception IllegalArgumentException
	 *                Is thrown if an argument is "null"
	 */
	public FuzzyTrafficGovernor() {
		this(FuzzyTrafficGovernor.FILE_PATH);
	}

	/**
	 * Initiates all dependencies.
	 * 
	 * @throws IllegalStateException
	 *             if ServiceDictionary returns 'null' for WeatherController.
	 */
	@PostConstruct
	private void init() throws IllegalStateException {
		// Extract WeatherController
		this.weatherController = this.serviceDictionary.getController(WeatherController.class);
		if (this.weatherController == null) {
			throw new IllegalStateException("ServiceDictionary returns 'null' for 'WeatherController'");
		}
	}

	/**
	 * Calculates the result for the passed arguments
	 * 
	 * @param simTime
	 *            the simulation time on which the traffic output is based
	 * @param ruleBlock
	 *            the rule block for the vehicle type
	 * @param outputVariable
	 *            the specialized output
	 * @param inputVariables
	 *            the input variables that have to be binded
	 * @return the defuzzificated result
	 */
	private double calcResult(final long simTime, final RuleBlock ruleBlock, final String outputVariable,
			final InputVariable... inputVariables) {
		if (inputVariables == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("inputVariables"));
		}

		// check whether all InputVariables are instantiated
		for (final InputVariable var : inputVariables) {
			if (var == null) {
				throw new UnsupportedOperationException(
						ExceptionMessages.getMessageForCollItemNotNull("inputVariables"));
			}
		}

		// create a calendar and set time to simulation time
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(simTime);

		ruleBlock.reset();
		// Set all variables to the rule block
		for (final InputVariable var : inputVariables) {
			var.set(ruleBlock, calendar);
		}

		// Evaluate the FIS
		ruleBlock.evaluate();

		// Show each rule (and degree of support)
		// for(final Rule r : ruleBlock.getRules()) {
		// LOG.debug(r.toString());
		// }

		return ruleBlock.getVariable(OUTPUT_PERCENTAGE).defuzzify();
	}

	/**
	 * Returns the fuzzy calculated average percentage (0.0 - 1.0) of a {@link Bus}' manning at a given simulation time.
	 * 
	 * @param simTime
	 *            the current simulation time
	 * @return the fuzzy calculated average percentage (0.0 - 1.0) of a {@link Bus}' manning
	 */
	@Override
	public double getAverageBusManningPercentage(final long simTime) {
		final FunctionBlock functionBlock = this.fis.getFunctionBlock(FuzzyTrafficGovernor.FUNCTION_BLOCK_NAME_TRAFFIC);
		final RuleBlock ruleBlock = functionBlock.getFuzzyRuleBlock(FuzzyTrafficGovernor.RULE_BLOCK_NAME_BUSPASSENGERS);
		return this.calcResult(simTime, ruleBlock, FuzzyTrafficGovernor.OUTPUT_PERCENTAGE, this.inputTimeOfDay,
				this.inputDayOfWeek, this.inputSeason, this.inputTemperature, this.inputPrecipitationAmount,
				this.inputWindStrength);
	}

	/**
	 * Returns the fuzzy calculated percentage (0.0 - 1.0) of active {@link Bike}s at a given simulation time.
	 * 
	 * @param simTime
	 *            the current simulation time
	 * @return the percentage (0.0 - 1.0) of active {@link Bike}s
	 */
	@Override
	public double getPercentageOfActiveBicycles(final long simTime) {
		final FunctionBlock functionBlock = this.fis.getFunctionBlock(FuzzyTrafficGovernor.FUNCTION_BLOCK_NAME_TRAFFIC);
		final RuleBlock ruleBlock = functionBlock.getFuzzyRuleBlock(FuzzyTrafficGovernor.RULE_BLOCK_NAME_BICYCLES);
		return this.calcResult(simTime, ruleBlock, FuzzyTrafficGovernor.OUTPUT_PERCENTAGE, this.inputTimeOfDay,
				this.inputDayOfWeek, this.inputSeason, this.inputTemperature, this.inputPrecipitationAmount,
				this.inputWindStrength);
	}

	/**
	 * Returns the fuzzy calculated percentage (0.0 - 1.0) of active {@link Car} s at a given simulation time.
	 * 
	 * @param simTime
	 *            the current simulation time
	 * @return the percentage (0.0 - 1.0) of active {@link Car}s
	 */
	@Override
	public double getPercentageOfActiveCars(final long simTime) {
		final FunctionBlock functionBlock = this.fis.getFunctionBlock(FuzzyTrafficGovernor.FUNCTION_BLOCK_NAME_TRAFFIC);
		final RuleBlock ruleBlock = functionBlock.getFuzzyRuleBlock(FuzzyTrafficGovernor.RULE_BLOCK_NAME_CARS);
		return this.calcResult(simTime, ruleBlock, FuzzyTrafficGovernor.OUTPUT_PERCENTAGE, this.inputTimeOfDay,
				this.inputDayOfWeek, this.inputLightIntensity, this.inputSeason, this.inputTemperature,
				this.inputPrecipitationAmount, this.inputWindStrength);
	}

	/**
	 * Returns the fuzzy calculated percentage (0.0 - 1.0) of active {@link DefaultMotorcycle}s at a given simulation
	 * time.
	 * 
	 * @param simTime
	 *            the current simulation time
	 * @return the percentage (0.0 - 1.0) of active {@link DefaultMotorcycle}s
	 */
	@Override
	public double getPercentageOfActiveMotorcycles(long simTime) {
		final FunctionBlock functionBlock = this.fis.getFunctionBlock(FuzzyTrafficGovernor.FUNCTION_BLOCK_NAME_TRAFFIC);
		final RuleBlock ruleBlock = functionBlock.getFuzzyRuleBlock(FuzzyTrafficGovernor.RULE_BLOCK_NAME_MOTORCYCLES);
		return this.calcResult(simTime, ruleBlock, FuzzyTrafficGovernor.OUTPUT_PERCENTAGE, this.inputTimeOfDay,
				this.inputDayOfWeek, this.inputSeason, this.inputTemperature, this.inputPrecipitationAmount,
				this.inputWindStrength);
	}

	/**
	 * Returns the fuzzy calculated percentage (0.0 - 1.0) of active {@link DefaultTruck}s at a given simulation time.
	 * 
	 * @param simTime
	 *            the current simulation time
	 * @return the percentage (0.0 - 1.0) of active {@link DefaultTruck}s
	 */
	@Override
	public double getPercentageOfActiveTrucks(final long simTime) {
		final FunctionBlock functionBlock = this.fis.getFunctionBlock(FuzzyTrafficGovernor.FUNCTION_BLOCK_NAME_TRAFFIC);
		final RuleBlock ruleBlock = functionBlock.getFuzzyRuleBlock(FuzzyTrafficGovernor.RULE_BLOCK_NAME_TRUCKS);
		return this.calcResult(simTime, ruleBlock, FuzzyTrafficGovernor.OUTPUT_PERCENTAGE, this.inputTimeOfDay,
				this.inputDayOfWeek);
	}

	public WeatherController getWeatherController() {
		return weatherController;
	}

	public void setWeatherController(WeatherController weatherController) {
		this.weatherController = weatherController;
	}

}
