package no.ntnu.it3708.Project_2;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        // Load data
        DataHandler data = new DataHandler();
        data.loadData("/data/train_0.json");

        // Prepare params
        ConstraintsHandler constraintsHandler = new ConstraintsHandler(data);
        int pop_size = 36;
        int max_generations = 100;
        float crossover_rate = 0.6f;
        float mutation_rate = 0.1f;
        float init_random_rate = 0.2f;
        int localSearchIterations = 100;

        SGA bestSga = null;
        double bestFitness = Double.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            SGA sga = new SGA(constraintsHandler, pop_size, max_generations, crossover_rate, mutation_rate,
                    init_random_rate, localSearchIterations, data);
            sga.run();
            System.out.println("\n\n");
            System.out.println("-------------------------------------");
            System.out.println("Temp Print of current best solution:");
            System.out.println("-------------------------------------");
            sga.printBestSolution();
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
