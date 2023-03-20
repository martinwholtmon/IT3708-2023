package no.ntnu.it3708.Project_2;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        // Load data
        DataHandler data = new DataHandler();
        data.loadData("/data/train_0.json");

        // Prepare params
        ObjectiveFunction objectiveFunction = new ObjectiveFunction(data);
        boolean maximize = false;
        int pop_size = 36;
        int max_generations = 100;
        float crossover_rate = 0.6f;
        float mutation_rate = 0.1f;
        float init_random_rate = 1f;
        int localSearchIterations = 100;

        SGA bestSga = null;
        double bestFitness = Double.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            SGA sga = new SGA(objectiveFunction, maximize, pop_size, max_generations, crossover_rate, mutation_rate,
                    init_random_rate, localSearchIterations, data);
            sga.run();
            Individual individual = sga.getBestIndividual();
            if (individual.getFitness() < bestFitness) {
                bestFitness = individual.getFitness();
                bestSga = sga;
            }
        }

        // print solution
        System.out.println("\n\nBest individual: ");
        bestSga.printBestSolution();
    }
}
