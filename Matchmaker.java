import java.util.ArrayList;

/**
 * A class that represents the matchmaking process when selecting
 * the best applicant for a job
 */
public class Matchmaker {

    // Job List
    private ArrayList<Job> masterJobList;       // List of all jobs
    private ArrayList<Job> appliedJobList;      // List of jobs that received applications
    private ArrayList<Job> matchedJobList;      // List of jobs that were matched with an applicant
    
    // Application List
    private ArrayList<Application> matchedApplicationList;

    // Matchmaking Algorithm Constants
    private final double DEGREE_NORMALIZER = 3.0;                                       // Normalizer value when calculating weightage of degree
    private enum Keywords {python, r, javascript, php, go, swift, ruby, css, java};     // Keywords that we look for when looking at applicant's summary
    
    // Text Constants
    private final String NO_AVAILABLE_JOBS = "No jobs available.";
    private final String NO_AVAILABLE_APPLICANTS = "No applicants available.";

    /**
     * Matchmaker default constructor
     */
    public Matchmaker(){}
   
    /**
     * Matchmaker Constructor (HR)
     * @param masterJobList jobs that contain information on which applicants applied 
     */
    public Matchmaker(ArrayList<Job> masterJobList) {
        this.masterJobList = masterJobList;
        this.appliedJobList = new ArrayList<Job>();
        this.matchedApplicationList = new ArrayList<Application>();
        this.matchedJobList = new ArrayList<Job>();
    }

    /**
     * Starts the matchmaking process
     */
    public void startProgram() {
        loadAppliedJobList();
        if (hasAvailableJobs() && hasAvailableApplicants()) {
            startMatchmaking();
            printMatches();   
        } else {
            if (!hasAvailableJobs() && !hasAvailableApplicants()) {
                // If no available jobs or applicants
                System.out.println(NO_AVAILABLE_JOBS);
            } else if (hasAvailableJobs()) {
                // If no available applicants
                System.out.println(NO_AVAILABLE_APPLICANTS);      
            } else {
                // If no available jobs
                System.out.println(NO_AVAILABLE_JOBS);
            } 
        }
    }

    /**
     * Checks if there are any available jobs to matchmake
     * @return true if there are available jobs
     */
    private boolean hasAvailableJobs() {
        if (masterJobList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if there are any available applicants
     * @return true if there are no available applicants
     */
    private boolean hasAvailableApplicants() {
        if (appliedJobList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Print the match results
     */
    private void printMatches() {
        for (Job job : matchedJobList) {
            // For each matched job
            int jobIndex = matchedJobList.indexOf(job) + 1; // Index to be printed
            Application application = matchedApplicationList.get(jobIndex - 1); // Get the matched applicant by the job index
            // Print Job Details
            System.out.printf("[%d] %s (%s). %s. Salary: %s. Start Date: %s.\n", jobIndex, job.getTitle(), 
                                    job.getDescription(), job.getDegree(), job.getSalary(), job.getStartDate());
            // Print Applicant Details
            System.out.printf("    Applicant match: %s, %s (%s): %s. Salary Expectations: %s. Available: %s\n", 
                application.getLastName(), application.getFirstName(), application.getDegree(),
                application.getCareerSummary(),
                application.getSalaryExpectations(), application.getFormattedAvailability());
        }
    }

    /**
     * Loads the applied job list
     */
    private void loadAppliedJobList() {
        for (Job job : masterJobList) {
            if (job.hasApplications()) {
                appliedJobList.add(job);
            }   
        }
    }


    /**
     * Starts the normal matching
     * process
     */
    private void startMatchmaking() {
        for (Job job : appliedJobList) {
            // For each Job that has applicants
            ArrayList<Application> candidateList = getCandidateList(job);
            findMatch(candidateList, job);
        }
    }

    private ArrayList<Application> getCandidateList(Job job) {
        ArrayList<Application> candidateList = new ArrayList<Application>();
        ArrayList<String[]> receivedApplicationData = job.getReceivedApplications();
        for (String[] data : receivedApplicationData) {
            candidateList.add(new Application(data));
        }
        return candidateList;
    }

    private void findMatch(ArrayList<Application> candidateList, Job job) {
        double topCandidateScore = 0;
        Application topCandidate = candidateList.get(0); // Default Value
        for (Application candidate : candidateList) {
            // For each candidate
            double candidateScore = 0;
            candidateScore += getDegreeWeightage(job, candidate);
            candidateScore += getWamWeightage(candidate.calculateWam()) * getWamPenalties(candidate.getSubjectCounter());
            candidateScore += getSummaryWeightage(topCandidate);
            if (candidateScore > topCandidateScore) {
                // If current candidate is more than the top candidate in score, replace top with current
                topCandidateScore = candidateScore;
                topCandidate = candidate;
            }
            if ((candidateScore == topCandidateScore) && (candidate.getCreatedAt() < topCandidate.getCreatedAt())) {
                // Tiebreak
                // If current candidate submitted their application earlier, replace top with current
                topCandidateScore = candidateScore;
                topCandidate = candidate;
            } 
        }
        matchedApplicationList.add(topCandidate);
        matchedJobList.add(job);
    }

    /**
     * Calculating weight scores based on summary field
     * @param candidate candidate
     * @return weight scores
     */
    private double getSummaryWeightage(Application candidate) {
        
        if (!candidate.hasCareerSummary()) {
            // No points if candidate has no summary written
            return 0;
        } else {
            double points = 0.1;    // Default points for writing a summary at the least
            String careerSummary = candidate.getCareerSummary();
            for (Keywords keyword : Keywords.values()) {
                if (careerSummary.contains(keyword.toString())) {
                    // Add more points if the summary contains the keywords
                    points += 0.1;
                }
            }
            return points;
        }
    }
    
    /**
     * Calculating weightage scores based on degree field
     * @param job job
     * @param candidate candidate
     * @return weight scores
     */
    private double getDegreeWeightage(Job job, Application candidate) {
        String jobDegreeRequirement = job.getDegree();
        // Priority is given in this order: PHD, Master, Bachelor
        int jobDegreePriority = candidate.getDegreeWeightage(jobDegreeRequirement);
        int candidateDegreePriority = candidate.getDegreeWeightage(candidate.getDegree());
        if (candidateDegreePriority >= jobDegreePriority) {
            // If candidate has met the degree requirement of the job
            return candidate.getDegreeWeightage(candidate.getDegree()) / DEGREE_NORMALIZER;
        } else {
            // If candidate did not meet the degree requirement of the job
            return 0;
        }
    }

    /**
     * Calculating WAM weightage scores based on WAM
     * @param wam candidate's wam
     * @return weight scores
     */
    private double getWamWeightage(double wam) {
        if (wam >50 && wam <= 70) {
            return 1.0;
        } else if (wam > 70 && wam <= 80) {
            return 2.0;
        } else if (wam > 80) {
            return 3.0;
        } else {
            return 0;
        }
    }

    /**
     * Calculating WAM penalties scores based on how many
     * subject grades they have inputted. The lesser subjects
     * they submit, the less the WAM's weightage would hold.
     * @param subjectCounter number of subject grades given
     * @return WAM penalties
     */
    private double getWamPenalties(int subjectCounter) {
        switch (subjectCounter)
        {
            case 0:
                return 0;
            case 1:
                return 0.25;
            case 2:
                return 0.50;
            case 3:
                return 0.75;
            case 4:
                return 1.0;
            default:
                return 0;  
        }
    }
}