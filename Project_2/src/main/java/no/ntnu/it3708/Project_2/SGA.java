package no.ntnu.it3708.Project_2;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The Sga.
 */
public class SGA {
    private final ObjectiveFunction objectiveFunction;
    private final Boolean maximize;
    private final Integer pop_size;
    private final Integer max_generations;
    private final Float crossover_rate;
    private final Float mutation_rate;
    private final DataHandler data;
    private ArrayList<Population> generations;

    /**
     * Instantiates a new Sga.
     *
     * @param objectiveFunction the objective function
     * @param maximize          if maximize
     * @param pop_size          the pop size
     * @param max_generations   the max generations
     * @param crossover_rate    the crossover rate
     * @param mutation_rate     the mutation rate
     */
    public SGA(
            ObjectiveFunction objectiveFunction,
            Boolean maximize,
            Integer pop_size,
            Integer max_generations,
            Float crossover_rate,
            Float mutation_rate,
            DataHandler data) {
        this.objectiveFunction = objectiveFunction;
        this.maximize = maximize;
        this.pop_size = pop_size;
        this.max_generations = max_generations;
        this.crossover_rate = crossover_rate;
        this.mutation_rate = mutation_rate;
        this.data = data;
        this.generations = new ArrayList<>();
    }

    public void simulate() {
        Population population = init_population();
        System.out.print(population);
        this.generations.add(population);

//        // Run the SGA loop
//        while (population.getGeneration_nr() < this.max_generations) {
//
//        }

    }

    private Population init_population() {
        Population population = new Population();
        int individuals = 0;

        while (individuals<this.pop_size) {
            try {
                Individual individual = new Individual(generate_bitstring_heuristic(2000));
                objectiveFunction.calculate_fitness(individual);

                // Check constraints
                if (objectiveFunction.check_constraints(individual)) {
                    population.getFeasible_individuals().add(individual);
                } else {
                    population.getInfeasible_individuals().add(individual);
                }
                System.out.println(individuals);
                individuals++;
            } catch (Exception e) {
//                System.out.println(e);
            }
        }

        System.out.println("Feasible solutions: " + population.getFeasible_individuals().size());
        return population;
    }

    /**
     * Generate a random bitstring:
     *  [
     *      [1,5,7],    // nurse 1 visits patient 1, 5 and 7
     *      [2,8,4]     // nurse 2 visits patient 2, 8 and 4
     *  ]
     * @return the bitstring
     */
    private HashMap<Integer, ArrayList<Integer>> generate_bitstring_random() {
        // create bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = create_bitstring();

        // Prepare list of patients
        List<Integer> patients = IntStream.rangeClosed(1, this.data.getPatients().size()).boxed().collect(Collectors.toList());

        // Randomly assign patient to nurse
        Random rand = new Random();
        while (patients.size() > 0) {
            int nurse_id = rand.nextInt(this.data.getNbr_nurses());
            int patient_idx = rand.nextInt(patients.size());
            int patient_id = patients.get(patient_idx);
            bitstring.get(nurse_id).add(patient_id);
            patients.remove(patient_idx);
        }
        return bitstring;
    }

