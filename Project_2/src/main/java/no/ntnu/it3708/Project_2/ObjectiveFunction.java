package no.ntnu.it3708.Project_2;

import java.util.*;

/**
 * The Objective function.
 * Used for calculating the fitness and checking constraints.
 */
public class ObjectiveFunction {
    private final DataHandler data;

    /**
     * Instantiates a new Objective function.
     *
     * @param data the data
     */
    public ObjectiveFunction(DataHandler data) {
        this.data = data;
    }

    /**
     * Checks that the individual do not violate the individual constraints.
     *
     * @param individual the individual
     * @return satisfies the constraints
     */
    public boolean check_constraints(Individual individual) {
        // Iterate over the nurses and the assign patients,
        // and check patient constraints
        HashMap<Integer, ArrayList<Integer>> bitstring = individual.getBitstring();
        Set<Integer> visited_patients = new HashSet<>();

        // Iterate over the nurses
        for (int nurse_id=0; nurse_id < data.getNbr_nurses(); nurse_id++) {
            int current_position = 0; // Depot
            double start_time = 0;
            int total_demand = 0;

            // Iterate over the patients that the nurse visits
            ArrayList<Integer> patients = bitstring.get(nurse_id);
            if (patients.size() == 0) {
                continue;   // just skip constraints checking for nurse if no patients
            }

            // iterate over all the patients
            for (int patient_id : patients) {
                // Get patient details
                DataHandler.Patient patient = data.getPatients().get(patient_id);

                // Set arrival_time given the patients start_time
                double arrival_time = start_time + data.getTravel_times().get(current_position).get(patient_id);
                if (arrival_time < patient.getStart_time()) {
                    // Must wait for time window, update arrival time
                    arrival_time = (double) patient.getStart_time();
                }

                // Check that we finish before the patients end_time
                double care_time_finish = arrival_time + patient.getCare_time();
                if (care_time_finish > patient.getEnd_time()) {
                    return false;
                }

                // Update variables
                visited_patients.add(patient_id);
                current_position = patient_id;
                start_time = care_time_finish;
                total_demand += patient.getDemand();

                // Check if the nurse has surpassed its capacity
                if (data.getCapacity_nurse() < total_demand) {
                    return false;
                }
            }
            // Check depot constraints
            DataHandler.Depot depot = data.getDepot();
            double end_time = start_time + data.getTravel_times().get(current_position).get(0);
            if (end_time > depot.getReturn_time()) {
                return false;
            }
        }
        // check that patient visists is the same size as the number of patients = all patients visited
        if (visited_patients.size() != data.getPatients().size()) {
            return false;
        }
        return true;
    }

    /**
     * Calculates the fitness of an individual.
     *
     * @param individual the individual
     * @return the fitness
     */
    public void calculate_fitness(Individual individual) {
        // Sum all travel times
        Double travel_time = 0d;

        for (Map.Entry<Integer, ArrayList<Integer>> entry : individual.getBitstring().entrySet()) {
            int nurse_idx = entry.getKey();
            ArrayList<Integer> patients = entry.getValue();

            int current_pos = 0;
            for (int patient_idx : patients) {
                travel_time += data.getTravel_times().get(current_pos).get(patient_idx);
                current_pos = patient_idx;
            }
        }
        individual.setFitness(travel_time);
    }
}
