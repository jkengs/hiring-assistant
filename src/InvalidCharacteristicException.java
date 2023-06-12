/**
 * Exception class for invalid field values
 */
public class InvalidCharacteristicException extends Exception{
    
    /**
     * Default constructor
     */
    public InvalidCharacteristicException(){}

    /**
     *  Exception constructor
     * @param message reason for error
     */
    public InvalidCharacteristicException(String message) {
        super(message);
    }
}
