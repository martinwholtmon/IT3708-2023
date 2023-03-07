package no.ntnu.it3708.Project_2;

public class ObjectiveFunction {
    private final DataHandler data;

    public ObjectiveFunction(DataHandler data) {
        this.data = data;
    }

    public boolean check_constraints(Individual individual) {
        return false;
    }

    public float get_fitness(Individual individual) {
        return 0f;
    }
}
