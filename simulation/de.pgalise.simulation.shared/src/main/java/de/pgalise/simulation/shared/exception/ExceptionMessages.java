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
 
package de.pgalise.simulation.shared.exception;

/**
 * Store exception messages for application wide use
 * 
 * @author Marcus
 */
public final class ExceptionMessages {

	/**
	 * Creates a string for {@link IllegalArgumentException} due to passing zero
	 * values.
	 * 
	 * @param argName
	 *            the name of the argument with the null value
	 * @return the exception message
	 * @throws IllegalArgumentException
	 *             if argument 'argName' is 'null'
	 */
	public static String getMessageForNotNegative(final String argName, final boolean mayBeZero) throws IllegalArgumentException {
		ExceptionMessages.checkArgument(argName);
		if(mayBeZero) {
			return String.format("Argument '%s' must not be negative.", argName);
		}
		return String.format("Argument '%s' must not be negative or 'zero'.", argName);
	}

	/**
	 * Creates a string for {@link IllegalArgumentException} due to passing null
	 * values.
	 * 
	 * @param argName
	 *            the name of the argument with the null value
	 * @return the exception message
	 * @throws IllegalArgumentException
	 *             if argument 'argName' is 'null'
	 */
	public static String getMessageForNotNull(final String argName) throws IllegalArgumentException {
		ExceptionMessages.checkArgument(argName);
		return String.format("Argument '%s' must not be 'null'.", argName);
	}

	/**
	 * Creates a string for {@link UnsupportedOperationException} due to a collection or array containing null-elements.
	 * @param collName
	 * the name of the collection or array
	 * @return the exception message
	 * @throws IllegalArgumentException if argument 'collName' is 'null'
	 */
	public static String getMessageForCollItemNotNull(final String collName) throws IllegalArgumentException {
		ExceptionMessages.checkArgument(collName);
		return String.format("Collection or array '%s' must not contain null-elements", collName);
	}
	
	/**
	 * Creates a string for {@link IllegalArgumentException} due to passing zero
	 * values.
	 * 
	 * @param argName
	 *            the name of the argument with the null value
	 * @return the exception message
	 * @throws IllegalArgumentException
	 *             if argument 'argName' is 'null'
	 */
	public static String getMessageForNotPositive(final String argName, final boolean mayBeZero) throws IllegalArgumentException {
		ExceptionMessages.checkArgument(argName);
		if(mayBeZero) {
			return String.format("Argument '%s' must be negative or 'zero'.", argName);
		}
		return String.format("Argument '%s' must be negative.", argName);
	}

	/**
	 * Creates a string for {@link IllegalArgumentException} due to passing zero
	 * values.
	 * 
	 * @param argName
	 *            the name of the argument with the null value
	 * @return the exception message
	 * @throws IllegalArgumentException
	 *             if argument 'argName' is 'null'
	 */
	public static String getMessageForNotZero(final String argName) throws IllegalArgumentException {
		ExceptionMessages.checkArgument(argName);
		return String.format("Argument '%s' must not be 'zero'.", argName);
	}

	/**
	 * Returns a string for {@link IllegalArgumentException} due to passing a
	 * value doesn't lie between demanded values.
	 * 
	 * @param argName
	 *            the name of the argument which violates the rule
	 * @param fromInclusive
	 *            the minimal value (inclusive)
	 * @param toExclusive
	 *            the maximal value (exclusive)
	 * @return the exception message
	 * @throws IllegalArgumentException
	 *             if argument 'argName' is 'null'
	 */
	public static String getMessageForMustBetween(final String argName, final double fromInclusive, final double toExclusive) throws IllegalArgumentException {
		ExceptionMessages.checkArgument(argName);
		return ExceptionMessages.getMessageForMustBetween(argName, fromInclusive, toExclusive, true, false);
	}

	/**
	 * 
	 * @param argName
	 *            the name of the argument which violates the rule
	 * @param from
	 *            the minimal value
	 * @param to
	 *            the maximal value
	 * @param isFromInclusive
	 *            determines whether the minimal value is inclusive
	 * @param isToInlusive
	 *            determines whether the maximal value is inclusive
	 * @return the exception message
	 * @throws IllegalArgumentException
	 *             if argument 'argName' is 'null'
	 */
	public static String getMessageForMustBetween(final String argName, final double from, final double to, final boolean isFromInclusive,
			final boolean isToInlusive) throws IllegalArgumentException {
		ExceptionMessages.checkArgument(argName);
		final String fromInklusive = isFromInclusive ? "inclusive" : "exclusive";
		final String toInklusive = isFromInclusive ? "inclusive" : "exclusive";
		return String.format("Argument '%s' must be between %s (%s) and %s (%s).", argName, from, fromInklusive, to, toInklusive);
	}

	/**
	 * Checks whether the passed string argument isn't null.
	 * 
	 * @param argName
	 *            the argument to validate
	 * @throws IllegalArgumentException
	 *             if argument 'argName' is 'null'
	 */
	private static void checkArgument(final String argName) throws IllegalArgumentException {
		if(argName == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("param"));
		}
	}
}
