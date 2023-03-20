package no.ntnu.it3708.Project_2;

import com.google.gson.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static no.ntnu.it3708.Project_2.Helpers.*;

/**
 * The Sga.
 */
public class SGA {
    private final ConstraintsHandler constraintsHandler;
    private final Integer pop_size;
    private final Integer max_generations;
    private final Float crossover_rate;
    private final Float mutation_rate;
    private final Float init_random_rate;
    private final int localSearchIterations;
    private final DataHandler data;
    private ArrayList<DataHandler.Cluster> clusters;
    private ArrayList<Population> generations;
    private Random random;

    /**
     * Instantiates a new Sga.
     *
     * @param constraintsHandler the objective function
     * @param pop_size          the pop size
     * @param max_generations   the max generations
     * @param crossover_rate    the crossover rate
     * @param mutation_rate     the mutation rate
     * @param init_random_rate  the initial random rate (completely random bitstring
     *                          generation)
     */
    public SGA(
            ConstraintsHandler constraintsHandler,
            Integer pop_size,
            Integer max_generations,
            Float crossover_rate,
            Float mutation_rate,
            Float init_random_rate,
            int localSearchIterations,
            DataHandler data) {
        this.constraintsHandler = constraintsHandler;
        this.pop_size = pop_size;
        this.max_generations = max_generations;
        this.crossover_rate = crossover_rate;
        this.mutation_rate = mutation_rate;
        this.init_random_rate = init_random_rate;
        this.localSearchIterations = localSearchIterations;
        this.data = data;
        this.clusters = data.cluster_patients(50, 1d);
        this.random = new Random();
        this.generations = new ArrayList<>();
    }

    public void run() {
        Population population = init_population(init_random_rate);
        System.out.println(population);
        this.generations.add(population);

        // Run the SGA loop
        while (population.getGeneration_nr() < this.max_generations) {
            population = newGeneration(population);

            // perform LNS
            // System.out.println("Performing local search on best solution:");
            Individual bestIndividual = population.get_best_solution();
            Individual localSearchIndividual = performLocalSearch(bestIndividual);

            // try to remove bad visit on route, and reassign to better route
            // objectiveFunction.optimizeRoute(localSearchIndividual);

            if (localSearchIndividual.getFitness() < bestIndividual.getFitness()) {
                population.replaceBestIndividual(localSearchIndividual);
            }

            // Print
            System.out.println(population);
            this.generations.add(population);
        }
    }

    public Individual getBestIndividual() {
        return this.generations.get(this.generations.size() - 1).get_best_solution();
    }

