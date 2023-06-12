/**
 * Exception class for invalid number format
 */
public class NumberFormatException extends Exception{
    
    /**
     * Default constructor
     */
    public NumberFormatException(){}

    /**
     *  Exception constructor
     * @param message reason for error
     */
    public NumberFormatException(String message) {
        super(message);
    }
}
