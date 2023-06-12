import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A class that represents the application of applicants,
 * containing their personal details and relevant background
 * information
 */
public class Application implements Serializable{

    private String[] applicationData;
    private int lineNo;

    // Application Information
    private long createdAt;
    private String lastName;
    private String firstName;
    private String careerSummary;
    private int age;
    private String gender;
    private String highestDegree;
    private int gradeCOMP90041;
    private int gradeCOMP90038;
    private int gradeCOMP90007;
    private int gradeINFO90002;
    private int salaryExpectations;
    private LocalDate availability;

    // Default Int Values
    private final int NO_WAM = 0;
    private final int NO_GRADE = 0;
    private final int NO_AGE = 0;
    private final static int REVERSE_SORT_MULTIPLIER = -1;
    private final int IS_LESSER = -1;
    private final int IS_GREATER = 1;
    public final static int DATA_FIELD_NO = 13;

    private final String EMPTY_FIELD = "n/a"; // Default value if user gave empty input
    private final String EMPTY_CSV_FIELD = "";
    
    // Data Field Constraints
    public final  static String DEGREE_BACHELOR = "Bachelor";
    public final  static String DEGREE_MASTER = "Master";
    public final  static String DEGREE_PHD = "PHD";
    public final  static String GENDER_FEMALE = "female";
    public final  static String GENDER_MALE = "male";
    public final  static String GENDER_OTHER = "other";
    public final static int AGE_UPPER_LIMIT = 100;
    public final static int AGE_LOWER_LIMIT = 18;
    public final static int SALARY_EXPECTATION_MINIMUM = 1;
    public final static int GRADE_MINIMUM = 49;
    public final static int GRADE_MAXIMUM = 100;
    public final static String ALLOWED_DATE_PATTERN = "dd/MM/yy";

    // Subject Codes
    public static final String SUBJECT_CODE_JAVA = "COMP90041";
    public static final String SUBJECT_CODE_ALGORITHMS = "COMP90038";
    public static final String SUBJECT_CODE_IT = "COMP90007";
    public static final String SUBJECT_CODE_DB = "INFO90002";

    // Indexes for Reading CSV File
    private final int CREATED_AT_INDEX = 0;
    private final int LAST_NAME_INDEX = 1;
    private final int FIRST_NAME_INDEX = 2;
    private final int CAREER_SUMMARY_INDEX = 3;
    private final int AGE_INDEX = 4;
    private final int GENDER_INDEX = 5;
    private final int HIGHEST_DEGREE_INDEX = 6;
    private final int GRADE_COMP90041_INDEX = 7;
    private final int GRADE_COMP90038_INDEX = 8;
    private final int GRADE_COMP90007_INDEX = 9;
    private final int GRADE_INFO90002_INDEX = 10;
    private final int SALARY_EXPECTATION_INDEX = 11;
    private final int AVAILABILITY_INDEX = 12;
    private final int PHD_PRIORITY_VALUE = 3;
    private final int MASTER_PRIORITY_VALUE = 2;
    private final int BACHELOR_PRIORITY_VALUE = 1;

    // Warning Messages
    private final String WARNING_CHARACTERISTICS = "WARNING: invalid characteristic in applications file in line ";
    private final String WARNING_NUMBER = "WARNING: invalid number format in applications file in line ";
    /**
     * Application default constructor,
     * used by applicant when creating a new one
     */
    public Application() {};

    /**
     * Application constructor when reading saved files to retrieve
     * received applications linked to jobs
     * @param applicationData contains application data fields
     */
    public Application(String[] applicationData) {
        this.applicationData = applicationData;
        try {
            loadMandatoryData();
            loadOptionalData(); 
        } catch (Exception e) {
            // Do nothing as any error would have be handled at initial launch before any saved files exist
        }
    }

    /**
     * Application constructor when reading the application
     * csv file, used by HR team
     * @param applicationData contains application data fields
     * @param lineNo the line number relative to csv file
     * @throws InvalidMandatoryDataException invalid mandatory data
     */
    public Application(String[] applicationData, int lineNo) throws InvalidMandatoryDataException {
        this.applicationData = applicationData;
        this.lineNo = lineNo;
        loadApplicationData();
    }


