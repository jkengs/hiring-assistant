import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;

/**
 * A class that represents the portal for applicants which allows
 * them to create an application and apply for jobs
 */

public class Applicant extends InteractiveRole {

    // IO Handlers
    private IOHandler ioHandler;
    private Scanner keyboard;
    private String input;

    // Filepaths
    private String jobFilePath;
    private String applicationFilePath;
    private final String WELCOME_APPLICANT_FILEPATH = "welcome_applicant.ascii";

    private boolean applicationCreated; // true if applicant created an application (profile)
    private Application application;

    // Status Counters
    private int availableJobCount;
    private int applicationCount;

    // Data Lists
    private ArrayList<String[]> jobData;
    private ArrayList<Job> masterJobList;
    private ArrayList<Job> appliedJobList;
    private ArrayList<Job> availableJobList;

    // Default Int Values
    private final int DEFAULT_APPLICATION_COUNT = 0;
    private final int BLANK_INPUT_VALUE = 0;

    // Creating New Application Prompts
    private final String PROMPT_LASTNAME = "Lastname: ";
    private final String PROMPT_FIRSTNAME = "Firstname: ";
    private final String PROMPT_CAREER_SUMMARY = "Career Summary: ";
    private final String PROMPT_AGE = "Age: ";
    private final String PROMPT_GENDER = "Gender: ";
    private final String PROMPT_HIGHEST_DEGREE = "Highest Degree: ";
    private final String PROMPT_COURSEWORK = "Coursework: ";
    private final String PROMPT_SALARY_EXPECTATIONS = "Salary Expectations ($ per annum): ";
    private final String PROMPT_AVAILABILITY = "Availability: ";

    // Data Field Types
    private final String COURSEWORK_FIELD = "Coursework";
    private final String HIGHEST_DEGREE_FIELD = "Highest Degree";
    private final String GENDER_FIELD = "Gender";
    private final String SALARY_EXPECTATION_FIELD = "Salary Expectation";
    private final String AVAILABILITY_FIELD = "Availability";

    // Invalid Input Messages
    private final String JOBS_INVALID_INPUT = "Invalid input! Please enter a valid number to continue: ";
    private final String APPLICATION_CREATE_NEW = "# Create new Application";
    private final String LASTNAME_INVALID = "Ooops! Lastname must be provided: ";
    private final String FIRSTNAME_INVALID = "Ooops! Firstname must be provided: ";
    private final String AGE_INVALID = "Ooops! A valid age between 18 and 100 must be provided: ";

    // Error Messages
    private final String SAVE_APPLICATION_FILE_ERROR = "Unable to find application file.";
    
    // Menu Instructions
    private final String MENU_APPLICANT_INSTRUCTION = "Please enter one of the following commands to continue:\n" +
            "- create new application: [create] or [c]\n" +
            "- list available jobs: [jobs] or [j]\n" +
            "- quit the program: [quit] or [q]";
    private final String MENU_APPLICATION_CREATED_INSTRUCTION = "Please enter one of the following commands to continue:\n" +
            "- list available jobs: [jobs] or [j]\n" +
            "- quit the program: [quit] or [q]";
    private final String MENU_JOB_APPLY_INSTRUCTION = "Please enter the jobs you would like to apply for (multiple options are possible): ";
    
    /**
     * Applicant default constructor
     */
    public Applicant() {};

    /**
     * Applicant  constructor
     * @param jobFilePath, file path for jobs.csv
     * @param applicationFilePath, file path for jobs.csv
     */
    public Applicant(String jobFilePath, String applicationFilePath) {
        this.jobFilePath = jobFilePath;
        this.applicationFilePath = applicationFilePath;
        this.ioHandler = new IOHandler();

        applicationCount = DEFAULT_APPLICATION_COUNT;
        applicationCreated = false;

        keyboard = new Scanner(System.in);
        jobData = new ArrayList<String[]>();
        masterJobList = new ArrayList<Job>();
        appliedJobList = new ArrayList<Job>();
        availableJobList = new ArrayList<Job>();
    }

    /**
     * Launches the program for applicant role
     */
    public void launchPortal() {
        ioHandler.displayWelcomeMessage(WELCOME_APPLICANT_FILEPATH);
        checkSave();
        updateJobCount();
        printStatus();
        System.out.print(PROMPT_TEXT);
        menuInput();
      
    }

    /**
     * Checks for any saved file from previous runs of the program
     */
    public void checkSave() {
        if (hasSavedFile()) {
            loadSave();
        } else {
            readJobData();
            loadJobList();
        }
    }

