package no.ntnu.it3708.Project_2;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * The Data handler.
 */
public class DataHandler {
    private String instance_name;
    private  int nbr_nurses;
    private int capacity_nurse;
    private final Depot depot;
    private final HashMap<Integer, Patient> patients;
    private final Vector<Vector<Double>> travel_times;

    /**
     * Instantiates a new Data handler.
     */
    public DataHandler() {
        this.instance_name = "";
        this.nbr_nurses = 0;
        this.capacity_nurse = 0;
        this.depot = null;
        this.patients = new HashMap<>();
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
        Depot depot = gson.fromJson(obj.get("depot"), Depot.class);

        // Get patients
        JsonObject patients_json = obj.getAsJsonObject("patients");
        for (Map.Entry<String, JsonElement> entry : patients_json.entrySet()) {
            Patient patient = gson.fromJson(entry.getValue(), Patient.class);

            // add patient
            this.patients.put(Integer.valueOf(entry.getKey()), patient);
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
     * Gets travel times in the form of a matrix. The depot is the first row/column, patient 1 the
     * second row/column, and so on. For example, index (2, 3) and (3, 2) the distance between
     * patient 1 and 2. The travel times are floats, so do not round them
     * [
     *     [0, 2, 5, ...],
     *     [2, 0, 3, ...],
     *     ...
     * ]
     *
     * @return the travel times matrix
     */
    public Vector<Vector<Double>> getTravel_times() {
        return travel_times;
    }

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

    public static class Patient {
        private final int x_coord;
        private final int y_coord;
        private final int demand;
        private final int start_time;
        private final int end_time;
        private final int care_time;

        /**
         * Instantiates a new Patient.
         *
         * @param x_coord    the x coord
         * @param y_coord    the y coord
         * @param demand     the demand
         * @param start_time the start time
         * @param end_time   the end time
         * @param care_time  the care time
         */
        public Patient(int x_coord, int y_coord, int demand, int start_time, int end_time, int care_time) {
            this.x_coord = x_coord;
            this.y_coord = y_coord;
            this.demand = demand;
            this.start_time = start_time;
            this.end_time = end_time;
            this.care_time = care_time;
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
    }
}

