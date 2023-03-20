package no.ntnu.it3708.Project_2;

import java.util.*;

import static no.ntnu.it3708.Project_2.Helpers.getRandomPatientIndex;

/**
 * The type Local search.
 */
public class LocalSearch {
    /**
     * Perform local search individual.
     *
     * @param individual            the individual
     * @param localSearchIterations the local search iterations
     * @param pop_size              the pop size
     * @param data                  the data
     * @param constraintsHandler    the constraints handler
     * @param random                the random
     * @return the individual
     */
    public static Individual performLocalSearch(Individual individual, int localSearchIterations, int pop_size, DataHandler data, ConstraintsHandler constraintsHandler, Random random) {
        Individual bestSolution = individual.deepCopy();
        double bestFitness = bestSolution.getFitness();

        for (int iterations = 0; iterations < localSearchIterations; iterations++) {
            // Create neighboring solutions
            ArrayList<Individual> neighboringSolutions = createNeighboringSolutions(individual, pop_size, data, constraintsHandler, random);

            for (Individual neighborSolution : neighboringSolutions) {
                if (neighborSolution.getFitness() < bestFitness) {
                    bestSolution = neighborSolution;
                    bestFitness = neighborSolution.getFitness();
                }
            }
        }
        return bestSolution;
    }

    /**
     * Create neighboring solutions array list.
     *
     * @param individual         the individual
     * @param pop_size           the pop size
     * @param data               the data
     * @param constraintsHandler the constraints handler
     * @param random             the random
     * @return the array list
     */
    public static ArrayList<Individual> createNeighboringSolutions(Individual individual, int pop_size, DataHandler data, ConstraintsHandler constraintsHandler, Random random) {
        ArrayList<Individual> neighboringSolutions = new ArrayList<>();

        for (int i = 0; i < pop_size; i++) {
            boolean foundSolution = false;
            while (!foundSolution) {
                Individual newIndividual = individual.deepCopy();

                // Perform Local Search Operator
                List<String> mutationOption = Arrays.asList("intraMove", "intraSwap", "interMove", "interSwap");
                String selectedMutationOption = mutationOption.get(random.nextInt(mutationOption.size()));

                HashMap<Integer, ArrayList<Integer>> bitstring = newIndividual.getBitstring();
                int nurse_idx1 = random.nextInt(data.getNbr_nurses());
                int nurse_idx2 = random.nextInt(data.getNbr_nurses());
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
}