    /**
     * Loads entire application data and handles any exceptions/errors
     * @throws InvalidMandatoryDataException invalid mandatory data
     */
    private void loadApplicationData() throws InvalidMandatoryDataException {
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
    private void loadMandatoryData() throws InvalidMandatoryDataException {
        try {
            loadLastName();
            loadFirstName();
            loadAge(); 
        } catch (NumberFormatException | InvalidCharacteristicException e) {
            throw new InvalidMandatoryDataException(e.getMessage());
        }
    }

    /**
     * Loads optional data fields and handles any number format or characteristics errors
     * @throws InvalidCharacteristicException invalid characteristic values
     * @throws NumberFormatException invalid number format
     */
    private void loadOptionalData() throws InvalidCharacteristicException, NumberFormatException {
        try {
            loadCreatedAt();
            loadCareerSummary();
            loadGender();
            loadHighestDegree();
            loadCourseGrades();
            loadSalaryExpectations();
            loadAvailability();   
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
            this.createdAt = Long.parseLong(applicationData[CREATED_AT_INDEX]);                
        } catch (Exception e) {
            // If value is not a number
            throw new NumberFormatException(WARNING_NUMBER + lineNo);
        }        
    }

    /**
     * Loads the last name field
     */
    private void loadLastName() {
        this.lastName = applicationData[LAST_NAME_INDEX];
    }

    /**
     * Loads the first name field
     */
    private void loadFirstName() {
        this.firstName = applicationData[FIRST_NAME_INDEX];
    }

    /**
     * Loads the career summary field
     */
    private void loadCareerSummary() {
        this.careerSummary = applicationData[CAREER_SUMMARY_INDEX];
    }

    /**
     * Loads the age field
     * @throws NumberFormatException invalid number format
     * @throws InvalidCharacteristicException invalid characteristic field
     */
    private void loadAge() throws NumberFormatException, InvalidCharacteristicException {
        try {
            int inputAge = Integer.parseInt(applicationData[AGE_INDEX]);
            if ((inputAge < Application.AGE_UPPER_LIMIT && inputAge > Application.AGE_LOWER_LIMIT)) {
                // If age is a number and within valid range
                this.age = inputAge;
            } else {
                // Age is not within valid range
                throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
            }  

        } catch (InvalidCharacteristicException e) {
            throw new InvalidCharacteristicException(e.getMessage());
        
        } catch (Exception e) {
            // If value is not a number
            throw new NumberFormatException(WARNING_NUMBER + lineNo);
        }
    }

    /**
     * Loads the gender field
     * @throws InvalidCharacteristicException invalid field value
     */
    private void loadGender() throws InvalidCharacteristicException {
        try {
            this.gender = applicationData[GENDER_INDEX];
            if (!(gender.equals(GENDER_MALE) || gender.equals(GENDER_FEMALE) || gender.equals(GENDER_OTHER) || isEmptyField(gender))) {
                // If not valid input
                throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
            }
        // } catch (InvalidCharacteristicException e) {
        //     this.gender = null;
        //     throw new InvalidCharacteristicException(e.getMessage());
        } catch (Exception e) {
            throw new InvalidCharacteristicException(e.getMessage());
        }
    }

    /**
     * Loads the highest degree field
     * @throws InvalidCharacteristicException invalid field value
     */
    private void loadHighestDegree() throws InvalidCharacteristicException {
        try {
            this.highestDegree = applicationData[HIGHEST_DEGREE_INDEX];
            if (!(highestDegree.equals(DEGREE_PHD) || highestDegree.equals(DEGREE_MASTER) || highestDegree.equals(DEGREE_BACHELOR) || isEmptyField(highestDegree))) {
                this.highestDegree = null;
                throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
            }
        } catch (Exception e) {
            throw new InvalidCharacteristicException(e.getMessage());
        }
    }

    /**
     * Loads all the course grades field
     * @throws InvalidCharacteristicException invalid data field
     * @throws NumberFormatException invalid number format
     */
    private void loadCourseGrades() throws NumberFormatException, InvalidCharacteristicException {
        loadSubjectGrade(SUBJECT_CODE_JAVA);
        loadSubjectGrade(SUBJECT_CODE_ALGORITHMS);
        loadSubjectGrade(SUBJECT_CODE_IT);
        loadSubjectGrade(SUBJECT_CODE_DB);
    }

    /**
     * Loads a subject grade field
     * @throws NumberFormatException invalid number format
     * @throws InvalidCharacteristicException invalid data field
     */
    private void loadSubjectGrade(String subjectCode) throws NumberFormatException, InvalidCharacteristicException {
        try {
            switch (subjectCode)
            // If value is an integer, check if it is within valid range
            {
                case SUBJECT_CODE_JAVA:
                    this.gradeCOMP90041 = Integer.parseInt(applicationData[GRADE_COMP90041_INDEX]);
                    if (!(gradeCOMP90041 >= Application.GRADE_MINIMUM && gradeCOMP90041 <= Application.GRADE_MAXIMUM)) {
                        this.gradeCOMP90041 = NO_GRADE;
                        throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
                    }
                    break;
    
                case SUBJECT_CODE_ALGORITHMS:
                    this.gradeCOMP90038 = Integer.parseInt(applicationData[GRADE_COMP90038_INDEX]);
                    if (!(gradeCOMP90038 >= Application.GRADE_MINIMUM && gradeCOMP90038 <= Application.GRADE_MAXIMUM)) {
                        this.gradeCOMP90038 = NO_GRADE;
                        throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
                    }
                    break;
    
                case SUBJECT_CODE_IT:
                    this.gradeCOMP90007 = Integer.parseInt(applicationData[GRADE_COMP90007_INDEX]);
                    if (!(gradeCOMP90007 >= Application.GRADE_MINIMUM && gradeCOMP90007 <= Application.GRADE_MAXIMUM)) {
                        this.gradeCOMP90007 = NO_GRADE;
                        throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
                    }
                    break;
    
                case SUBJECT_CODE_DB:
                    this.gradeINFO90002 = Integer.parseInt(applicationData[GRADE_INFO90002_INDEX]);
                    if (!(gradeINFO90002 >= Application.GRADE_MINIMUM && gradeINFO90002 <= Application.GRADE_MAXIMUM)) {
                        this.gradeINFO90002 = NO_GRADE;
                        throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
                    }
                    break;
            } 
        } catch (InvalidCharacteristicException e) {
            throw new InvalidCharacteristicException(e.getMessage());
        } catch (Exception e) {
            
            switch (subjectCode) 
            {
                case SUBJECT_CODE_JAVA:
                    this.gradeCOMP90041 = NO_GRADE;
                    break;
                    
                case SUBJECT_CODE_ALGORITHMS:
                    this.gradeCOMP90038 = NO_GRADE;
                    break;
    
                case SUBJECT_CODE_IT:
                    this.gradeCOMP90007 = NO_GRADE;
                    break;
    
                case SUBJECT_CODE_DB:
                    this.gradeINFO90002 = NO_GRADE;
                    break; 
                }
            }
        }

    /**
     * Loads the salary expectation field
     * @throws NumberFormatException invalid number format
     * @throws InvalidCharacteristicException invalid salary value
     */
    private void loadSalaryExpectations() throws NumberFormatException, InvalidCharacteristicException {
        try {
            int salaryExpectationsInput = Integer.parseInt(applicationData[SALARY_EXPECTATION_INDEX]);
            if (salaryExpectationsInput >= Application.SALARY_EXPECTATION_MINIMUM) {
                // If value is a number and within valid range
                this.salaryExpectations = salaryExpectationsInput;
            } else {
                // If value is a number and not within valid range
                throw new InvalidCharacteristicException(WARNING_CHARACTERISTICS + lineNo);
            }         
        } catch (InvalidCharacteristicException e) {
            salaryExpectations = 0;
            throw new InvalidCharacteristicException(e.getMessage());
        } catch (Exception e ) {
            // Default Value if no valid input
            salaryExpectations = 0;
        }
    }

    /**
     * Loads the availability field
     * @throws InvalidCharacteristicException invalid availability format
     */
    private void loadAvailability() {
        try {
            this.availability = LocalDate.parse(applicationData[AVAILABILITY_INDEX], DateTimeFormatter.ofPattern("dd/MM/yy"));
        } catch (Exception e) {
            // Not a date with valid pattern
        }
    }

    /**
     * Gets application data in CSV form
     * @return application data in csv form
     */
    public String getCSVFormat() {

        String csvFormat = String.format("%d,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s\n", createdAt, lastName, firstName, careerSummary, age, gender, highestDegree,
        getCSVGradeCOMP90041(), getCSVGradeCOMP90038(), getCSVGradeCOMP90007(), getCSVGradeINFO90002(), getCSVSalaryExpectations(), getCSVAvailability());
        return csvFormat;
    }

    // Application Data Field Setters

    /**
     * Sets created at
     */
    public void setCreatedAt() {
        this.createdAt = Instant.now().getEpochSecond();
    }

    /**
     * Sets last name
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    /**
     * Sets first name
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    /**
     * Sets career summary
     * @param careerSummary career summary
     */
    public void setCareerSummary(String careerSummary) {
        this.careerSummary = checkCommas(careerSummary).trim();
    }

    /**
     * Sets age
     * @param age age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets gender
     * @param gender gender
     */
    public void setGender(String gender) {
        this.gender = gender.trim();
    }

    /**
     * Sets highest degree
     * @param highestDegree highest degree
     */
    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree.trim();
    }

