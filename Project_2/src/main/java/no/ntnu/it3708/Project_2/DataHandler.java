package no.ntnu.it3708.Project_2;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DataHandler {
    private String instance_name;
    private  int nbr_nurses;
    private int capacity_nurse;
    private final Depot depot;
    private final HashMap<Integer, Patient> patients;
    private final Vector<Vector<Double>> travel_times;

    public DataHandler() {
        this.instance_name = "";
        this.nbr_nurses = 0;
        this.capacity_nurse = 0;
        this.depot = null;
        this.patients = new HashMap<>();
        this.travel_times = new Vector<>();
    }

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

    public String getInstance_name() {
        return instance_name;
    }

    public int getNbr_nurses() {
        return nbr_nurses;
    }

    public int getCapacity_nurse() {
        return capacity_nurse;
    }

    public Depot getDepot() {
        return depot;
    }

    public HashMap<Integer, Patient> getPatients() {
        return patients;
    }

    public Vector<Vector<Double>> getTravel_times() {
        return travel_times;
    }

    private static class Depot {
        private int return_time;
        private int x_coord;
        private int y_coord;

        public Depot(int return_time, int x_coord, int y_coord) {
            this.return_time = return_time;
            this.x_coord = x_coord;
            this.y_coord = y_coord;
        }

        public int getReturn_time() {
            return return_time;
        }

        public void setReturn_time(int return_time) {
            this.return_time = return_time;
        }

        public int getX_coord() {
            return x_coord;
        }

        public void setX_coord(int x_coord) {
            this.x_coord = x_coord;
        }

        public int getY_coord() {
            return y_coord;
        }

        public void setY_coord(int y_coord) {
            this.y_coord = y_coord;
        }
    }

    private static class Patient {
        private final int x_coord;
        private final int y_coord;
        private final int demand;
        private final int start_time;
        private final int end_time;
        private final int care_time;

        public Patient(int x_coord, int y_coord, int demand, int start_time, int end_time, int care_time) {
            this.x_coord = x_coord;
            this.y_coord = y_coord;
            this.demand = demand;
            this.start_time = start_time;
            this.end_time = end_time;
            this.care_time = care_time;
        }

        public int getX_coord() {
            return x_coord;
        }

        public int getY_coord() {
            return y_coord;
        }

        public int getDemand() {
            return demand;
        }

        public int getStart_time() {
            return start_time;
        }

        public int getEnd_time() {
            return end_time;
        }

        public int getCare_time() {
            return care_time;
        }
    }
}

