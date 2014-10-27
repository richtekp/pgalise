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
package de.pgalise.simulation.energy.internal;

import de.pgalise.simulation.energy.EnergyConsumptionManagerLocal;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;

/**
 * Energy Consumption manager that uses fuzzy rules and maximum energy values to
 * determine the current energy consumption. The fuzzy rules can be changed in
 * 'fuzzy/energy_fuzzy.fcl' and the maximum energy values in
 * 'fuzzy/properties.props'.
 *
 * @author Timo
 * @author Andreas
 */
@Stateful
public class FuzzyEnergyConsumptionManager implements
  EnergyConsumptionManagerLocal {

  private static final String FUNCTION_BLOCK_NAME = "energy";
  private static final String OUTPUT_VARIABLE_NAME = "percentage";

  /**
   * The energy fuzzy logic inference system
   */
  private FIS energyFIS;

  /**
   * Fuzzy properties
   */
  private Properties properties;

  /**
   * Contains the max energy consumption for every profile.
   */
  private Map<EnergyProfileEnum, Double> maxEnergyConsumptionMap;

  @EJB
  private WeatherControllerLocal weatherController;

  /**
   * Default
   */
  public FuzzyEnergyConsumptionManager() {
  }

  @Override
  public double getEnergyConsumptionInKWh(long timestamp,
    EnergyProfileEnum key,
    BaseCoordinate position) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(timestamp);
    double percentage = 0.0;
    synchronized (this.energyFIS) {
      RuleBlock ruleBlock = this.energyFIS.getFunctionBlock(FUNCTION_BLOCK_NAME).
        getFuzzyRuleBlock(key.getKey());
      ruleBlock.setVariable("temperature",
        this.weatherController.
        getValue(WeatherParameterEnum.TEMPERATURE,
          timestamp,
          position).doubleValue());
      ruleBlock.setVariable("brightness",
        this.weatherController.getValue(WeatherParameterEnum.LIGHT_INTENSITY,
          timestamp,
          position).doubleValue());
      ruleBlock.setVariable("rain",
        this.weatherController.getValue(
          WeatherParameterEnum.PRECIPITATION_AMOUNT,
          timestamp,
          position).doubleValue());
      ruleBlock.setVariable("storm",
        this.weatherController.getValue(WeatherParameterEnum.WIND_VELOCITY,
          timestamp,
          position).doubleValue());
      ruleBlock.setVariable("timeOfDay",
        calendar.get(Calendar.HOUR_OF_DAY));
      ruleBlock.setVariable("dayOfWeek",
        calendar.get(Calendar.DAY_OF_WEEK) == 1 ? 6 : calendar.get(
          Calendar.DAY_OF_WEEK) - 1);
      ruleBlock.setVariable("season",
        calendar.get(Calendar.MONTH) + 1);
      ruleBlock.evaluate();
      percentage = ruleBlock.getVariable(OUTPUT_VARIABLE_NAME).defuzzify();
      ruleBlock.reset();
    }

    return this.maxEnergyConsumptionMap.get(key) * percentage;
  }

  @Override
  public void init(long start,
    long end,
    WeatherControllerLocal weatherController) {

    this.weatherController = weatherController;

    // Read properties
    try (InputStream inputStream = FuzzyEnergyConsumptionManager.class
      .getResourceAsStream("/fuzzy/properties.properties")) {
      this.properties = new Properties();
      this.properties.load(inputStream);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    this.energyFIS = FIS.load(FuzzyEnergyConsumptionManager.class.
      getResourceAsStream("/fuzzy/energy_fuzzy.fcl"),
      true);

    /* fill max energy map*/
    this.maxEnergyConsumptionMap = new HashMap<>();
    for (EnergyProfileEnum energyProfile : EnergyProfileEnum.values()) {
      this.maxEnergyConsumptionMap.put(energyProfile,
        Double.valueOf(this.properties.getProperty(
            energyProfile.getKey() + "-max")));
    }
  }

  public void setWeatherController(WeatherControllerLocal weatherController) {
    this.weatherController = weatherController;
  }
}