    /**
     * Sets the grades 
     * @param grade grade
     * @param subjectCode subject code
     */
    public void setGrade(int grade, String subjectCode) {
        switch (subjectCode) {

            case SUBJECT_CODE_JAVA:
                this.gradeCOMP90041 = grade;

            case SUBJECT_CODE_ALGORITHMS:
                this.gradeCOMP90038 = grade;

            case SUBJECT_CODE_IT:
                this.gradeCOMP90007 = grade;

            case SUBJECT_CODE_DB:
                this.gradeINFO90002 = grade;

        }
    }

    /**
     * Sets the salary expectation
     * @param salary salary expectation
     */
    public void setSalaryExpectations(int salary) {
        this.salaryExpectations = salary;
    }

    /**
     * Sets availability
     * @param date date
     */
    public void setAvailability(LocalDate date) {
        this.availability = date;
    }

    /**
     * Sets availability to null if user chooses not
     * input any
     */
    public void setBlankAvailability() { 
        this.availability = null;
    }


    // Application Data Field Getters

    /**
     * Gets created at
     * @return created at
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets last name 
     * @return last name
     */
    public String getLastName() {
        if (isEmptyField(lastName)) {
            return EMPTY_FIELD;
        }
        return lastName;
    }

    /**
     * Gets first name
     * @return first name
     */
    public String getFirstName() {
        if (isEmptyField(firstName)) {
            return EMPTY_FIELD;
        }
        return firstName;
    }

