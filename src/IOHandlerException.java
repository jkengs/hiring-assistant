/**
 * Exception class for file input/output errors
 */
public class IOHandlerException extends Exception {
   
        /**
         * Default constructor
         */
        public IOHandlerException(){}
    
        /**
         *  Exception constructor
        * @param message reason for error
         */
        public IOHandlerException(String message) {
            super(message);
        }
}
    
