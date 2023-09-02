package ggc.core;

import ggc.core.exception.InvalidDateValueException;

/**
 * Implements Date object
 */
public class Date {
    private static final long serialVersionUID = 202109192006L;

    /** Global value shared by all Date instances */
    private static int _globalValue;

    /** Local value for independent Date objects */
    private int _localValue;

    /**
     * Creates a new Date object with the supplied value
     * @param value
     */
    public Date(int value) {
        _localValue = value;
    }

    /**
     * Advances the global value a given number
     * @param value
     * @throws InvalidDateValueException
     */
    static void add(int value) throws InvalidDateValueException {
        if (value <= 0) {
            throw new InvalidDateValueException(value);
        }
        _globalValue += value;
    }

    /**
     * Set the global value to a given number
     * @param value number
     * @throws InvalidDateValueException
     */
    static void set(int value) throws InvalidDateValueException {
        if (value < 0) {
            throw new InvalidDateValueException(value);
        }
        _globalValue = value;
    }



    /**
     * Calculates the difference between two dates (local)
     * @param date Date to compare
     * @return absolute difference between dates 
     */
    
    public int difference(Date date) {
        return Math.abs(date.getValue() - _localValue);
    }

    /**
     * Get Date local value
     * @return local value
     */
    public int getValue() {
        return _localValue;
    }

    /**
     * Get the "Global" Date object
     * @return Date object
     */
    public static Date now() {
        return new Date(_globalValue);
    }
}
