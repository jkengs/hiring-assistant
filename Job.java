import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class that represents the jobs
 */
public class Job implements Serializable{

    // Job Information
    private long createdAt;
    private String title;
    private String description;
    private String degree;
    private int salary;
    private LocalDate startDate;

    // Job Data Lists
    private String[] jobData;
    private ArrayList<String[]> receivedApplications;
    private int lineNo;

    // Index Constants for CSV File
    private final int CREATED_AT_INDEX = 0;
    private final int TITLE_INDEX = 1;
    private final int DESCRIPTION_INDEX = 2;
    private final int DEGREE_INDEX = 3;
    private final int SALARY_INDEX = 4;
    private final int START_DATE_INDEX = 5;

    // Warning Messages
    private final String WARNING_CHARACTERISTICS = "WARNING: invalid characteristic in jobs file in line ";
    private final String WARNING_NUMBER = "WARNING: invalid number format in jobs file in line ";
    private final String WARNING_MANDATORY = "WARNING: invalid mandatory data field in line ";

    public final static int DATA_FIELD_NO = 6;  // Valid Number of Fields per line in CSV
    private final String EMPTY_FIELD = "n/a";

    /**
     * Job default constructor
     */
    public Job(){};

    /**
     * Job constructor when reading the corresponding 
     * csv file
     * @param jobData contains job data fields
     * @param lineNo the line number relative to csv file
     * @throws InvalidMandatoryDataException invalid mandatory data
     */
    public Job(String[] jobData, int lineNo) throws InvalidMandatoryDataException {
        this.jobData = jobData;
        this.lineNo = lineNo;
        this.receivedApplications = new ArrayList<String[]>();
        loadJobData();
    }

    /**
     * Saves applications (applicants who applied)
     * @param applicationData application data
     */
    public void saveApplication(String applicationData) {
        IOHandler ioHandler = new IOHandler();
        try
        {
            this.receivedApplications.add(ioHandler.processLine(applicationData));      
        } catch (Exception e) {
            this.receivedApplications = new ArrayList<String[]>();
            this.receivedApplications.add(ioHandler.processLine(applicationData)); 
        }
    }
    
