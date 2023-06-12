import java.util.ArrayList;

/**
 * A class that starts the hiring assistant program by command argument
 * flags given by the user
 */
public class HRAssistant {

    private enum Roles {hr, applicant}; // Type of Roles 
    private ArrayList<String> commandArgs;
    private ArrayList<String> optionalFlags;
    private String mandatoryFlag;
    private boolean hasOptionalFlags;
    private boolean hasMandatoryFlag;
    private IOHandler ioHandler;

    // Flags Constants
    private final String FLAG_R = "-r";
    private final String FLAG_ROLE = "--role";
    private final String FLAG_A = "-a";
    private final String FLAG_APPLICATIONS = "--applications";
    private final String FLAG_J = "-j";
    private final String FLAG_JOBS = "--jobs";
    private final String FLAG_H = "-h";
    private final String FLAG_HELP = "--help";
    
    // Filepaths
    private String applicationFilePath;
    private String jobFilePath;

    // Default Filepaths
    private final String JOB_FILEPATH_DEFAULT = "jobs.csv";
    private final String APPLICATIONS_FILEPATH_DEFAULT = "applications.csv";

    private final String CSV_EXTENSION = ".csv";
    private final String UNDEFINED_ROLE = "ERROR: no role defined.";
    private final String HELP_TEXT = "HRAssistant - COMP90041 - Final Project\n\n" +
            "Usage: java HRAssistant [arguments]\n\n" +
            "Arguments:\n" +
            "    -r or --role            Mandatory: determines the user's role\n" +
            "    -a or --applications    Optional: path to applications file\n" +
            "    -j or --jobs            Optional: path to jobs file\n" +
            "    -h or --help            Optional: print Help (this message) and exit";

    public static void main(String[] args) {
        HRAssistant hrAssistant = new HRAssistant();
        hrAssistant.runProgram(args);
    }

    /**
     * Runs HRAssistant 
     * @param args command arguments given by user
     */
    private void runProgram(String[] args) {
        hasMandatoryFlag = false;
        this.hasOptionalFlags = false;
        this.ioHandler = new IOHandler();
        scanCommandArguments(args);
        checkValidArgument();
        loadFlags();
    }


    /**
     * Scans the command arguments
     * @param args entire command arguments given by user
     */
    private void scanCommandArguments(String[] args) {
        this.commandArgs = new ArrayList<String>();
        this.optionalFlags = new ArrayList<String>();
        for (String arg : args) {
            this.commandArgs.add(arg);
            scanFlags(arg);
        }
    }

    /**
     * Scan the argument for valid flags
     * @param args individual command argument given by user
     */
    private void scanFlags(String arg) {
    //todo: add check for repeated flags?
        if (arg.equals(FLAG_H) || arg.equals(FLAG_HELP)) {
            this.optionalFlags.add(arg);
            flagHelp();
            exitProgram();
        } else if (arg.equals(FLAG_J) || arg.equals(FLAG_JOBS)) {
            this.optionalFlags.add(arg);
        } else if (arg.equals(FLAG_A) || arg.equals(FLAG_APPLICATIONS)) {
            this.optionalFlags.add(arg);
        } else if (arg.equals(FLAG_R) || arg.equals(FLAG_ROLE)) {
            this.mandatoryFlag = arg;
        }
    }

    /**
     * Check if command argument given by user
     * is valid
     */
    private void checkValidArgument() {
        checkMandatoryFlags();
        checkOptionalFlags();
    }

    /**
     * Check if mandatory flag is given, and
     * exit program if no mandatory flag is in
     * the argument
     */
    private void checkMandatoryFlags() {
        if ((mandatoryFlag == null || mandatoryFlag.length() == 0 || !isValidMandatoryFlag(mandatoryFlag))) {
            flagHelp();
            exitProgram();
        } else {
            hasMandatoryFlag = true;
        }
    }

    /**
     * Check if argument has optional flags and validates the
     * optional flags
     */
    private void checkOptionalFlags() {
        if (optionalFlags.size() > 0) {
            hasOptionalFlags = true;
            for (String flag : optionalFlags) {
                if (!isValidOptionalFlag(flag) && !hasMandatoryFlag) {
                    flagHelp();
                    exitProgram();
                }
            }
        }
    }

    /**
     * Loads the mandatory flag and optional
     * flag if user has indicated them
     */
    private void loadFlags() {
        if (hasOptionalFlags) {
            loadOptionalFlags();
            loadMandatoryFlag();
        } else {
            loadDefaultOptionalFlags();
            loadMandatoryFlag();
        }
    }

    /**
     * Load up any optional flags given by user
     */
    private void loadOptionalFlags() {
        for (String flag: optionalFlags) {
            String flagArgument = getFlagArgument(flag);
            loadOptionalApplicationFlag(flag, flagArgument);
            loadOptionalJobFlag(flag, flagArgument);
        } 
    }

