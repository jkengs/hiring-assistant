import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * A class that represents the portal for the HR team which allows
 * them to create a new job, filter and list applications
 */
public class HR extends InteractiveRole {

    private Scanner keyboard;
    private IOHandler ioHandler;
    private String input;
    private int applicationCount;
    private Job job;

    private final int ALPHABET_MAX_INDEX = 25;              // Maximum Index of Alphabet (A-Z)

    // Filepaths
    private String jobFilePath;
    private String applicationFilePath;
    private final String WELCOME_HR_FILEPATH = "welcome_hr.ascii";

    // Data parsed from the CSV files
    private ArrayList<String[]> masterJobData;             
    private ArrayList<String[]> masterApplicantData;        

    private ArrayList<Job> masterJobList;                   
    private ArrayList<Application> masterApplicantList;     // includes all applicants
    private ArrayList<Application> masterApplicationList;   // includes only applicants that applied for a job

    // Filter Type Constants
    public final static String FILTER_LASTNAME = "lastname";
    public final static String FILTER_DEGREE = "degree";
    public final static String FILTER_WAM = "wam";

    // Command Input Constants
    private final String COMMAND_APPLICANTS = "applicants";
    private final String COMMAND_A = "a";
    private final String COMMAND_FILTER = "filter";
    private final String COMMAND_F = "f";
    private final String COMMAND_MATCH = "match";
    private final String COMMAND_M = "m";
    
    // Creating New Job Prompts
    private final String PROMPT_TITLE = "Position Title: ";
    private final String PROMPT_DESCRIPTION = "Position Description: ";
    private final String PROMPT_DEGREE = "Minimum Degree Requirement: ";
    private final String PROMPT_SALARY = "Salary ($ per annum): ";
    private final String PROMPT_START_DATE = "Start Date: ";
    private final String PROMPT_SELECT_FILTER = "Filter by: [lastname], [degree] or [wam]: ";

    // Error Messages
    private final String SAVE_JOB_FILE_ERROR = "Unable to find jobs file.";

    // Menu Instructions
    private final String TITLE_INVALID = "Ooops! Position Title must be provided: ";
    private final String APPLICANTS_UNAVAILABLE = "No applicants available.";
    private final String JOB_CREATE_NEW = "# Create new Job";
    private final String MENU_HR_INSTRUCTION = "Please enter one of the following commands to continue:\n" +
            "- create new job: [create] or [c]\n" +
            "- list available jobs: [jobs] or [j]\n" +
            "- list applicants: [applicants] or [a]\n" +
            "- filter applications: [filter] or [f]\n" +
            "- matchmaking: [match] or [m]\n" +
            "- quit the program: [quit] or [q]";

    /**
     *  Default HR constructor
     */
    public HR() {
    }

    /**
     * HR  constructor
     * @param jobFilePath, file path for jobs.csv
     * @param applicationFilePath, file path for jobs.csv
     */
    public HR(String jobFilePath, String applicationFilePath) {
        this.jobFilePath = jobFilePath;
        this.applicationFilePath = applicationFilePath;
        this.ioHandler = new IOHandler();
        this.keyboard = new Scanner(System.in);

        this.masterJobData = new ArrayList<String[]>();
        this.masterApplicantData = new ArrayList<String[]>();

        this.masterJobList = new ArrayList<Job>();
        this.masterApplicantList = new ArrayList<Application>();
        this.masterApplicationList = new ArrayList<Application>();
    }

    /**
     * Launches the program for applicant role
     */
    public void launchPortal() {

        ioHandler.displayWelcomeMessage(WELCOME_HR_FILEPATH);
        checkSave();
        updateApplicationCount();
        printStatus();
        System.out.print(PROMPT_TEXT);
        menuInput();
    }


    /**
     * Checks if there are saved files and loads them if so
     */
    public void checkSave() {
        if (hasSavedFile()) {
            loadSave();             // Load Jobs with received application data 
        } else {
            readMasterJobData();
            loadMasterJobList();
        }
        // Loads applicant data (regardless of job applied)
        readMasterApplicantData();
        loadMasterApplicantList();
    }

