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
    private final Float init_random_rate;
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
     * @param init_random_rate  the initial random rate (completely random bitstring
     *                          generation)
     */
    public SGA(
            ObjectiveFunction objectiveFunction,
            Boolean maximize,
            Integer pop_size,
            Integer max_generations,
            Float crossover_rate,
            Float mutation_rate,
            Float init_random_rate,
            DataHandler data) {
        this.objectiveFunction = objectiveFunction;
        this.maximize = maximize;
        this.pop_size = pop_size;
        this.max_generations = max_generations;
        this.crossover_rate = crossover_rate;
        this.mutation_rate = mutation_rate;
        this.init_random_rate = init_random_rate;
        this.data = data;
        this.generations = new ArrayList<>();
    }

    public void simulate() {
        Population population = init_population(init_random_rate);
        System.out.print(population);
        this.generations.add(population);

    /**
     * Parent selection done by roulette wheel.
     * 
     * @param population population to select from
     * @param nParents   number of parents
     * @return the selected parents
     */
    private ArrayList<Individual> parent_selection(Population population, Integer nParents) {
        if (nParents % 2 != 0) {
            throw new IllegalArgumentException("nParents=" + nParents + " is invalid! Number must be even");
        }

        // Sum fitness values
        List<Double> fitnessValues = new ArrayList<>();
        population.getFeasible_individuals().forEach(individual -> fitnessValues.add(individual.getFitness()));

        // Scale values
        double minValue = 5 + Collections.min(fitnessValues);
        fitnessValues.forEach(v -> v += minValue);
        double totalFitness = fitnessValues.stream().mapToDouble(f -> f.doubleValue()).sum();

        // Create probability distribution
        List<Double> individualProbability = new ArrayList<>();
        fitnessValues.forEach(fitness -> individualProbability.add(fitness / totalFitness));

        // Select parents
        Random random = new Random();
        ArrayList<Individual> parents = new ArrayList<>();
        for (int i = 0; i < nParents; i++) {
            double randomValue = random.nextDouble();
            double sum = 0;
            int idx = -1;
            for (idx = 0; i < individualProbability.size(); idx++) {
                sum += individualProbability.get(idx);
                if (sum > randomValue) {
                    break;
                }
            }
            parents.add(population.getFeasible_individuals().get(idx));
        }
        return parents;
    }

    private ArrayList<Individual> generateOffspring(ArrayList<Individual> matingPool, Float crossoverRate,
            Float mutationRate) {
        ArrayList<Individual> offsprings = new ArrayList<>();

        // perform crossover and mutation
        for (int i = 1; i < matingPool.size(); i = i + 2) {
            // get parent pair
            Individual parent1 = matingPool.get(i - 1);
            Individual parent2 = matingPool.get(i);

            ArrayList<Individual> newOffsprings = null;
            boolean feasibleSolution = false;
            while (!feasibleSolution) {
                // create new individuals from the crossover
                newOffsprings = crossover(parent1, parent2, crossoverRate);

                // Mutate
                mutation(newOffsprings, mutationRate);

                // check constraints
                feasibleSolution = true;
                for (Individual offspring : newOffsprings) {
                    if (objectiveFunction.check_constraints(offspring) == false) {
                        feasibleSolution = false;
                    }
                }
            }
            // add individuals
            offsprings.addAll(newOffsprings);
        }
        return offsprings;
    }

    private ArrayList<Individual> crossover(Individual parent1, Individual parent2, Float crossoverRate) {
        // deep copy = new objects
        Individual child1 = parent1.createChild(parent1, parent2);
        Individual child2 = parent2.createChild(parent1, parent2);

        // do crossover
        Random random = new Random();
        if (random.nextFloat() < crossoverRate) {
            // select random nurse
            Integer route1 = random.nextInt((this.data.getNbr_nurses()));
            Integer route2 = random.nextInt((this.data.getNbr_nurses()));
            reassignPatients(child1, child2.getBitstring().get(route1));
            reassignPatients(child2, child1.getBitstring().get(route2));
        }
        ArrayList<Individual> offsprings = new ArrayList<>();
        offsprings.add(child1);
        offsprings.add(child2);
        return offsprings;
    }

    private void reassignPatients(Individual parent, ArrayList<Integer> selectedPatients) {
        if (selectedPatients.size() == 0) {
            return;
        }

        // remove patients from parent
        ArrayList<Integer> removedPatients = new ArrayList<>();
        for (int nurse_idx = 0; nurse_idx < this.data.getNbr_nurses(); nurse_idx++) {
            ArrayList<Integer> patients = parent.getBitstring().get(nurse_idx);
            for (Integer selectedPatient : selectedPatients) {
                if (patients.contains(selectedPatient)) {
                    patients.remove(selectedPatient);
                    removedPatients.add(selectedPatient);
                }
            }
            // selectedPatients.removeAll(removedPatients);
        }

        // for each removed patient, try to find best insertion
        for (Integer removedPatient : removedPatients) {
            int bestNurse = 0;
            double minIncrease = 0;

            // Try to assign to each nurse, pick the one with the least increase in travel
            // time
            for (int nurse_idx = 0; nurse_idx < this.data.getNbr_nurses(); nurse_idx++) {
                // Create lists
                ArrayList<Integer> patients = parent.getBitstring().get(nurse_idx);
                ArrayList<Integer> patients_clone = (ArrayList<Integer>) patients.clone();

                // Add patient and check route decrease/increase
                patients_clone.add(removedPatient);
                boolean feasible = this.objectiveFunction.optimizeRoute(patients_clone);
                if (feasible) {
                    double routeIncrease = this.objectiveFunction.getTravelTimeRoute(patients_clone)
                            - this.objectiveFunction.getTravelTimeRoute(patients);
                    if (routeIncrease < minIncrease) {
                        bestNurse = nurse_idx;
                        minIncrease = routeIncrease;
                    }
                }
            }
        }
    }

    private void mutation(ArrayList<Individual> newOffsprings, Float mutationRate) {
        Random random = new Random();
        for (Individual offspring : newOffsprings) {
            if (random.nextFloat() < mutationRate) {
                // TODO: do mutation
                // Options:
                // intra move: move a patient to earlier/later visit
                // intra swap: swap two patient for one employee
                // inter move: move patient from one nurse to another
                // inter swap: swap two patient visits between nurses
            }
        }
    }

    private Population survivor_selection(Population oldPopulation, ArrayList<Individual> offspring) {
        return null;
    }

    private Population init_population(float init_random_rate) {
        Population population = new Population();
        int individuals = 0;
        Random random = new Random();
        boolean retryHeuristic = false;

        while (individuals < this.pop_size) {
            try {
                Individual individual = null;
                if (random.nextFloat() > init_random_rate || retryHeuristic) {
                    individual = new Individual(generate_bitstring_heuristic(2000));
                } else {
                    individual = new Individual(generate_bitstring_random());
                }
                objectiveFunction.calculate_fitness(individual);

                // Check constraints
                if (objectiveFunction.check_constraints(individual)) {
                    population.getFeasible_individuals().add(individual);
                } else {
                    population.getInfeasible_individuals().add(individual);
                }
                System.out.println(individuals);
                individuals++;
                retryHeuristic = false;
            } catch (Exception e) {
                retryHeuristic = true;
            }
        }

        System.out.println("Feasible solutions: " + population.getFeasible_individuals().size());
        return population;
    }

    /**
     * Generate a random bitstring:
     * [
     * [1,5,7], // nurse 1 visits patient 1, 5 and 7
     * [2,8,4] // nurse 2 visits patient 2, 8 and 4
     * ]
     * 
     * @return the bitstring
     */
    private HashMap<Integer, ArrayList<Integer>> generate_bitstring_random() {
        // create bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = create_bitstring();

        // Prepare list of patients
        List<Integer> patients = IntStream.rangeClosed(1, this.data.getPatients().size()).boxed()
                .collect(Collectors.toList());

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
     * 
     * @return the bitstring
     */
    private HashMap<Integer, ArrayList<Integer>> generate_bitstring_heuristic(long timeout) throws TimeoutException {
        // timeout
        final long startTime = System.nanoTime();
        final long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(timeout);
        Random random = new Random();

        // create bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = create_bitstring();

        // Prepare nurses
        ArrayList<Nurse> nurses = new ArrayList<>();
        for (int i = 0; i < this.data.getNbr_nurses(); i++) {
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
        String selectedPatientSortOption = patientSortOptions.get(random.nextInt(patientSortOptions.size()));

        // Sort nurses
        Boolean sortNurses = random.nextBoolean();

        for (DataHandler.Cluster cluster : clusters) {
            // Get cluster
            ArrayList<DataHandler.Patient> cluster_patients = cluster.getPatients();

            // Sort patients
            switch (selectedPatientSortOption) {
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
                    throw new IllegalArgumentException("Invalid sort option: " + selectedPatientSortOption);
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
                    Double arrival_time = nurse.getOccupied_until()
                            + data.getTravel_times().get(nurse.getPosition()).get(patient.getId());
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
            for (int i = 0; i < cluster_nurses.size(); i++) {
                nurses.remove(i); // Remove used nurses
                nurses.add(cluster_nurses.get(i)); // add back at the end
            }

            // Sort nurses
            if (sortNurses == true) {
                nurses.sort(Comparator.comparing(Nurse::getOccupied_until));
            }
        }
        return bitstring;
    }

    /**
     * Create a bitstring representation where each key represent a nurse and the
     * arraylist represent visited patients
     * 
     * @return the bitstring
     */
    private HashMap<Integer, ArrayList<Integer>> create_bitstring() {
        // Prepare bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = new HashMap<>();
        for (int i = 0; i < this.data.getNbr_nurses(); i++) {
            bitstring.put(i, new ArrayList<>());
        }
        return bitstring;
    }
}