    /**
     * Gets degree
     * @return degree
     */
    public String getDegree() {
        if (isEmptyField(highestDegree)) {
            return EMPTY_FIELD;
        }
        return highestDegree;
    }

    /**
     * Gets gender
     * @return gender
     */
    public String getGender() {
        if (isEmptyField(gender)) {
            return EMPTY_FIELD;
        } 
        return gender;
    }

    /**
     * Gets career summary
     * @return career summary
     */
    public String getCareerSummary() {
        if (isEmptyField(careerSummary)) {
            return EMPTY_FIELD;
        }
        return careerSummary;
    }

    /**
     * Gets salary expectations
     * @return salary expectations
     */
    public String getSalaryExpectations() {
        if (isEmptyField(Integer.toString(salaryExpectations)) || salaryExpectations == 0) {
            return EMPTY_FIELD;
        }
        return Integer.toString(salaryExpectations);
    }


    /**
     * Gets availability in the dd/mm/yy format
     * @return formatted availability date
     */
    public String getFormattedAvailability() {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            return dateFormatter.format(availability);          
        } catch (Exception e) {
            return EMPTY_FIELD;
        }
    }

    /**
     * Gets availability field 
     * @return availability date
     */
    private LocalDate getAvailability() {
        try {
            return availability;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets age
     * @return age
     */
    public int getAge() {
        try {
            return this.age;
        } catch (Exception e) {
            return NO_AGE;
        }
    }

    // Application Data Field Getters in CSV readable format

    /**
     * Gets COMP90041 grade in CSV format
     * @return comp90041 grade
     */
    private String getCSVGradeCOMP90041() {
        if (gradeCOMP90041 == 0) {
            return EMPTY_CSV_FIELD;
        } else {
            return Integer.toString(gradeCOMP90041);
        }
    }

    /**
     * Gets COMP90038 grade in CSV format
     * @return comp90038 grade
     */
    private String getCSVGradeCOMP90038() {
        if (gradeCOMP90038 == 0) {
            return EMPTY_CSV_FIELD;
        } else {
            return Integer.toString(gradeCOMP90038);
        }
    }

    /**
     * Gets COMP90007 grade in CSV format
     * @return comp90007 grade
     */
    private String getCSVGradeCOMP90007() {
        if (gradeCOMP90007 == 0) {
            return EMPTY_CSV_FIELD;
        } else {
            return Integer.toString(gradeCOMP90007);
        }
    }

    /**
     * Gets INFO90002 grade in CSV format
     * @return info90002 grade
     */
    private String getCSVGradeINFO90002() {
        if (gradeINFO90002 == 0) {
            return EMPTY_CSV_FIELD;
        } else {
            return Integer.toString(gradeINFO90002);
        }
    }

    /**
     * Gets availability in CSV format
     * @return availability
     */
    private String getCSVAvailability() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        if (availability == null) {
            return EMPTY_CSV_FIELD;
        } else {
            return dateFormatter.format(availability);
        }
    }

    /**
     * Gets salary expectations in CSV format
     * @return salary expectations
     */
    private String getCSVSalaryExpectations() {
        if (salaryExpectations == 0) {
            return EMPTY_CSV_FIELD;
        } else {
            return Integer.toString(salaryExpectations);
        }
    }

    /**
     * Add quotes to inputs that contain commas
     * @param input input string
     * @return formatted string
     */
    public String checkCommas(String input) {
        if (input.contains(",")) {
            return ("\"" + input + "\"");
        }
        return input;
    }

    /**
     * Check if data field is empty
     * @param field datafield
     * @return true if empty
     */
    private boolean isEmptyField(String field) {
        //trim cuts whitespaces, return true on null empty string or whitespace only
        if (field == null || field.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if application has a career summary
     * @return true if career summary present
     */
    public boolean hasCareerSummary() {
        if (careerSummary.equals(EMPTY_FIELD)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if application has gender field
     * @return true if gender present
     */
    public boolean hasDegree() {
        if (highestDegree.equals(EMPTY_FIELD)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if application has a gender field
     * @return true if career summary present
     */
    public boolean hasGender() {
        if (gender.equals(EMPTY_FIELD)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Helper function to sort applicants when listing applicant
     * list in HR portal
     * @param application application
     * @return compareTo value
     */
    public int sortApplicantList (Application application) {
        LocalDate thisAvailability = this.availability;
        LocalDate otherAvailablity = application.getAvailability();

        if (thisAvailability == null && otherAvailablity == null) {
            // If both dates are null
            return compareName(application);
        } else if (thisAvailability == null) {
            // If this date is null
            return IS_GREATER;
        } else if (otherAvailablity == null) {
            // If other date is null
            return IS_LESSER;
        } else {
            // If both dates are not null
            if (thisAvailability.isBefore(otherAvailablity)) {
                return IS_LESSER;
            } else if (thisAvailability.isAfter(otherAvailablity)){
                return IS_GREATER;
            } else {
                // If both dates are not null and equal
                return compareName(application); 
            }
        }
    }


    /**
     * Helper function to compare applicants by their availability
     * @param application application to compare against
     * @return compareTo value
     */
    public int compareAvailability(Application application) {
        if (this.availability == null && application.getAvailability() == null) {
            //both null
            return compareCreatedAt(application);

        } else if (this.availability == null) {
            //before
            return 1;
        } else if (application.getAvailability() == null) {
            //before
            return -1;
        } else {
            if (this.availability.isBefore(application.getAvailability())) {
                return -1;
            } else if (this.availability.isAfter(application.getAvailability())){
                return 1;
            } else {
                //equal
                return compareCreatedAt(application);
            }
        }
    }

    /**
     * Helper function to compare applicants by their lastname
     * @param application application to compare against
     * @return compareTo value
     */
    private int compareLastName(Application application) {
        String thisLastName = this.lastName.toLowerCase();
        String otherLastName = application.getLastName().toLowerCase();
        int compareLastName = thisLastName.compareTo(otherLastName);
        return compareLastName;
    }

    /**
     * Helper function to compare applicants by their firstname
     * @param application application to compare against
     * @return compareTo value
     */
    private int compareFirstName(Application application) {
        String thisFirstName = this.firstName.toLowerCase();
        String otherFirstName = application.getFirstName().toLowerCase();
        int compareFirstName = thisFirstName.compareTo(otherFirstName);
        return compareFirstName;   
    }

    /**
     * Helper function to compare applicants by their full name
     * @param application application to compare against
     * @return compareTo value
     */
    private int compareName(Application application) {
        if (compareLastName(application) != 0) {
            return compareLastName(application);
        } else {
            return compareFirstName(application);
        }
    }

    /**
     * Helper function to compare applicants by their creation date time
     * @param application application to compare against
     * @return compareTo value
     */
    private int compareCreatedAt(Application application) {
        Long thisCreatedAt = this.createdAt;
        Long otherCreatedAt = application.getCreatedAt();
        return thisCreatedAt.compareTo(otherCreatedAt);
    }

    /**
     * Helper function to compare applicants by their degree
     * @param application application to compare against
     * @return compareTo value
     */
    private int compareDegree(Application application) {
        int thisDegreeWeightage = getDegreeWeightage(this.highestDegree);
        //rename getdegree
        int otherDegreeWeightage = getDegreeWeightage(application.getDegree());
        int compareDegreeWeightage = thisDegreeWeightage - otherDegreeWeightage;

        return compareDegreeWeightage;
    }

    /**
     * Get the weightage of degree with PHD being the highest
     * and Bachelor the lowest
     * @param degree degree
     * @return degree weighting
     */
    public int getDegreeWeightage(String degree) {
        int degreePriority = 0;
        switch(degree)
        {
            case DEGREE_PHD:
                degreePriority = PHD_PRIORITY_VALUE;
            case DEGREE_MASTER:
                degreePriority = MASTER_PRIORITY_VALUE;
            case DEGREE_BACHELOR:
                degreePriority = BACHELOR_PRIORITY_VALUE;
            default:
                break;
        }
        return degreePriority;
    }

    /**
     * Main function to help sort applications by a datafield type,
     * such as last name, wam, degree
     * @param filterType type of filter
     * @param application application to compare against
     * @return compareTo value
     */
    public int filterBy(String filterType, Application application) {

        int filterValue;
        switch (filterType)
        {
            case HR.FILTER_LASTNAME:
                filterValue = filterLastName(application);
                break;

            case HR.FILTER_DEGREE:
                filterValue = filterDegree(application);
                break;

            case HR.FILTER_WAM:
                filterValue = (int)(filterWam(application));
                break;
            default:
                filterValue = 0;
                break;
        }
        return filterValue;
    }

    /**
     * Helper function to filter applicants by their lastname
     * in HR portal
     * @param application application to compare against
     * @return compareTo value
     */
    private int filterLastName(Application application) {
        if (compareLastName(application) != 0) {
            return compareLastName(application);
        } else {
            return compareCreatedAt(application);
        }
    }

    /**
     * Helper function to filter applicants by their degree
     * @param application application to compare against
     * @return int compareTo value
     */
    private int filterDegree(Application application) {
        if (compareDegree(application) != 0) {
            return compareDegree(application) * REVERSE_SORT_MULTIPLIER;
        } else {
            return compareCreatedAt(application) * REVERSE_SORT_MULTIPLIER;
        }
    }
    
    /**
     * Helper function to filter applicants by their wam
     * @param application application to compare against
     * @return compareTo value
     */
    private double filterWam(Application application) {
        if (compareWam(application) != 0) {
            return (compareWam(application)) * REVERSE_SORT_MULTIPLIER;
        } else {
            // return compareWam(application) * HR.REVERSE_SORT_MULTIPLIER;
            return compareName(application);
        }
    }

    /**
     * Helper function to compare applicants by their wam
     * @param application application to compare against
     * @return compareTo value
     */
    private double compareWam(Application application) {
        double thisWam = this.calculateWam();
        double otherWam = application.calculateWam();
        double compareWam = thisWam - otherWam;
        return compareWam;
    }
    
    /**
     * Helper function to calculate applicant's WAM
     * @return WAM score
     */
    public double calculateWam() {
        double score = 0;
        double wam = 0;
        if (hasNoCourseGrades()) {
            return NO_WAM;
        } else {

            if (gradeCOMP90007 != 0) {
                score += gradeCOMP90007;    
            }

            if (gradeCOMP90038 != 0) {
                score += gradeCOMP90038;
            }

            if (gradeCOMP90041 != 0) {
                score += gradeCOMP90041;  
            }

            if (gradeINFO90002 != 0) {
                score += gradeINFO90002; 
            }

            wam = score / getSubjectCounter();
            return wam;
        }
    }

    /**
     * Check if applicant has any course grades in their application
     * @return true if no course grades present
     */
    public boolean hasNoCourseGrades() {
        if (gradeCOMP90007 == 0 || gradeCOMP90038 == 0 || gradeCOMP90041 == 0 || gradeINFO90002 == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Helper function to calculate how many subjects scores
     * were given by user when creating application
     * @return number of filled subject grade data field
     */
    public int getSubjectCounter() {
        int subjectCounter = 0;
        if (gradeCOMP90007 != 0) {
            subjectCounter += 1;
        }
        if (gradeCOMP90038 != 0) {
            subjectCounter += 1;
        }
        if (gradeCOMP90041 != 0) {
            subjectCounter += 1;
        }
        if (gradeINFO90002 != 0) {
            subjectCounter += 1;   
        }
        return subjectCounter;
    }

    /**
     * Returns the unique identifier key of the application 
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(firstName,lastName,age,createdAt);
    }
}