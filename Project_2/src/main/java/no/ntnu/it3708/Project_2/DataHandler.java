package no.ntnu.it3708.Project_2;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.*;

/**
 * The Data handler.
 */
public class DataHandler {
    private String instance_name;
    private int nbr_nurses;
    private int capacity_nurse;
    private Depot depot;
    private HashMap<Integer, Patient> patients;
    private HashMap<Integer, Cluster> clusters;
    private Vector<Vector<Double>> travel_times; // TODO: Refactor to double[][] ?

    /**
     * Instantiates a new Data handler.
     */
    public DataHandler() {
        this.instance_name = "";
        this.nbr_nurses = 0;
        this.capacity_nurse = 0;
        this.depot = null;
        this.patients = new HashMap<>();
        this.clusters = new HashMap<>();
        this.travel_times = new Vector<>();
    }

    /**
     * Load data from resources.
     *
     * @param path the path
     * @throws URISyntaxException    the uri syntax exception
     * @throws FileNotFoundException the file not found exception
     */
    void loadData(String path) throws URISyntaxException, FileNotFoundException {
        FileReader resource = new FileReader(getClass().getResource(path).toURI().getPath());
        JsonObject obj = new JsonParser().parse(resource).getAsJsonObject();
        Gson gson = new Gson();

        // update Data
        this.instance_name = obj.get("instance_name").getAsString();
        this.nbr_nurses = obj.get("nbr_nurses").getAsInt();
        this.capacity_nurse = obj.get("capacity_nurse").getAsInt();

        // Get Depot
        this.depot = gson.fromJson(obj.get("depot"), Depot.class);

        // Get patients
        JsonObject patients_json = obj.getAsJsonObject("patients");
        for (Map.Entry<String, JsonElement> entry : patients_json.entrySet()) {
            Patient patient = gson.fromJson(entry.getValue(), Patient.class);
            int patient_id = Integer.valueOf(entry.getKey());
            patient.setId(patient_id);
            patient.calculateRange();

            // add patient
            this.patients.put(patient_id, patient);
        }

        // Get travel time
        JsonArray travel_time_json = obj.getAsJsonArray("travel_times");

        // Store as matrix
        for (int x = 0; x < travel_time_json.size(); x++) {
            Vector<Double> row = new Vector<>();
            this.travel_times.add(row);
            JsonArray travel_times_y = travel_time_json.get(x).getAsJsonArray();
            for (int y = 0; y < travel_times_y.size(); y++) {
                row.add(travel_times_y.get(y).getAsDouble());
            }
        }
    }


    /**
     * Run the KNN+ Clustering on the patients
     * @param tolerance tolerance when selecting k-value
     */
    void cluster_patients(int tolerance) {
        // Run KNN+
        KMeansPP kMeansPP = new KMeansPP(this.nbr_nurses*2,50, this.patients);
        HashMap<Integer, KMeansPP.Cluster> knn_clusters = kMeansPP.run(tolerance);


        // Create clusters
        for (Map.Entry<Integer, KMeansPP.Cluster> entry : knn_clusters.entrySet()) {
            int cluster_idx = entry.getKey();
            ArrayList<Patient> cluster_patients = entry.getValue().getMembers();
            this.clusters.put(cluster_idx, new Cluster(cluster_idx, cluster_patients));
        }
    }

    /**
     * Gets instance name.
     *
     * @return the instance name
     */
    public String getInstance_name() {
        return instance_name;
    }

    /**
     * Gets number of nurses.
     *
     * @return the number of nurses
     */
    public int getNbr_nurses() {
        return nbr_nurses;
    }

    /**
     * Gets capacity per nurse.
     *
     * @return the capacity per nurse
     */
    public int getCapacity_nurse() {
        return capacity_nurse;
    }

    /**
     * Gets depot information.
     *
     * @return the depot
     */
    public Depot getDepot() {
        return depot;
    }

    /**
     * Gets patients.
     *
     * @return the patients
     */
    public HashMap<Integer, Patient> getPatients() {
        return patients;
    }

    /**
     * Gets clusters.
     *
     * @return the clusters
     */
    public HashMap<Integer, Cluster> getClusters() {
        return clusters;
    }

    /**
     * Gets travel times in the form of a matrix. The depot is the first row/column, patient 1 the
     * second row/column, and so on. For example, index (2, 3) and (3, 2) the distance between
     * patient 1 and 2. The travel times are floats, so do not round them
     * [
     * [0, 2, 5, ...],
     * [2, 0, 3, ...],
     * ...
     * ]
     *
     * @return the travel times matrix
     */
    public Vector<Vector<Double>> getTravel_times() {
        return travel_times;
    }

    /**
     * The type Depot.
     */
    public static class Depot {
        private int return_time;
        private int x_coord;
        private int y_coord;

        /**
         * Instantiates a new Depot.
         *
         * @param return_time the return time
         * @param x_coord     the x coord
         * @param y_coord     the y coord
         */
        public Depot(int return_time, int x_coord, int y_coord) {
            this.return_time = return_time;
            this.x_coord = x_coord;
            this.y_coord = y_coord;
        }

        /**
         * Gets return time.
         *
         * @return the return time
         */
        public int getReturn_time() {
            return return_time;
        }

