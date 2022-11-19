import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class to handle input output of files
 */
public class IOHandler {
    
    public final static String JOB_FILE_TYPE = "jobs";
    public final static String APPLICATION_FILE_TYPE = "applications";
    public final static String DATA_SAVE_FILEPATH = "save.ser";
    private final String APPLICATION_HEADER = "createdAt,lastname,firstname,careerSummary,age,gender,highestDegree,COMP90041,COMP90038,COMP90007,INFO90002,salaryExpectations,availability\n";
    private final String JOB_HEADER = "createdAt,title,description,degree,salary,startDate";

    // Error Messages
    private final String ERROR_READ_SAVE = "Unable to read object from save file.";
    private final String ERROR_WRITE = "Unable to write object to file: ";
    private final String ERROR_READ = "Unable to read file: ";
    private final String ERROR_CREATE_FILE = "Unable to create new file: ";
    private final String ERROR_WELCOME_MESSAGE = "Welcome Message File not found.";

    
    /**
     *  Default constructor
     */
    public IOHandler(){}
    
    /**
     * Reads the CSV file
     * @param filePath file path
     * @param dataFieldNo number of data fields
     * @param fileType type of file (jobs or application)
     * @return array of data strings
     * @throws IOHandlerException error during reading file
     */
    public ArrayList<String[]> readFile(String filePath, int dataFieldNo, String fileType) throws IOHandlerException {
        ArrayList<String[]> data = new ArrayList<String[]>();
        String currentLine;
        int lineNo = 1; // Start from Line 1
        try {
            File file = new File(filePath);
            file.createNewFile(); //creates new file if not avail.
            BufferedReader csvReader = new BufferedReader(new FileReader(file));
            csvReader.readLine(); // Reads the File Header
            while ((currentLine = csvReader.readLine()) != null) {
                try {
                    data.add(processLine(currentLine, dataFieldNo, lineNo, fileType));
                    lineNo += 1;
                } catch (InvalidDataFormatException e) {
                    System.out.println(e.getMessage());
                    lineNo += 1;
                }
            }
            csvReader.close();
        }  catch (Exception e) {
            throw new IOHandlerException(ERROR_READ + filePath);
        } 
        return data;
    }

    /**
     * Process string for any commas within quotes when reading CSV
     * @param currentLine current line
     * @param dataFieldNo number of data fields
     * @param lineNo line it is reading from
     * @param fileType type of file (job or application)
     * @throws InvalidDataFormatException invalid data no per row
     * @return array of data strings
     */
    private String[] processLine(String currentLine, int dataFieldNo, int lineNo, String fileType) throws InvalidDataFormatException {
        String[] processedLine;
        StringBuilder stringBuilder = new StringBuilder();
        currentLine = currentLine.replaceAll(",\"", "<QUOTES><SENTENCE>");  // Mark the start of strings that have commas within
        currentLine = currentLine.replaceAll("\",", "<SENTENCE><QUOTES>");  // Mark the end of strings that have commas within
        processedLine = currentLine.split("<QUOTES>");                                  // Split strings that have quotes from those without                                           
        for (String line : processedLine) {
            if (line.contains("<SENTENCE>")) {
                // Process strings that have commas within the sentence
                line = line.replaceAll("<SENTENCE>", "<SPLIT>");      
                line = line.replaceAll("\"", "<SPLIT>");
                stringBuilder.append(line);
            } else {
                // Process strings without any commas within the sentence
                line = line.replaceAll(",", "<SPLIT>");
                stringBuilder.append(line);
            }
        }
        processedLine = stringBuilder.toString().split("<SPLIT>");
        if (processedLine.length <= dataFieldNo) {
            return processedLine; 
        } else {
            throw new InvalidDataFormatException(String.format("WARNING: invalid data format in %s file in line %d", fileType, lineNo));
        }
    }

    /**
     * Process string for any commas within quotes
     * for regular processing
     * @param currentLine current line
     * @return array of data strings
     */
    public String[] processLine(String currentLine) {
        String[] processedLine;
        StringBuilder stringBuilder = new StringBuilder();
        currentLine = currentLine.replaceAll(",\"", "<QUOTES><SENTENCE>");
        currentLine = currentLine.replaceAll("\",", "<SENTENCE><QUOTES>");
        processedLine = currentLine.split("<QUOTES>");
        for (String line : processedLine) {
            if (line.contains("<SENTENCE>")) {
                line = line.replaceAll("<SENTENCE>", "<SPLIT>");
                line = line.replaceAll("\"", "<SPLIT>");
                stringBuilder.append(line);
            } else {
                line = line.replaceAll(",", "<SPLIT>");
                stringBuilder.append(line);
            }
        }
        processedLine = stringBuilder.toString().split("<SPLIT>");
        return processedLine;
    }

    /**
     * Saves state of program
     * @param obj object
     * @throws IOHandlerException error during saving data to save file
     */
    public void saveData(Object obj) throws IOHandlerException {
        writeObject(obj, DATA_SAVE_FILEPATH);
    }

    /**
     * Reads saved file
     * @return list of read objects
     * @throws IOHandlerException error during reading save file
     */
    public ArrayList<?> readSave() throws IOHandlerException {
            try {
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(DATA_SAVE_FILEPATH));
                ArrayList<?> objectList = (ArrayList<?>) input.readObject();                                // Any type to avoid java compiler warning when typecasting
                input.close();
             return objectList;
            } catch (Exception e) {
                throw new IOHandlerException(ERROR_READ_SAVE);
            }
    }

    /**
     * Write object to a file
     * @param obj object
     * @param filepath filepath
     * @throws IOHandlerException error during writing to a file
     */
    private void writeObject(Object obj, String filePath) throws IOHandlerException {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filePath,false));
            output.writeObject(obj);
            output.close();
        } catch (Exception e) {
            throw new IOHandlerException(ERROR_WRITE + filePath);
        } 
    }

    /**
     * Check if job or application file exist,
     * else create a new one and add their respective
     * headers
     * @param filePath filepath
     * @param fileType whether it is job or application type
     */
    public void checkFileExists(String filePath, String fileType) {
        File file = new File (filePath);
        if (!file.exists()) {
            String fileHeader = getFileHeader(fileType);
            try {
                file.createNewFile();
                FileWriter fr = new FileWriter(file, true);
                PrintWriter csvWriter = new PrintWriter(fr);
                csvWriter.write(fileHeader);
                csvWriter.flush();
                csvWriter.close();
            } catch (Exception e) {
                System.out.println(ERROR_CREATE_FILE + filePath);
            }
        }
    }

    /**
     * Gets the header for CSV
     * @param fileType whether it is job or application type
     * @return header
     */
    private String getFileHeader(String fileType) {
        
        switch (fileType)
        {
            case APPLICATION_FILE_TYPE:
                return APPLICATION_HEADER;
                
            case JOB_FILE_TYPE:
                return JOB_HEADER;

            default:
                return "";
        }
    }

    /**
     * Display welcome message
     * @param filename filename
     */
    public void displayWelcomeMessage(String filename) {
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            System.out.println(ERROR_WELCOME_MESSAGE);
        }
        while (inputStream.hasNextLine()) {
            System.out.println(inputStream.nextLine());
        }
    }
}