    /**
     * Reads application data from CSV
     */
    private void readMasterApplicantData() {
        try {
            masterApplicantData = ioHandler.readFile(applicationFilePath, Application.DATA_FIELD_NO, IOHandler.APPLICATION_FILE_TYPE);
        } catch (IOHandlerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Read jobs data from CSV file
     */
    private void readMasterJobData() {
        try {
            masterJobData = ioHandler.readFile(jobFilePath, Job.DATA_FIELD_NO, IOHandler.JOB_FILE_TYPE);
        } catch (IOHandlerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loads application data
     */
    private void loadMasterApplicantList() {
        for (String[] applicationData : masterApplicantData) {
            int applicationLineNo = masterApplicantData.indexOf(applicationData) + 1;
            try {
                Application application = new Application(applicationData, applicationLineNo);
                masterApplicantList.add(application);
            } catch (InvalidMandatoryDataException e) {
                // Skips line row if invalid mandatory fields in the row
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Loads job data
     */
    private void loadMasterJobList() {
        for (String[] jobData : masterJobData) {
            int jobLineNo = masterJobData.indexOf(jobData) + 1;
            try {
                Job job = new Job(jobData, jobLineNo);
                masterJobList.add(job);
            } catch (InvalidMandatoryDataException e) {
                // Skip line if invalid mandatory line in row
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Reads the saved file and loads the objects - jobs
     * with received application data
     */
    private void loadSave() {
        masterJobList.clear();
        try {
            ArrayList<?> objectList = ioHandler.readSave();
            for (Object obj : objectList) {
                masterJobList.add((Job) obj);
            }
        } catch (IOHandlerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates application count
     */
    private void updateApplicationCount() {
        int applicationCounter = 0;
        for (Job job : masterJobList) {
            applicationCounter += job.getApplicationCount();
        }
        this.applicationCount = applicationCounter;
    }

    /**
     * Receives users command and initiates if valid
     */
    private void menuInput() {
        this.input = keyboardInput();
        switch (input) {
            case COMMAND_CREATE:
            case COMMAND_C:
                createJob();
                printStatus();
                System.out.print(PROMPT_TEXT);
                menuInput();
                break;

            case COMMAND_JOBS:
            case COMMAND_J:
                listAvailableJobs();
                printStatus();
                System.out.print(PROMPT_TEXT);
                menuInput();
                break;

            case COMMAND_APPLICANTS:
            case COMMAND_A:
                listApplicants();
                printStatus();
                System.out.print(PROMPT_TEXT);
                menuInput();
                break;

            case COMMAND_FILTER:
            case COMMAND_F:
                filterApplications();
                printStatus();
                System.out.print(PROMPT_TEXT);
                menuInput();
                break;

            case COMMAND_MATCH:
            case COMMAND_M:
                match();
                printStatus();
                System.out.print(PROMPT_TEXT);
                menuInput();
                break;

            case COMMAND_QUIT:
            case COMMAND_Q:
                closeInput();
                saveData();
                System.out.println();
                System.exit(0);
                break;

            default:
                printInvalidCommand();
                System.out.print(PROMPT_TEXT);
                menuInput();
                break;
        }
    }

    /**
     * Creates a new job and saves it
     */
    private void createJob() {
        this.job = new Job();
        System.out.println(JOB_CREATE_NEW);
        promptTitle();
        promptDescription();
        promptDegree();
        promptSalary();
        promptStartDate();
        job.setCreatedAt();
        masterJobList.add(job);
        saveJobToCSV();
    }

    /**
     * Prompt for Job Title
     */
    private void promptTitle() {
        System.out.print(PROMPT_TITLE);
        input = keyboardInput();
        while (isEmptyInput(input)) {
            System.out.print(TITLE_INVALID);
            input = keyboardInput();
        }
        job.setTitle(input.trim());
    }

    /**
     * Prompt for Job Description
     */
    private void promptDescription() {
        System.out.print(PROMPT_DESCRIPTION);
        // trim?
        job.setDescription(keyboardInput());
    }

    /**
     * Prompt for Job Degree
     */
    private void promptDegree() {
        System.out.print(PROMPT_DEGREE);
        String degree = keyboardInput().trim();
        while (!(degree.equals(Application.DEGREE_BACHELOR) || degree.equals(Application.DEGREE_MASTER) || degree.equals(Application.DEGREE_PHD)
                || isEmptyInput(input))) {
            printInvalidOptionalField("Minimum Degree Requirement");
            degree = keyboardInput();
        }
        job.setDegree(degree);
    }

    /**
     * Prompt for Job Salary
     */
    private void promptSalary() {
        System.out.print(PROMPT_SALARY);
        boolean isValid = false;
        int salary;
        while (!isValid) {
            input = keyboardInput();
            try {
                salary = Integer.parseInt(input);
                while (!(salary > 0)) {
                    printInvalidOptionalField("Salary");
                    input = keyboardInput();
                    salary = Integer.parseInt(input);
                }
                isValid = true;
                job.setSalary(salary);
            } catch (Exception e) {
                if (isEmptyInput(input)) {
                    isValid = true;
                    job.setSalary(0);
                } else {
                    printInvalidOptionalField("Salary");
                }
            }
        }
    }

    /**
     * Prompt for Job Start Date
     */
    private void promptStartDate() {
        System.out.print(PROMPT_START_DATE);
        boolean isValid = false;
        LocalDate date;
        while (!isValid) {
            input = keyboardInput();
            try {
                date = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yy"));
                isValid = true;
                job.setStartDate(date);
            } catch (Exception e) {
                printInvalidOptionalField("Start Date");
            }
        }
    }
   
    /**
     * Save new job to CSV
     */
    private void saveJobToCSV() {
        File jobFile = new File(jobFilePath);
        try {
            FileWriter fr = new FileWriter(jobFile, true);
            PrintWriter csvWriter = new PrintWriter(fr);
            csvWriter.write(job.getCSVFormat());
            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            System.out.println(SAVE_JOB_FILE_ERROR);
        }
    }
    
    /**
     * Filters Application List
     */
    private void filterApplications() {
        updateApplicationList();
        promptFilter();
        masterApplicationList.clear();
    }

    /**
     * Prompts Filter Type
     */
    private void promptFilter() {
        System.out.print(PROMPT_SELECT_FILTER);
        String filter = keyboardInput();
        switch (filter) {
            case FILTER_LASTNAME:
                if (hasApplications()) {
                    filterBy(FILTER_LASTNAME);
                } else {
                    System.out.println(APPLICANTS_UNAVAILABLE);
                }
                break;

            case FILTER_DEGREE:
                if (hasApplications()) {
                    filterBy(FILTER_DEGREE);
                } else {
                    System.out.println(APPLICANTS_UNAVAILABLE);
                }
                break;

            case FILTER_WAM:
                if (hasApplications()) {
                    filterBy(FILTER_WAM);
                } else {
                    System.out.println(APPLICANTS_UNAVAILABLE);
                }
                break;

            default:
                break;
        }

    }

    /**
     * Filter by a type
     * @param filterType filter type
     */
    private void filterBy(String filterType) {
        ArrayList<Application> sortedList = masterApplicationList;
        Collections.sort(sortedList, new Comparator<Application>() {
            @Override
            public int compare(Application thisApplication, Application otherApplication) {
                return thisApplication.filterBy(filterType,otherApplication);
            }
        });
        for (Application application : sortedList) {
            printApplicantDetails(application, getApplicationListIndex(application));
        }
    }

    /**
     * Gets index of application (applicants who applied)
     * @param application application
     * @return the index
     */
    private int getApplicationListIndex(Application application) {
        return masterApplicationList.indexOf(application) + 1;
    }
    /**
     * Update the application list with no duplicate ensured
     * by using a unique key
     */
    private void updateApplicationList() {
        ArrayList<Integer> hashSet = new ArrayList<Integer>();
        for (Job job : masterJobList) {
            // For each Job
            if (job.hasApplications()) {
                // If the Job has received applications
                ArrayList<String[]> receivedApplicationData = job.getReceivedApplications();
                for (String[] data : receivedApplicationData) {
                    // For each Application
                    Application application = new Application(data);
                    int hashCode = application.hashCode();
                    if (!hashSet.contains(hashCode)) {
                        // If not duplicate application
                        masterApplicationList.add(application);
                        hashSet.add(hashCode);
                    }
                }
            }
        }
    }

    /**
     * Cheecks if there are applications
     * @return true if applicationos present
     */
    private boolean hasApplications() {
        if (masterApplicationList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

     /**
     * List applicants in sorted order
     */
    private void listApplicants() {
        ArrayList<Application> sortedList = masterApplicantList;
        // Sorts applicants by their availability
        Collections.sort(sortedList, new Comparator<Application>() {
            @Override
            public int compare(Application thisApplication, Application otherApplication) {
                return thisApplication.sortApplicantList(otherApplication);
            }
        });
        if (masterApplicantList.size() != 0) {
            // Prints each application details
            for (Application application : sortedList) {
                printApplicantDetails(application, getApplicantListIndex(application));
            }
        } else {
            System.out.println(APPLICANTS_UNAVAILABLE);
        }
    }

    /**
     * Prints applicant details
     * @param application application
     * @param applicantIndex index of application in the list
     */
    private void printApplicantDetails(Application application, int applicantIndex) {
        System.out.printf("[%s] %s, %s (%s): %s. Salary Expectations: %s. Available: %s\n", applicantIndex,
                application.getLastName(), application.getFirstName(), application.getDegree(),
                application.getCareerSummary(),
                application.getSalaryExpectations(), application.getFormattedAvailability());
    }

    /**
     * Gets index of applicant
     * @param application application
     * @return the index
     */
    private int getApplicantListIndex(Application application) {
        return masterApplicantList.indexOf(application) + 1;
    }

    
    /**
     * List available jobs
     */
    private void listAvailableJobs() {
        if (masterJobList.size() == 0) {
            System.out.println(JOBS_UNAVAILABLE);
        } else {
            for (Job job : masterJobList) {
                printJobDetails(job);
                System.out.println();
                if (job.hasApplications()) {
                    printReceivedApplicationDetails(job);
                }
            }
        }
    }

    /**
     * Print job details
     * @param job
     */
    private void printJobDetails(Job job) {
        int jobIndex = getJobIndex(job);
        System.out.printf("[%d] %s (%s). %s. Salary: %s. Start Date: %s.", jobIndex, job.getTitle(),
                job.getDescription(), job.getDegree(), job.getSalary(), job.getStartDate());
    }

    /**
     * Get job index
     * @param job
     * @return int
     */
    private int getJobIndex(Job job) {
        return masterJobList.indexOf(job) + 1;
    }

    /**
     * List available jobs
     * @param job job
     * @return list of applicants that applied
     */
    private ArrayList<Application> getReceivedApplicationList(Job job) {
        ArrayList<String[]> receivedApplicationsData = job.getReceivedApplications();
        ArrayList<Application> receivedApplications = new ArrayList<Application>();
        for (String[] data : receivedApplicationsData) {
            receivedApplications.add(new Application(data));
        }
        return receivedApplications;
    }

    /**
     * Print details who applicants who applied for jobs
     * @param job job
     */
    private void printReceivedApplicationDetails(Job job) {
        ArrayList<Application> receivedApplications = getReceivedApplicationList(job);
        boolean addCounter = false;
        int applicationNo = 0;
        int numberCounter = 0;
        for (Application application : receivedApplications) {
            if (addIndexCounter(applicationNo)) {
                // If
                applicationNo = 0;
                addCounter = true;
                numberCounter += 1;
            }
            String applicationIndex = getApplicationIndex(applicationNo, numberCounter, addCounter);
            System.out.printf("    [%s] %s, %s (%s): %s. Salary Expectations: %s. Available: %s\n", applicationIndex,
                    application.getLastName(), application.getFirstName(), application.getDegree(),
                    application.getCareerSummary(),
                    application.getSalaryExpectations(), application.getFormattedAvailability());
            applicationNo += 1;
        }
    }

     /**
     * Checks if a counter should be added beside index
     * @param applicationNo number of application currently
     * @return true if it surpassed last alphabet 'z'
     */
    private boolean addIndexCounter(int applicationNo) {;
        if (applicationNo > ALPHABET_MAX_INDEX) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets application index
     * @param applicationNo number of application currently
     * @param counter counter beside index
     * @param addCounter true if counter is to be added
     * @return true if it surpassed last alphabet 'z'
     */
    private String getApplicationIndex(int applicationNo, int counter, boolean addCounter) {
        String applicationIndex;
        final char[] number = "0123456789".toCharArray();
        final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        applicationIndex = Character.toString(alphabet[applicationNo]).toLowerCase();
        if (addCounter) {
            String numberCounter = Character.toString(number[applicationNo]).toLowerCase();
            return applicationIndex + numberCounter;
        }
        return applicationIndex;
    }

    /**
     * Prints menu status
     */
    private void printStatus() {
        // System.out.printf("0 applications received.\n");
        System.out.printf("%d applications received.\n", applicationCount);
        System.out.println(MENU_HR_INSTRUCTION);
    }

    /**
     * Keyboard input
     * @return input string
     */
    private String keyboardInput() {
        return keyboard.nextLine();
    }

    /**
     * Close scanner
     */
    private void closeInput() {
        keyboard.close();
    }

    /**
     * Prints invalid commands
     */
    private void printInvalidCommand() {
        System.out.println(MENU_INVALID_COMMAND);
    }

    
     /**
     * Saves user preferences if any to
     * a permanent storage option
     */
    private void saveData() {
        try {
            ioHandler.saveData(masterJobList);
        } catch (IOHandlerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Matchmaking
     */
    private void match() {
        Matchmaker matchmaker = new Matchmaker(masterJobList);
        matchmaker.startProgram();
    }
}
