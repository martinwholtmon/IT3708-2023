package no.ntnu.it3708.Project_2;

import com.google.common.collect.Collections2;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * The Objective function.
 * Used for calculating the fitness and checking constraints.
 */
public class ConstraintsHandler {
    private final DataHandler data;

    /**
     * Instantiates a new Objective function.
     *
     * @param data the data
     */
    public ConstraintsHandler(DataHandler data) {
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
        for (int nurse_id = 0; nurse_id < data.getNbr_nurses(); nurse_id++) {
            ArrayList<Integer> patients = bitstring.get(nurse_id);
            visited_patients.addAll(patients);
            if (!routeIsFeasible(patients)) {
                return false;
            }
        }
        // check that patient visists is the same size as the number of patients = all
        // patients visited
        return visited_patients.size() == data.getPatients().size();
    }

    /**
     * Calculates the fitness of an individual.
     *
     * @param individual the individual
     */
    public void calculate_fitness(Individual individual) {
        // Sum all travel times
        Double travel_time = 0d;

        HashMap<Integer, ArrayList<Integer>> bitstring = individual.getBitstring();
        for (int nurse_idx = 0; nurse_idx < bitstring.size(); nurse_idx++) {
            travel_time += getTravelTimeRoute(bitstring.get(nurse_idx));
        }
        individual.setFitness(travel_time);
    }

    /**
     * Given a route, bruteforce to optimize the order while keeping it feasible
     *
     * @param route patient visits for a nurse
     * @return It 's not possible to generate a feasible route using the current         visits
     */
    public boolean optimizeRouteBF(ArrayList<Integer> route) {
        System.out.println("before:\n" + route);

        boolean feasible = false;
        double travelTime = Double.MAX_VALUE;

        for (List<Integer> r : Collections2.permutations(route)) {
            ArrayList<Integer> tmpRoute = new ArrayList<>(r);
            if (routeIsFeasible(tmpRoute)) {
                // get travel time
                double currentTravelTime = getTravelTimeRoute(tmpRoute);

                // if better, check if feasible.
                if (currentTravelTime < travelTime) {
                    // Update
                    feasible = true;
                    travelTime = currentTravelTime;
                    route = tmpRoute;
                }
            }
        }
        System.out.println("After:\n" + route);
        return feasible;
    }

    /**
     * Optimize routes individual.
     *
     * @param individual the individual
     * @return the individual
     */
    public Individual optimizeRoutes(Individual individual) {
        Individual bestSolution = individual.deepCopy();

        // Logic:
        // Go over each route. If distance from n to n+1 is large, remove n+1.
        // then check next..
        // Try to assign the patients again

        // Remove bad patients from route
        return bestSolution;
    }

    /**
     * Insert a new visit in the best position
     *
     * @param route    The route to modify
     * @param newVisit The new patient to visit
     * @return Feasible to add the new patient
     */
    public boolean optimizedInsert(ArrayList<Integer> route, Integer newVisit) {
        boolean feasible = false;
        double travelTime = Double.MAX_VALUE;

        ArrayList<Integer> orgRoute = makeDeepCopyInteger(route);
        for (int pos = 0; pos < route.size(); pos++) {
            ArrayList<Integer> tmpRoute = makeDeepCopyInteger(orgRoute);
            tmpRoute.add(pos, newVisit);
            if (routeIsFeasible(tmpRoute)) {
                // get travel time
                double currentTravelTime = getTravelTimeRoute(tmpRoute);

                // if better, check if feasible.
                if (currentTravelTime < travelTime) {
                    // Update
                    feasible = true;
                    travelTime = currentTravelTime;
                    route = tmpRoute;
                }
            }
        }
        return feasible;
    }

    private ArrayList<Integer> makeDeepCopyInteger(ArrayList<Integer> a) {
        return (ArrayList<Integer>) a.stream().map(Integer::new).collect(toList());
    }

    /**
     * Check that a route is feasible
     *
     * @param route list of patients to visit
     * @return boolean
     */
    private boolean routeIsFeasible(ArrayList<Integer> route) {
        int current_position = 0; // Depot
        double start_time = 0;
        int total_demand = 0;

        // Iterate over the patients that the nurse visits
        if (route.size() == 0) {
            return true;
        }

        // iterate over all the patients
        for (int patient_id : route) {
            // Get patient details
            DataHandler.Patient patient = data.getPatients().get(patient_id);

            // Set arrival_time given the patients start_time
            double arrival_time = start_time + data.getTravel_times().get(current_position).get(patient_id);
            if (arrival_time < patient.getStart_time()) {
                // Must wait for time window, update arrival time
                arrival_time = patient.getStart_time();
            }

            // Check that we finish before the patients end_time
            double care_time_finish = arrival_time + patient.getCare_time();
            if (care_time_finish > patient.getEnd_time()) {
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
        // Check depot constraints
        double end_time = start_time + data.getTravel_times().get(current_position).get(0);
        return !(end_time > data.getDepot().getReturn_time());
    }

    /**
     * Calculate the travel time on a given route
     *
     * @param route patient visits for a nurse
     * @return travel time
     */
    public double getTravelTimeRoute(ArrayList<Integer> route) {
        Double travel_time = 0d;

        int pos = 0; // depot
        for (int patient_idx : route) {
            travel_time += data.getTravel_times().get(pos).get(patient_idx);
            pos = patient_idx;
        }

        // back to depot
        travel_time += data.getTravel_times().get(pos).get(0);

        return travel_time;
    }
}