    /**
     * Check if job flag argument is valid, else load default job flag
     * @param flag optional flag
     * @param flagArgument optional flag argument
     */
    private void loadOptionalJobFlag(String flag, String flagArgument) {
        if ((flag.equals(FLAG_J) || flag.equals(FLAG_JOBS))) {
            flagJobs(flagArgument);
        } else {
            flagJobs(JOB_FILEPATH_DEFAULT);
        }
    }

    /**
     * Check if application flag argument is valid, else load default application flag
     * @param flag optional flag
     * @param flagArgument optional flag
     */
    private void loadOptionalApplicationFlag(String flag, String flagArgument) {
        if ((isValidOptionalFlag(flag)) && (flag.equals(FLAG_A) || flag.equals(FLAG_APPLICATIONS))) {
            flagApplications(flagArgument);
        } else {
            flagApplications(APPLICATIONS_FILEPATH_DEFAULT);
        }
    }

    /**
     * Load default optional flags
     */
    private void loadDefaultOptionalFlags() {
        flagJobs(JOB_FILEPATH_DEFAULT);
        flagApplications(APPLICATIONS_FILEPATH_DEFAULT);
    }

    /**
     * Loads mandatory flag
     */
    private void loadMandatoryFlag() {
        flagRole(getFlagArgument(mandatoryFlag));
    }
    
    /**
     * Initiates the Help flag which prints the help text
     */
    private void flagHelp() {
        System.out.println(HELP_TEXT);
    }

    /**
     * Initiates the Role flag which starts the respective role portals
     * @param flagArgument argument for role flag
     */
    private void flagRole(String flagArgument) {
        String roleType = flagArgument;
        switch (roleType)
        {
            case "applicant":
                Applicant applicant = new Applicant(jobFilePath, applicationFilePath);
                applicant.launchPortal();
                break;

            case "hr":
                HR hr = new HR(jobFilePath, applicationFilePath);
                hr.launchPortal();
                break;
        }
    }

    /**
     * Initiates the Application flag which assigns the filepath 
     * for the CSV file containing application info
     * @param flagArgument argument for application flag
     */
    private void flagApplications(String flagArgument) {
        ioHandler.checkFileExists(flagArgument, "applications");
        applicationFilePath = flagArgument;
    }
   
    /**
     * Initiates the Job flag which assigns the filepath 
     * for the CSV file
     * containing job info
     * @param flagArgument argument for job flag
     */
    private void flagJobs(String flagArgument) {
        ioHandler.checkFileExists(flagArgument, "jobs");
        jobFilePath = flagArgument;
    }
    
    /**
     * Prints output when role argument given is invalid
     * @param arg invalid role 
     */
    private void printInvalidRole(String arg) {
        System.out.printf("ERROR: %s is not a valid role.\n", arg);
    }

    /**
     * Check if optional flag is valid
     * @param flag optional flag
     * @return true if optional flag is valid
     */
    private boolean isValidOptionalFlag(String flag) {
        String flagArgument = getFlagArgument(flag);
        if (flagArgument.contains(CSV_EXTENSION)) {
            return true;
        } else {
            return false;
        }
    }

    // fix this

    /**
     * Check if mandatory flag is valid
     * @param flag mandatory flag
     * @return true if mandatory flag is valid
     */
    private boolean isValidMandatoryFlag(String flag) {
        int argIndex = getArgIndex(flag);
        if (isValidIndex(argIndex)) {
            String flagArgument = getFlagArgument(flag);
            for (Roles role : Roles.values()) {
                if (flagArgument.equals(role.toString())) {
                    return true;
                }
            }
        } else if (!isValidIndex(argIndex)){
            System.out.println(UNDEFINED_ROLE);
            return false;
        }
        String flagArgument = commandArgs.get(argIndex);
        printInvalidRole(flagArgument);
        return false;
    }

    /**
     * Get the flag argument
     * @param flag flag
     * @return argument that follows the flag
     */
    private String getFlagArgument(String flag) {
        int argIndex = getArgIndex(flag);
        return commandArgs.get(argIndex);
    }

    /**
     * Check if argument index is valid
     * @param index arg index
     * @return true if index is valid
     */
    private boolean isValidIndex(int index) {
        if (index < commandArgs.size()) {
            return true;
        }
        return false;
    }

    /**
     * Get the flag index
     * @param flag flag
     * @return flag index
     */
    private int getFlagIndex(String flag) {
        return commandArgs.indexOf(flag);
    }

    /**
     * Get the argument index (behind flag)
     * @param flag flag
     * @return argument index
     */
    private int getArgIndex(String flag) {
        int flagIndex = getFlagIndex(flag);
        return flagIndex + 1;
    }

    /**
     * Exits the program
     */
    private void exitProgram() {
        System.exit(0);
    }
}
