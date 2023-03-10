package no.ntnu.it3708.Project_2;

import java.util.HashSet;
import java.util.Set;

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
    public boolean check_individual_constraints(Individual individual) {
        int current_position = 0; // Depot
        double start_time = 0;
        int total_demand = 0;

        // Iterate over the patients and check patient constraints
        int[] bitstring = individual.getBitstring();
        for (int i = 0; i < bitstring.length; i++) {
            int patient_id = i+1;   // Patients start at 1
            // Check if nurse is visiting patient
            if (bitstring[patient_id] == 1) {
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
//                    System.out.println("care_time_finish="+care_time_finish+" > patients_end_time="+patient.getEnd_time());
                    return false;
                }

                // Update variables
                current_position = patient_id;
                start_time = care_time_finish;
                total_demand += patient.getDemand();

                // Check if the nurse has surpassed its capacity
                if (data.getCapacity_nurse() < total_demand) {
                    return false;
                }
            }
        }

        // Check depot constraints
        DataHandler.Depot depot = data.getDepot();
        double end_time = start_time + data.getTravel_times().get(current_position).get(0);
        if (end_time > depot.getReturn_time()) {
            return false;
        }
        return true;
    }

    /**
     * Checks that the individuals do not violate the populations constraints.
     * @param population the population
     * @return satisfies the constraints
     */
    public boolean check_population_constraints(Population population) {
        // Check that each patient is visited on only one route
        Set<Integer> patient_visits = new HashSet<>();

        for (Individual nurse : population.getInfeasible_individuals()) {
            int[] bitstring = nurse.getBitstring();
            for (int i = 0; i < bitstring.length; i++) {
                int patient_id = i+1;   // Patients start at 1

                // Check if nurse is visiting patient
                if (bitstring[patient_id] == 1) {
                    patient_visits.add(patient_id);
                }
            }
        }

        // check that patient visists is the same size as the number of patients
        if (patient_visits.size() != data.getPatients().size()) {
            System.out.println("Broke population constraint");
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
    public float calculate_fitness(Individual individual) {
        return 0f;
    }
}