    /**
     * Generate a bitstring using the heuristic in data: clusters of patients
     * @return the bitstring
     */
    private HashMap<Integer, ArrayList<Integer>> generate_bitstring_heuristic(long timeout) throws TimeoutException {
        // timeout
        final long startTime = System.nanoTime();
        final long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(timeout);
        Random random = new Random();

        // create bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = create_bitstring();

        // Prepare  nurses
        ArrayList<Nurse> nurses = new ArrayList<>();
        for (int i=0; i < this.data.getNbr_nurses(); i++) {
            Nurse nurse = new Nurse(i, data.getCapacity_nurse());
            nurses.add(nurse);
        }

        // Iterate over the clusters and assign nurses
        ArrayList<DataHandler.Cluster> clusters = data.getClusters();

        // Set randomness variables
        // Sort clusters
        List<String> clusterSortOptions = Arrays.asList("shuffle", "demand", "start_time");
        String selectedClusterSortOption = clusterSortOptions.get(random.nextInt(clusterSortOptions.size()));
        switch (selectedClusterSortOption) {
            case "shuffle":
                Collections.shuffle(clusters, random);
                break;
            case "demand":
                clusters.sort(Comparator.comparing(DataHandler.Cluster::getDemand));
                break;
            case "start_time":
                clusters.sort(Comparator.comparing(DataHandler.Cluster::getStart_time));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort option: " + selectedClusterSortOption);
        }

        // Sort patients
        List<String> patientSortOptions = Arrays.asList("shuffle", "demand", "range", "start_time");
        String selected_patient_sort_option = patientSortOptions.get(random.nextInt(patientSortOptions.size()));

        // Sort nurses
        Boolean sort_nurses = random.nextBoolean();


        for (DataHandler.Cluster cluster : clusters) {
            // Get cluster
            ArrayList<DataHandler.Patient> cluster_patients = cluster.getPatients();

            // Sort patients
            switch (selected_patient_sort_option) {
                case "shuffle":
                    Collections.shuffle(cluster_patients);
                    break;
                case "demand":
                    cluster_patients.sort(Comparator.comparing(DataHandler.Patient::getDemand));
                    break;
                case "range":
                    cluster_patients.sort(Comparator.comparing(DataHandler.Patient::getRange));
                    break;
                case "start_time":
                    cluster_patients.sort(Comparator.comparing(DataHandler.Patient::getStart_time));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid sort option: " + selected_patient_sort_option);
            }


            // Assign nurses
            // Logic: Have a list of nurses. Iterate over patients
            // - Select patient
            // - Check if nurse(s) at location is available
            // - else: pop off new nurse from stack
            // - loop
            // No more patient, move the nurses to top (last) of stack
            ArrayList<Nurse> cluster_nurses = new ArrayList<>();
            for (DataHandler.Patient patient : cluster_patients) {
                Integer nurse_idx = -1;
                Nurse nurse = null;
                Double end_time = 0d;

                Boolean foundNurse = false;
                while (!foundNurse) {
                    // Reached timeout
                    final long elapsedNanos = System.nanoTime() - startTime;
                    final long timeLeftNanos = timeoutNanos - elapsedNanos;
                    if (timeLeftNanos <= 0) {
                        throw new TimeoutException();
                    }


                    // select nurse
                    nurse_idx++;
                    try {
                        // Try to get the nurse
                        nurse = cluster_nurses.get(nurse_idx);
                    } catch (IndexOutOfBoundsException exception) {
                        // Does not exist, try to add a new nurse
                        try {
                            nurse = nurses.get(nurse_idx);
                            cluster_nurses.add(nurse);
                        } catch (EmptyStackException exception1) {
                            System.out.println("No more nurses");
                        }
                    }

                    // Update arrival_time
                    Double arrival_time = nurse.getOccupied_until() + data.getTravel_times().get(nurse.getPosition()).get(patient.getId());
                    if (arrival_time < patient.getStart_time()) {
                        arrival_time = (double) patient.getStart_time();
                    }

                    // finish within the time window
                    end_time = arrival_time + patient.getCare_time();
                    if (end_time > patient.getEnd_time()) {
                        continue;
                    }

                    // Still has capacity
                    if (nurse.getCapacity() < patient.getDemand()) {
                        continue;
                    }

                    // Finish before depot limit
                    Double depot_arrival_time = end_time + data.getTravel_times().get(nurse.getPosition()).get(0);
                    if (depot_arrival_time > data.getDepot().getReturn_time()) {
                        continue;
                    }

                    // Use this nurse
                    foundNurse = true;
                }

                // Assign nurse
                nurse.setPosition(patient.getId());
                nurse.reduceCapacity(patient.getDemand());
                nurse.setOccupied_until(end_time);

                // Update bitstring
                bitstring.get(nurse.getId()).add(patient.getId());
            }

            // Move nurses
            for (int i=0; i < cluster_nurses.size(); i++) {
                nurses.remove(i);                   // Remove used nurses
                nurses.add(cluster_nurses.get(i));  // add back at the end
            }

            // Sort nurses
            if (sort_nurses = true) {
                nurses.sort(Comparator.comparing(Nurse::getOccupied_until));
            }
        }
        return bitstring;
    }

    /**
     * Create a bitstring representation where each key represent a nurse and the arraylist represent visited patients
     * @return the bitstring
     */
    private HashMap<Integer, ArrayList<Integer>> create_bitstring() {
        // Prepare bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = new HashMap<>();
        for (int i=0; i<this.data.getNbr_nurses(); i++) {
            bitstring.put(i, new ArrayList<>());
        }
        return bitstring;
    }
}