    private Population newGeneration(Population oldPopulation) {
        // Get parents
        ArrayList<Individual> matingPool = parent_selection(oldPopulation, this.pop_size);

        // Create offspring
        ArrayList<Individual> offspring = generateOffspring(matingPool, this.crossover_rate, this.mutation_rate);

        // Calculate fitness
        for (Individual individual : offspring) {
            constraintsHandler.calculate_fitness(individual);
        }
        return survivor_selection(oldPopulation, offspring);
    }

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
        ArrayList<Individual> parents = new ArrayList<>();
        for (int i = 0; i < nParents; i++) {
            double randomValue = this.random.nextDouble();
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
            int feasibleSolutions = 0;
            while (feasibleSolutions < 2) {
                // create new individuals from the crossover
                newOffsprings = crossover(parent1, parent2, crossoverRate);

                // Mutate
                mutation(newOffsprings, mutationRate);

                // check constraints
                for (Individual offspring : newOffsprings) {
                    if (constraintsHandler.check_constraints(offspring)) {
                        offsprings.add(offspring);
                        feasibleSolutions++;
                    }
                }
            }
        }
        return offsprings;
    }

    private ArrayList<Individual> crossover(Individual parent1, Individual parent2, Float crossoverRate) {
        // deep copy = new objects
        Individual child1 = parent1.createChild(parent1, parent2);
        Individual child2 = parent2.createChild(parent1, parent2);

        // do crossover
        if (this.random.nextFloat() < crossoverRate) {
            boolean done = false;
            while (!done) {
                try {
                    // select random nurse
                    Integer route1 = this.random.nextInt((this.data.getNbr_nurses()));
                    Integer route2 = this.random.nextInt((this.data.getNbr_nurses()));
                    reassignPatients(child1, child2.getBitstring().get(route1));
                    reassignPatients(child2, child1.getBitstring().get(route2));
                    done = true;
                } catch (Exception e) {
                }
            }
        }
        ArrayList<Individual> offsprings = new ArrayList<>();
        offsprings.add(child1);
        offsprings.add(child2);
        return offsprings;
    }

    private void reassignPatients(Individual parent, ArrayList<Integer> selectedPatients)
            throws IndexOutOfBoundsException {
        if (selectedPatients.size() == 0) {
            return;
        }

        // remove patients from parent
        ArrayList<Integer> removedPatients = new ArrayList<>();
        for (int nurse_idx = 0; nurse_idx < this.data.getNbr_nurses(); nurse_idx++) {
            ArrayList<Integer> patients = (ArrayList<Integer>) parent.getBitstring().get(nurse_idx).clone();
            for (Integer selectedPatient : selectedPatients) {
                if (patients.contains(selectedPatient)) {
                    patients.remove(selectedPatient);
                    removedPatients.add(selectedPatient);
                }
            }
        }

        // for each removed patient, try to find best insertion
        for (Integer removedPatient : removedPatients) {
            int bestNurse = -1;
            ArrayList<Integer> bestRoute = null;
            double minIncrease = Double.MAX_VALUE;

            // Try to assign to each nurse, pick the one with the least increase in travel
            // time
            for (int nurse_idx = 0; nurse_idx < this.data.getNbr_nurses(); nurse_idx++) {
                // Create lists
                ArrayList<Integer> patients = parent.getBitstring().get(nurse_idx);
                ArrayList<Integer> patients_clone = (ArrayList<Integer>) patients.clone();

                // Add patient and check route decrease/increase
                boolean feasible = this.constraintsHandler.optimizedInsert(patients_clone, removedPatient);
                if (feasible) {
                    double routeIncrease = this.constraintsHandler.getTravelTimeRoute(patients_clone)
                            - this.constraintsHandler.getTravelTimeRoute(patients);
                    if (routeIncrease < minIncrease) {
                        bestNurse = nurse_idx;
                        bestRoute = patients_clone;
                        minIncrease = routeIncrease;
                    }
                }
            }
            // set new route
            if (bestNurse != -1) {
                parent.getBitstring().put(bestNurse, bestRoute);
            } else {
                throw new IndexOutOfBoundsException("Not able to insert patient");
            }
        }
    }

    private void mutation(ArrayList<Individual> newOffsprings, Float mutationRate) {
        for (int i = 0; i < newOffsprings.size(); i++) {
            constraintsHandler.calculate_fitness(newOffsprings.get(i));
            if (this.random.nextFloat() < mutationRate) {
                Individual offspring = performLocalSearch(newOffsprings.get(i));
                newOffsprings.set(i, offspring);
            }
        }
    }

    private Individual performLocalSearch(Individual individual) {
        Individual bestSolution = individual.deepCopy();
        double bestFitness = bestSolution.getFitness();

        for (int iterations = 0; iterations < localSearchIterations; iterations++) {
            // Create neighboring solutions
            ArrayList<Individual> neighboringSolutions = createNeighboringSolutions(individual);

            for (Individual neighborSolution : neighboringSolutions) {
                if (neighborSolution.getFitness() < bestFitness) {
                    bestSolution = neighborSolution;
                    bestFitness = neighborSolution.getFitness();
                }
            }
        }
        return bestSolution;
    }

    private ArrayList<Individual> createNeighboringSolutions(Individual individual) {
        ArrayList<Individual> neighboringSolutions = new ArrayList<>();

        for (int i = 0; i < pop_size; i++) {
            boolean foundSolution = false;
            while (!foundSolution) {
                Individual newIndividual = individual.deepCopy();

                // Perform Local Search Operator
                List<String> mutationOption = Arrays.asList("intraMove", "intraSwap", "interMove", "interSwap");
                String selectedMutationOption = mutationOption.get(this.random.nextInt(mutationOption.size()));

                HashMap<Integer, ArrayList<Integer>> bitstring = newIndividual.getBitstring();
                int nurse_idx1 = this.random.nextInt(this.data.getNbr_nurses());
                int nurse_idx2 = this.random.nextInt(this.data.getNbr_nurses());
                ArrayList<Integer> patients1 = bitstring.get(nurse_idx1);
                ArrayList<Integer> patients2 = bitstring.get(nurse_idx2);

                switch (selectedMutationOption) {
                    case "intraMove":
                        // intra move: move a patient to earlier/later visit
                        if (patients1.size() > 1) {
                            int patientToMoveIdx = getRandomPatientIndex(patients1, random);
                            int patientToMove = patients1.get(patientToMoveIdx);
                            patients1.remove(patientToMoveIdx);

                            // add back
                            patients1.add(getRandomPatientIndex(patients1, random), patientToMove);
                        }
                        break;
                    case "intraSwap":
                        // intra swap: swap two patient for one employee
                        if (patients1.size() > 2) {
                            int pos1 = getRandomPatientIndex(patients1, random);
                            int pos2 = getRandomPatientIndex(patients1, random);
                            if (pos1 != pos2) {
                                Collections.swap(patients1, pos1, pos2);
                            }
                        } else if (patients1.size() == 2) {
                            Collections.swap(patients1, 0, 1);
                        }
                        break;
                    case "interMove":
                        // inter move: move patient from one nurse to another
                        if (patients1.size() > 0 && patients2.size() > 0) {
                            int patient1Idx = getRandomPatientIndex(patients1, random);
                            int patient1 = patients1.get(patient1Idx);
                            patients1.remove(patient1Idx);

                            // add
                            patients2.add(getRandomPatientIndex(patients2, random), patient1);
                        }
                        break;
                    case "interSwap":
                        // inter swap: swap two patient visits between nurses
                        if (patients1.size() > 0 && patients2.size() > 0) {
                            // get position.
                            int patient1Idx = getRandomPatientIndex(patients1, random);
                            int patient2Idx = getRandomPatientIndex(patients2, random);

                            // get patients
                            int patient1 = patients1.get(patient1Idx);
                            int patient2 = patients2.get(patient2Idx);

                            // swap
                            patients1.set(patient1Idx, patient2);
                            patients2.set(patient2Idx, patient1);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid sort option: " + selectedMutationOption);
                }

                // Add solution if its acceptable
                if (constraintsHandler.check_constraints(newIndividual)) {
                    constraintsHandler.calculate_fitness(newIndividual);
                    neighboringSolutions.add(newIndividual);
                    foundSolution = true;
                }
            }
        }
        return neighboringSolutions;
    }

    /**
     * Pick the best survivors given their fitness values. Elitism apprach
     *
     * @param oldPopulation The old population
     * @param offspring     the new population
     * @return The new generation
     */
    private Population survivor_selection(Population oldPopulation, ArrayList<Individual> offspring) {
        // combine individuals
        ArrayList<Individual> allIndividuals = new ArrayList<>();
        allIndividuals.addAll(oldPopulation.getFeasible_individuals());
        allIndividuals.addAll(offspring);
        allIndividuals.sort(Comparator.comparing(Individual::getFitness));

        // pick individuals
        ArrayList<Individual> newIndividuals = new ArrayList<>(allIndividuals.subList(0, this.pop_size / 2));

        // create new population
        return new Population(newIndividuals, oldPopulation, oldPopulation.getGeneration_nr() + 1);
    }

    private Population init_population(float init_random_rate) {
        Population population = new Population();
        population.getFeasible_individuals().addAll(getNewIndividuals(pop_size));
        System.out.println("Feasible solutions: " + population.getFeasible_individuals().size());
        return population;
    }

    private ArrayList<Individual> getNewIndividuals(int n_individuals) {
        ArrayList<Individual> individuals = new ArrayList<>();
        int currentIndividuals = 0;
        boolean retryHeuristic = false;

        while (currentIndividuals < this.pop_size) {
            try {
                Individual individual = null;
                if (this.random.nextFloat() > init_random_rate || retryHeuristic) {
                    individual = new Individual(generate_bitstring_heuristic(this.clusters, this.data, this.random, 2000));
                } else {
                    individual = new Individual(generate_bitstring_random(this.data, this.random, 2000));
                }
                constraintsHandler.calculate_fitness(individual);

                // Check constraints
                if (constraintsHandler.check_constraints(individual)) {
                    individuals.add(individual);
                } else {
                    individuals.add(individual);
                }
                System.out.println(currentIndividuals);
                currentIndividuals++;
                retryHeuristic = false;
            } catch (Exception e) {
                retryHeuristic = true;
            }
        }
        return individuals;
    }

    public void printBestSolution() {
        Individual bestIndividual = this.getBestIndividual();
        System.out.println(bestIndividual);
        Helpers.printSolution(this.data, bestIndividual);
    }
}