        /**
         * Sets return time.
         *
         * @param return_time the return time
         */
        public void setReturn_time(int return_time) {
            this.return_time = return_time;
        }

        /**
         * Gets x coord.
         *
         * @return the x coord
         */
        public int getX_coord() {
            return x_coord;
        }

        /**
         * Sets x coord.
         *
         * @param x_coord the x coord
         */
        public void setX_coord(int x_coord) {
            this.x_coord = x_coord;
        }

        /**
         * Gets y coord.
         *
         * @return the y coord
         */
        public int getY_coord() {
            return y_coord;
        }

        /**
         * Sets y coord.
         *
         * @param y_coord the y coord
         */
        public void setY_coord(int y_coord) {
            this.y_coord = y_coord;
        }
    }

    /**
     * The type Patient.
     */
    public static class Patient {
        private int id;
        private final int x_coord;
        private final int y_coord;
        private final int demand;
        private final int start_time;
        private final int end_time;
        private final int care_time;
        private int range;

        private int cluster;

        /**
         * Instantiates a new Patient.
         *
         * @param id
         * @param x_coord    the x coord
         * @param y_coord    the y coord
         * @param demand     the demand
         * @param start_time the start time
         * @param end_time   the end time
         * @param care_time  the care time
         */
        public Patient(int id, int x_coord, int y_coord, int demand, int start_time, int end_time, int care_time) {
            this.id = id;
            this.x_coord = x_coord;
            this.y_coord = y_coord;
            this.demand = demand;
            this.start_time = start_time;
            this.end_time = end_time;
            this.care_time = care_time;
            this.cluster = -1;
            this.range = 0;
        }

        /**
         * Gets id.
         *
         * @return the id
         */
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        /**
         * Gets x coord.
         *
         * @return the x coord
         */
        public int getX_coord() {
            return x_coord;
        }

        /**
         * Gets y coord.
         *
         * @return the y coord
         */
        public int getY_coord() {
            return y_coord;
        }

        /**
         * Gets demand.
         *
         * @return the demand
         */
        public int getDemand() {
            return demand;
        }

        /**
         * Gets start time.
         *
         * @return the start time
         */
        public int getStart_time() {
            return start_time;
        }

        /**
         * Gets end time.
         *
         * @return the end time
         */
        public int getEnd_time() {
            return end_time;
        }
        /**
         * Gets care time.
         *
         * @return the care time
         */
        public int getCare_time() {
            return care_time;
        }

        /**
         * Gets cluster.
         *
         * @return the cluster
         */
        public int getCluster() {
            return cluster;
        }

        /**
         * Gets range.
         *
         * @return the range
         */
        public int getRange() {
            return range;
        }

        /**
         * Sets cluster.
         *
         * @param cluster the cluster
         */
        public void setCluster(int cluster) {
            this.cluster = cluster;
        }

        public void calculateRange() {
            this.range = start_time+end_time-demand;
        }

        @Override
        public String toString() {
            return "Patient{" +
                    "id=" + id +
                    ", x_coord=" + x_coord +
                    ", y_coord=" + y_coord +
                    ", demand=" + demand +
                    ", start_time=" + start_time +
                    ", end_time=" + end_time +
                    ", care_time=" + care_time +
                    ", range=" + range +
                    ", cluster=" + cluster +
                    '}';
        }
    }

    public static class Cluster {
        private ArrayList<Patient> patients;
        private int demand;
        private int concurrentOverlaps;
        private int start_time;
        private int end_time;

        /**
         * Instantiates a new Cluster.
         *
         * @param cluster_idx the cluster idx
         * @param patients    the patients
         */
        public Cluster(int cluster_idx, ArrayList<Patient> patients) {
            this.patients = patients;
            this.demand = 0;
            this.concurrentOverlaps = 0;
            this.start_time = Integer.MAX_VALUE;
            this.end_time = 0;

            // Iterate over all patients and set fields
            for (Patient patient : patients) {
                // Set cluster_idx for the patient
                patient.setCluster(cluster_idx);

                // Update cluster demand
                this.demand += patient.getDemand();

                // Update cluster start_time
                if (this.start_time < patient.getStart_time()) {
                    this.start_time = patient.getStart_time();
                }

                // Update cluster end_time
                if (this.end_time > patient.getEnd_time()) {
                    this.end_time = patient.getEnd_time();
                }

                // TODO: Update concurrentOverlaps in cluster
                int maxOverlaps = 0;
            }
        }

        /**
         * Gets patients.
         *
         * @return the patients
         */
        public ArrayList<Patient> getPatients() {
            return patients;
        }

        /**
         * Gets demand.
         *
         * @return the demand
         */
        public int getDemand() {
            return demand;
        }

        /**
         * Gets concurrent overlaps.
         *
         * @return the concurrent overlaps
         */
        public int getConcurrentOverlaps() {
            return concurrentOverlaps;
        }

        /**
         * Gets start time.
         *
         * @return the start time
         */
        public int getStart_time() {
            return start_time;
        }

        /**
         * Gets end time.
         *
         * @return the end time
         */
        public int getEnd_time() {
            return end_time;
        }
    }
}