    /**
     * Loads the job data
     * @throws InvalidMandatoryDataException invalid mandatory field
     */
    private void loadJobData() throws InvalidMandatoryDataException {
        try {
            loadMandatoryData();
            loadOptionalData();
        } catch (InvalidMandatoryDataException e) {
            throw new InvalidMandatoryDataException(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        } catch (InvalidCharacteristicException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Loads mandatory data fields and handles any number format errors
     * @throws InvalidMandatoryDataException invalid mandatory data
     */
    private void loadMandatoryData() throws InvalidMandatoryDataException  {
            try {
                loadTitle();
                loadStartDate();
            } catch (NumberFormatException e) {
                // Prioritize Number Format Exception's message
                throw new InvalidMandatoryDataException(e.getMessage());
            } catch (InvalidMandatoryDataException e) {
                throw new InvalidMandatoryDataException(e.getMessage());
            }
    }


    /**
     * Loads optional data fields and handles any number format or characteristics errors
     * @throws InvalidCharacteristicException invalid characteristic values
     * @throws NumberFormatException invalid number format
     */
    private void loadOptionalData() throws NumberFormatException, InvalidCharacteristicException {
        try {
            loadCreatedAt();
            loadDescription();
            loadDegree();
            loadSalary();  
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage());
        } catch (InvalidCharacteristicException e) {
            throw new InvalidCharacteristicException(e.getMessage());
        }
    }
    
     /**
     * Loads the created at (unix timestamp) field
     * @throws NumberFormatException invalid number format
     */
    private void loadCreatedAt() throws NumberFormatException {
        try {
            this.createdAt = Long.parseLong(jobData[CREATED_AT_INDEX]);           
        } catch (Exception e) {
            // If value is not a number
            throw new NumberFormatException(WARNING_NUMBER + lineNo);
        }
    }

    /**
     * Loads the job title
     * @throws InvalidMandatoryDataException error when loading title
     */
    private void loadTitle() throws InvalidMandatoryDataException {
        try {
            this.title = jobData[TITLE_INDEX]; 
        } catch (Exception e) {
            throw new InvalidMandatoryDataException(WARNING_MANDATORY);
        }
    }

    /**
     * Loads the job description
     */
    private void loadDescription() {
        this.description = jobData[DESCRIPTION_INDEX];
    }

    /**
     * Loads the job degree
     * @throws InvalidCharacteristicException invalid degree value
     */
    private void loadDegree() throws InvalidCharacteristicException {
        try {
            this.degree = jobData[DEGREE_INDEX];
            if (!(degree.equals(Application.DEGREE_PHD) || degree.equals(Application.DEGREE_MASTER) || degree.equals(Application.DEGREE_BACHELOR) || isEmptyField(degree))) {
                this.degree = null;
                throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
        }
    } catch (Exception e) {
        throw new InvalidCharacteristicException(e.getMessage());
    }
}

    /**
     * Loads the job salary
     * @throws InvalidCharacteristicException invalid salary value
     */
    private void loadSalary() throws InvalidCharacteristicException {
        // add try here in case whitespace blank csv.
        this.salary = Integer.parseInt(jobData[SALARY_INDEX]);      
        try {
            int salaryInput = Integer.parseInt(jobData[SALARY_INDEX]);
            if (salaryInput >= Application.SALARY_EXPECTATION_MINIMUM) {
                // If value is a number and within valid range
                this.salary = salaryInput;
            } else {
                // If value is a number and not within valid range
                throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
            }         
        } catch (InvalidCharacteristicException e) {
            salary = 0;
            throw new InvalidCharacteristicException(e.getMessage());
        } catch (Exception e ) {
            // Default Value if no valid input
            salary = 0;
        }
    }

    /**
     * Loads the job start date
     * @throws NumberFormatException invalid start data
     */
    private void loadStartDate() throws NumberFormatException {
        try {
            if (isEmptyField(jobData[START_DATE_INDEX])) {
                this.startDate = null;
            } else {
                this.startDate = LocalDate.parse(jobData[START_DATE_INDEX], DateTimeFormatter.ofPattern("dd/MM/yy"));
            }         
        } catch (Exception e) {
            throw new NumberFormatException(WARNING_NUMBER + lineNo);
        }
    }

    // Job Setters

    /**
     * Sets created at
     */
    public void setCreatedAt() {
        this.createdAt = Instant.now().getEpochSecond();
    }

    /**
     * Sets job title
     * @param title job title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets job description
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets job degree
     * @param degree degree
     */
    public void setDegree(String degree) {
        this.degree = degree;
    }

    /**
     * Sets job salary
     * @param salary salary
     */
    public void setSalary(int salary) {
        this.salary = salary;
    }

    /**
     * Sets job start date
     * @param startDate start date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    


    /**
     * Checks if data field was left empty
     * @param field data field
     * @return true if empty field
     */
    private boolean isEmptyField(String field) {
        //return true on null empty string or whitespace only
        if (field == null || field.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    // Job Getters
    
    /**
     * Gets job title
     * @return title
     */
    public String getTitle() {
        if (isEmptyField(title)) {
            return EMPTY_FIELD;
        }
        return title;
    }

    /**
     * Gets job description
     * @return description
     */
    public String getDescription() {
        if (isEmptyField(description)) {
            return EMPTY_FIELD;
        }
        return description;
    }

    /**
     * Gets job degree
     * @return degree
     */
    public String getDegree() {
        if (isEmptyField(degree)) {
            return EMPTY_FIELD;
        }
        return degree;
    }

    /**
     * Gets job salary
     * @return salary
     */
    public String getSalary() {
        if (isEmptyField(Integer.toString(salary))) {
            return EMPTY_FIELD;
        }
        return Integer.toString(salary);
    }

    /**
     * Gets job start date
     * @return start date
     */
    public String getStartDate() {
        if (isEmptyField(startDate.toString())) {
            return EMPTY_FIELD;
        }
        return startDate.toString();
    }

    /**
     * Gets number of application applied to job
     * @return number of applications
     */
    public int getApplicationCount() {
        if (hasApplications()) {
            return receivedApplications.size();
        } else {
            return 0;
        }
    }

    /**
     * Gets the data for applications applied to job
     * @return application data list
     */
    public ArrayList<String[]> getReceivedApplications() {
        return receivedApplications;
    }

    /**
     * Checks if job has any applications
     * @return true if has applications
     */
    public boolean hasApplications() {
        try {
            if (receivedApplications.size() != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets job in CSV format
     * @return csv format data line
     */
    public String getCSVFormat() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String csvFormat = String.format("%d,%s,%s,%s,%d,%s\n", createdAt, title, description, degree, salary, dateFormatter.format(startDate));
        return csvFormat;
    }

    
    /**
     * Returns the unique identifier key of the job
     * @return hashcode
     */
    @Override
    public int hashCode() {
        //hashcode for multiple objects
        return Objects.hash(createdAt);
    }
}
