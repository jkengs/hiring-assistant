/**
 * Exception class for invalid data formats
 */
public class InvalidDataFormatException extends Exception{
    
    /**
     * Default constructor
     */
    public InvalidDataFormatException(){}

    /**
     *  Exception constructor
     * @param message reason for error
     */
    public InvalidDataFormatException(String message) {
        super(message);
    }
}
