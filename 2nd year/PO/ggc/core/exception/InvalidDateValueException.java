package ggc.core.exception;

public class InvalidDateValueException extends Exception {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 201708301010L;

    /** Date value */
    private int _value;

    /**
     * @param value
     */
    public InvalidDateValueException(int value) {
        _value = value;
    }
    
    /**
     * @return date value
     */
    public int getValue() {
        return _value;
    }
}
