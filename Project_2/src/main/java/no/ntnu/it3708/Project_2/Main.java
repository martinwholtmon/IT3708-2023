package no.ntnu.it3708.Project_2;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        // Load data
        DataHandler data = new DataHandler();
        data.loadData("/data/test_0.json");

        // Prepare params
        ConstraintsHandler constraintsHandler = new ConstraintsHandler(data);
        int pop_size = 36;
        int max_generations = 100; // 100 / 120 / 150 / 100
        float crossover_rate = 0.6f; // 0.6 / 0.6 / 0.10 / 0.6f
        float mutation_rate = 0.1f; // 0.1 / 0.1 / 0.01 / 0.1f
        float init_random_rate = 1f; // 0.0 / 0.2 / 0.20 / 0.9f
        int localSearchIterations = 100;

        SGA bestSga = null;
        double bestFitness = Double.MAX_VALUE;

        Scanner input = new Scanner(System.in);
        for (int i = 0; i < 35; i++) {
            SGA sga = new SGA(constraintsHandler, pop_size, max_generations, crossover_rate, mutation_rate,
                    init_random_rate, localSearchIterations, data);
            sga.run();
            Individual individual = sga.getBestIndividual();
            if (individual.getFitness() < bestFitness) {
                bestFitness = individual.getFitness();
                bestSga = sga;

                System.out.println("\n\n");
                System.out.println("-------------------------------------");
                System.out.println("Temp Print of current best solution:");
                System.out.println("-------------------------------------");
                bestSga.printBestSolution();
            }

            // Pause
            if (i % 5 == 0) {
                System.out.println("\n\n");
                System.out.println("-------------------------------------");
                System.out.println("Temp Print of current best solution:");
                System.out.println("-------------------------------------");
                bestSga.printBestSolution();

                System.out.println("\nPress enter to continue");
                String name = input.nextLine();
            }

        }

        // print solution
        System.out.println("\n\nBest individual: ");
        bestSga.printBestSolution();
    }
}
