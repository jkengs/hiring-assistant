/**
 * Exception class for errors when reading mandatory data
 */
public class InvalidMandatoryDataException extends Exception{
    
    /**
     * Default constructor
     */
    public InvalidMandatoryDataException(){}

    /**
     *  Exception constructor
     * @param message reason for error
     */
    public InvalidMandatoryDataException(String message) {
        super(message);
    }
}