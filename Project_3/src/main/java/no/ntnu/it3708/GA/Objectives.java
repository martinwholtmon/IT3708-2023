package no.ntnu.it3708.GA;

import java.util.Comparator;

public enum Objectives {
    EdgeValue,
    Connectivity,
    Deviation;

    public static Comparator<Individual> getComparator(Objectives objective) {
        return switch (objective) {
            case EdgeValue -> Comparator.comparingDouble(Individual::getEdgeValue);
            case Connectivity -> Comparator.comparingDouble(Individual::getConnectivity);
            case Deviation -> Comparator.comparingDouble(Individual::getDeviation);
        };
    }

    public static double getObjective(Objectives objective, Individual individual) {
        return switch (objective) {
            case EdgeValue -> individual.getEdgeValue();
            case Connectivity -> individual.getConnectivity();
            case Deviation -> individual.getDeviation();
        };
    }
}
