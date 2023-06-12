import java.io.File;

/**
 * An abstract class that represents roles that are interactive (requires user input etc.), which includes
 * the HR class and Applicant class
 */
public abstract class InteractiveRole {
    
    protected final String CSV_DELIMITER = ",";

    // Menu Command Constants
    protected final String PROMPT_TEXT = "> ";
    protected final String COMMAND_JOBS = "jobs";
    protected final String COMMAND_J = "j";
    protected final String COMMAND_CREATE = "create";
    protected final String COMMAND_C = "c";
    protected final String COMMAND_QUIT = "quit";
    protected final String COMMAND_Q = "q";

    // Menu Output Constants
    protected final String MENU_INVALID_COMMAND = "Invalid input! Please enter a valid command to continue: ";
    protected final String JOBS_UNAVAILABLE = "No jobs available.";

    /**
     * Default InteractiveRole Constructor
     */
    public InteractiveRole(){};


    /**
     * Check if there are saved files
     * @return true if has saved file
     */
    protected boolean hasSavedFile() {
        File saveFile = new File(IOHandler.DATA_SAVE_FILEPATH);
        if (saveFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if input string is empty
     * @param input string
     * @return true if string is empty
     */
    protected boolean isEmptyInput(String input) {
        if (input.trim().length() == 0 || input == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes whitespaces from input string
     * @param input string
     * @return formatted string
     */
    protected String removeWhiteSpace(String input) {
        return input.trim();
    }

    /**
     * Prints the output when invalid input is entered for
     * optional fields
     * @param fieldType type of data field
     */
    protected void printInvalidOptionalField(String fieldType) {
        System.out.printf("Invalid input! Please specify %s: ", fieldType);
    }
}