    /**
     * Loads the saved file if present
     */
    private void loadSave() {
        masterJobList.clear();
        availableJobList.clear();
        try {
            ArrayList<?> objectList = ioHandler.readSave();
            for (Object obj : objectList) {
                Job job = (Job) obj;
                masterJobList.add(job);
                availableJobList.add(job);
                }
            } catch (IOHandlerException e ) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Reads job data in csv format
     */
    private void readJobData() {

        try {
            jobData = ioHandler.readFile(jobFilePath, Job.DATA_FIELD_NO, IOHandler.JOB_FILE_TYPE);
        } catch (IOHandlerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Load job data
     */
    private void loadJobList() {
        for (String[] jobDataLine : jobData) {
            // For each data line
            int jobLineNo = jobData.indexOf(jobDataLine) + 1;   // Line number in CSV file
            try {
                Job job = new Job(jobDataLine, jobLineNo);
                availableJobList.add(job);
                masterJobList.add(job);
            } catch (InvalidMandatoryDataException e) {
                // Skips line if mandatory field is not valid
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Updates available jobs count
     */
    private void updateJobCount() {
        availableJobCount = availableJobList.size();
    }

    /**
     * Updates application count
     */
    private void updateApplicationCount() {
        applicationCount = appliedJobList.size();
    }

    /**
     * Prints menu text
     */
    private void printStatus() {
        System.out.printf("%d jobs available. %d applications submitted.\n", availableJobCount, applicationCount);
        if (applicationCreated) {
            System.out.println(MENU_APPLICATION_CREATED_INSTRUCTION);
        } else {
            // If no application is created
            System.out.println(MENU_APPLICANT_INSTRUCTION);
        }
    }

    /**
     * Receives users command and initiates if valid
     */
    private void menuInput() {
        this.input = keyboardInput();
        switch (input) {

            //add try catch?
            case COMMAND_CREATE:
            case COMMAND_C:
                if (!applicationCreated) {
                    createApplication();
                    printStatus();
                    System.out.print(PROMPT_TEXT);
                    menuInput();
                    break;
                } else {
                    printStatus();
                    System.out.print(PROMPT_TEXT);
                    menuInput();
                    break;
                }

            case COMMAND_JOBS:
            case COMMAND_J:
                if (!applicationCreated) {
                    listAvailableJobs();
                    printStatus();
                    System.out.print(PROMPT_TEXT);
                    menuInput();
                    break;
                } else {
                    listAvailableJobs();
                    if (hasAvailableJobs()) {
                        promptApplyJob();
                    }
                    printStatus();
                    System.out.print(PROMPT_TEXT);
                    menuInput();
                    break;
                }

            case COMMAND_QUIT:
            case COMMAND_Q:
                closeInput();
                saveJobPreferences();
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
     * Creates a new application for the applicant
     */
    private void createApplication() {
        this.application = new Application();
        System.out.print(APPLICATION_CREATE_NEW);
        System.out.println();
        promptLastName();
        promptFirstName();
        promptCareerSummary();
        promptAge();
        promptGender();
        promptHighestDegree();
        promptCourseGrades();
        promptSalaryExpectations();
        promptAvailability();
        timeStampApplication(application);
        applicationCreated = true;
        saveApplication();
    }

    /**
     * Asks user for their last name (mandatory)
     * and saves it to the newly created application
     */
    private void promptLastName() {
        System.out.print(PROMPT_LASTNAME);
        input = keyboardInput();
        while (isEmptyInput(input)) {
            System.out.print(LASTNAME_INVALID);
            input = keyboardInput();
        }
        application.setLastName(removeWhiteSpace(input));

    }

    /**
     * Asks user for their first name (mandatory)
     * and saves it to the newly created application
     */
    private void promptFirstName() {
        System.out.print(PROMPT_FIRSTNAME);
        input = keyboardInput();
        while (isEmptyInput(input)) {
            System.out.print(FIRSTNAME_INVALID);
            input = keyboardInput();
        }
        application.setFirstName(removeWhiteSpace(input));
    }

    /**
     * Asks user for their career summary (optional)
     * and saves it to the newly created application
     */
    private void promptCareerSummary() {
        System.out.print(PROMPT_CAREER_SUMMARY);
        input = keyboardInput();
        application.setCareerSummary(removeWhiteSpace(input));
    }

    /**
     * Asks user for their age (mandatory)
     * and saves it to the newly created application
     */
    private void promptAge() {
        System.out.print(PROMPT_AGE);
        boolean isValid = false;
        int age;
        while (!isValid) {
            input = keyboardInput();
            try {
                age = Integer.parseInt(input.trim());
                if (!(age < Application.AGE_UPPER_LIMIT && age > Application.AGE_LOWER_LIMIT)) {
                    System.out.print(AGE_INVALID);
                    
                } else {
                    isValid = true;
                    application.setAge(age); 
                }
               
            } catch (Exception e) {
                //System.out.println();
                System.out.print(AGE_INVALID);
                
            }  
        }
    }


    /**
     * Asks user for their gender (optional)
     * and saves it to the newly created application
     */
    private void promptGender() {
        //accept whitespaces/empty
        System.out.print(PROMPT_GENDER);
        String input = keyboardInput().trim();
        while (!(input.equals(Application.GENDER_FEMALE) || input.equals(Application.GENDER_MALE) || input.equals(Application.GENDER_OTHER) || isEmptyInput(input))) {
            printInvalidOptionalField(GENDER_FIELD);
            input = keyboardInput();
        }
        application.setGender(input);
    }

    /**
     * Asks user for their highest degree (optional)
     * and saves it to the newly created application
     */
    private void promptHighestDegree() {
        //accept whitespaces/empty
        System.out.print(PROMPT_HIGHEST_DEGREE);
        String input = keyboardInput().trim();
        while (!(input.equals(Application.DEGREE_PHD) || input.equals(Application.DEGREE_MASTER) || input.equals(Application.DEGREE_BACHELOR) || isEmptyInput(input))) {
            printInvalidOptionalField(HIGHEST_DEGREE_FIELD);
            input = keyboardInput();
        }
        application.setHighestDegree(input);
    }

    /**
     * Asks user for their course grades (MIT Core Subjects)
     * and saves it to the newly created application
     */
    private void promptCourseGrades() {
        System.out.println(PROMPT_COURSEWORK);
        promptSubjectGrade(Application.SUBJECT_CODE_JAVA);
        promptSubjectGrade(Application.SUBJECT_CODE_ALGORITHMS);
        promptSubjectGrade(Application.SUBJECT_CODE_IT);
        promptSubjectGrade(Application.SUBJECT_CODE_DB);
    }

    /**
     * Asks user for their individual subject grades (optional)
     * and saves it to the newly created application
     * @param subjectCode subject code
     */
    private void promptSubjectGrade(String subjectCode) {
        System.out.printf("- %s: ", subjectCode);
        boolean isValid = false;
        int grade;
        while (!isValid) {
            input = keyboardInput();
            try {
                grade = Integer.parseInt(input);
                while (!(grade >= Application.GRADE_MINIMUM && grade <= Application.GRADE_MAXIMUM)) {
                    printInvalidOptionalField(COURSEWORK_FIELD);
                    input = keyboardInput();
                    grade = Integer.parseInt(input);
                }
                isValid = true;
                application.setGrade(grade, subjectCode);
            } catch (Exception e) {
                if (isEmptyInput(input)) {
                    isValid = true;
                    // check logic
                    application.setGrade(BLANK_INPUT_VALUE, subjectCode);
                } else {
                    printInvalidOptionalField(COURSEWORK_FIELD);
                }
            }
        }
    }

    /**
     * Asks user for their salary expectations (optional)
     * and saves it to the newly created application
     */
    private void promptSalaryExpectations() {
        System.out.print(PROMPT_SALARY_EXPECTATIONS);
        boolean isValid = false;
        int salary;
        while (!isValid) {
            input = keyboardInput();
            try {
                salary = Integer.parseInt(input);
                while (!(salary >= Application.SALARY_EXPECTATION_MINIMUM)) {
                    printInvalidOptionalField(SALARY_EXPECTATION_FIELD);
                    input = keyboardInput();
                    salary = Integer.parseInt(input);
                }
                isValid = true;
                application.setSalaryExpectations(salary);
            } catch (Exception e) {
                if (isEmptyInput(input)) {
                    isValid = true;
                    application.setSalaryExpectations(BLANK_INPUT_VALUE);
                } else {
                    printInvalidOptionalField(SALARY_EXPECTATION_FIELD);
                }
            }
        }
    }

    /**
     * Asks user for their availability (optional)
     * and saves it to the newly created application
     */
    private void promptAvailability() {
        System.out.print(PROMPT_AVAILABILITY);
        boolean isValid = false;
        LocalDate date;
        while (!isValid) {
            input = keyboardInput().trim();
            LocalDate currentDate = LocalDate.now();    // Current Date
            try {
                date = LocalDate.parse(input, DateTimeFormatter.ofPattern(Application.ALLOWED_DATE_PATTERN));
                while (!date.isAfter(currentDate) && !date.isEqual(currentDate)) {
                    // Date is not valid 
                    printInvalidOptionalField(AVAILABILITY_FIELD);
                    input = keyboardInput();
                    date = LocalDate.parse(input, DateTimeFormatter.ofPattern(Application.ALLOWED_DATE_PATTERN));
                }
                isValid = true;
                application.setAvailability(date);
            } catch (Exception e) {
                if (isEmptyInput(input)) {
                    // If input is blank
                    isValid = true;
                    application.setBlankAvailability();
                } else {
                    printInvalidOptionalField(AVAILABILITY_FIELD);
                }
            }
        }
    }

    /**
     * Timestamps the creation of the new application
     * in UNIX format
     * @param application application
     */
    private void timeStampApplication(Application application) {
        application.setCreatedAt();
    }

    /**
     * Saves the newly created application to the
     * applications file (applications.csv by default)
     */
    private void saveApplication() {
        File applicationFile = new File(applicationFilePath);
        try {
            FileWriter fr = new FileWriter(applicationFile, true);
            PrintWriter csvWriter = new PrintWriter(fr);
            csvWriter.write(application.getCSVFormat());
            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            System.out.println(SAVE_APPLICATION_FILE_ERROR);
        }
    }

    /**
     * Lists available jobs that user can apply to
     */
    private void listAvailableJobs() {
        if (availableJobList.size() > 0) {
            printAvailableJobs();
        } else {
            System.out.println(JOBS_UNAVAILABLE);
        }
    }

    /**
     * Prints available jobs details
     */
    private void printAvailableJobs() {
        //maybe repeated? redundant
        for (Job job : availableJobList) {
            int jobIndex = availableJobList.indexOf(job) + 1;
            System.out.printf("[%d] %s (%s). %s. Salary: %s. Start Date: %s.", jobIndex, job.getTitle(),
                job.getDescription(), job.getDegree(), job.getSalary(), job.getStartDate());
            // line?
            System.out.println();
        }
    }

    /**
     * Asks user which job options they wish to apply to
     * and apply to them if valid
     */
    private void promptApplyJob() {
        System.out.print(MENU_JOB_APPLY_INSTRUCTION);
        boolean isValid = false;
        while (!isValid) {
            try {
                input = keyboardInput();
                String[] selectedOptions = input.trim().split(CSV_DELIMITER);
                if (isValidJobOptions(selectedOptions)) {
                    // If selected options are valid
                    selectedOptions = removeDuplicationOptions(selectedOptions);        // Remove repeated duplicate selections (i.e. 1,1,1)
                    updateAppliedJobs(selectedOptions);
                    updateAvailableJobs();
                    updateJobCount();
                    updateApplicationCount();
                    isValid = true;
                } else {
                    System.out.print(JOBS_INVALID_INPUT);
                }
            } catch (Exception e) {
                if (isEmptyInput(input)) {
                    // If blank input, proceed
                    isValid = true;
                } else {
                    System.out.print(JOBS_INVALID_INPUT);
                }
            }
        }
    }

    /**
     * Remove duplicate job options selected by user
     * @param selectedOptions array of job options
     */
    private String[] removeDuplicationOptions(String[] selectedOptions) {
        
        LinkedHashSet<String> tempSelectedOptions = new LinkedHashSet<String>(Arrays.asList(selectedOptions));  // Removes duplicate options
        String[] sortedSelectedOptions = tempSelectedOptions.toArray(new String[tempSelectedOptions.size()]);   // Converts back to String array
        return sortedSelectedOptions;
    }

    /**
     * Saves user's application to the jobs they
     * applied for
     */
    private void applyJobs() {
        for (Job appliedJob : appliedJobList) {
            String applicationData = application.getCSVFormat();
            appliedJob.saveApplication(applicationData);
        }
    }

    /**
     * Updates available jobs by removing jobs that
     * have been already applied to
     */
    private void updateAvailableJobs() {
        availableJobList.removeAll(appliedJobList);
    }

    /**
     * Update which jobs have already been applied to
     * @param selectedOptions job options that user wish to apply to
     */
    private void updateAppliedJobs(String[] selectedOptions) {
        for (String option : selectedOptions) {
            int jobIndex = Integer.parseInt(option) - 1;
            appliedJobList.add(availableJobList.get(jobIndex));
        }
    }

    /**
     * Check if user selected job options are valid
     * @param selectedOptions job options that user wish to apply to
     * @return true if options are valid
     */
    private boolean isValidJobOptions(String[] selectedOptions) {
        //need better valid checker/
        for (String option : selectedOptions) {
            int jobIndex = Integer.parseInt(option) - 1;        // Takes into account menu index values being different
            if (!(jobIndex < availableJobCount && jobIndex >= 0)) {
                // If selected options are not within the range of given options
                return false;
            }
        }
        return true;
    }

    /**
     * Check if there are any available jobs
     * @return true if there are available jobs
     */
    private boolean hasAvailableJobs() {
        if (availableJobList.size() != 0){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Saves jobs that the user has
     * successfully applied to
     */
    private void saveJobPreferences() {
        applyJobs();
        saveData();
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
     * Prints output when invalid
     * command is given
     */
    private void printInvalidCommand() {
        System.out.println(MENU_INVALID_COMMAND);
    }

    /**
     * Gets user's keyboard input
     * @return user input
     */
    private String keyboardInput() {
        return keyboard.nextLine();
    }

    /**
     * Closes the scanner
     */
    private void closeInput() {
        keyboard.close();
    }
}
