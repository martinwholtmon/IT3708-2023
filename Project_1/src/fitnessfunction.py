"""The fitness (objective) functions that operate on the individuals
"""
import math
from src.sga import Individual
from src.lin_reg import LinReg


class ObjectiveSine:
    def __init__(self) -> None:
        pass

    def get_fitness(self, individuals: "list[Individual]") -> float:
        """calculate the fitness value in the interval [0, 128]
        scaling factor = ((upper_bound - lower_bound) / max_value) + lower_bound

        Returns:
            float: average fitness value for a population
        """
        scaling_factor = 2 ** (-8)
        for individual in individuals:
            # Get fitness
            phenotype = int("".join(map(str, individual.bitstring)), 2)
            value = phenotype * scaling_factor
            fitness = math.sin(value)

            # Set fitness
            individual.fitness = fitness
            individual.value = value


class ObjectiveLinReg:
    def __init__(self, X, y, seed=None) -> None:
        self.X = X
        self.y = y
        self.seed = seed
        self.model: LinReg = LinReg()

    def get_fitness(self, individuals: "list[Individual]") -> float:
        for individual in individuals:
            # Get columns
            X_sub = self.model.get_columns(self.X, individual.bitstring)

            # Get fitness
            fitness = self.model.get_fitness(X_sub, self.y, self.seed)

            # Set fitness
            individual.fitness = fitness
