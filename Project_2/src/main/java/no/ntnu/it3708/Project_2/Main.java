package no.ntnu.it3708.Project_2;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        // Load data
        DataHandler data = new DataHandler();
        data.loadData("/data/train_0.json");
        data.cluster_patients(150, 1d);

        // Prepare params
        ObjectiveFunction objectiveFunction = new ObjectiveFunction(data);
        boolean maximize = false;
        int pop_size = 100;
        int max_generations = 100;
        float crossover_rate = 0.6f;
        float mutation_rate = 0.05f;
        float init_random_rate = 0f;
        int localSearchIterations = 100;

        // Execute the SGA
        SGA sga = new SGA(objectiveFunction, maximize, pop_size, max_generations, crossover_rate, mutation_rate,
                init_random_rate, localSearchIterations, data);
        sga.simulate();
    }
}
