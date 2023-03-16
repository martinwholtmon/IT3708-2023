package no.ntnu.it3708.Project_2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
        for (int i=0; i<this.pop_size; i++) {
            Individual individual = new Individual(generate_random_bitstring());
            objectiveFunction.calculate_fitness(individual);

            // Check constraints
            if (objectiveFunction.check_constraints(individual)) {
                population.getFeasible_individuals().add(individual);
            } else {
                population.getInfeasible_individuals().add(individual);
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
    private HashMap<Integer, ArrayList<Integer>> generate_random_bitstring() {
        // Prepare bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = new HashMap<>();
        for (int i=0; i<this.data.getNbr_nurses(); i++) {
            bitstring.put(i, new ArrayList<>());
        }

        // Prepare list of patients
        List<Integer> patients = IntStream.rangeClosed(1, this.data.getNbr_nurses()).boxed().collect(Collectors.toList());

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
    private HashMap<Integer, ArrayList<Integer>> generate_bitstring_heuristic() {
        // create bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = create_bitstring();

        // Prepare list of nurses
        List<Integer> nurses = IntStream.rangeClosed(0, this.data.getNbr_nurses()-1).boxed().collect(Collectors.toList());

        // Find out how many nurses to assign
        int nurses_per_cluster = 2;

        // Iterate over the clusters and assign  nurses
        Random rand = new Random();
        HashMap<Integer, DataHandler.Cluster> clusters = data.getClusters();
        for (int cluster_idx = 0; cluster_idx < clusters.size(); cluster_idx++) {
            DataHandler.Cluster cluster = clusters.get(cluster_idx);
            ArrayList<DataHandler.Patient> cluster_patients = cluster.getPatients();

            List<Integer> cluster_nurses = new ArrayList<>();
            for (int i=0; i < nurses_per_cluster; i++) {
                int nurse_idx = rand.nextInt(nurses.size());
                cluster_nurses.add(nurses.get(nurse_idx));
                nurses.remove(nurse_idx);
            }

            // Randomly add patients to assigned nurses
            for (DataHandler.Patient patient : cluster_patients) {
                int nurse_id = rand.nextInt(cluster_nurses.size());
                bitstring.get(cluster_nurses.get(nurse_id)).add(patient.getId());
            }

            // TODO: Sort patients
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
