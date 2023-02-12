"""The fitness (objective) functions that operate on the individuals
"""
import math
from src.sga import Individual


def sine(individuals: "list[Individual]") -> float:
    """calculate the fitness value in the interval [0, 128]
    scaling factor = ((upper_bound - lower_bound) / max_value) + lower_bound

    Returns:
        float: average fitness value for a population
    """
    scaling_factor = 2 ** (-8)
    for individual in individuals:
        # Get fitness
        phenotype = int("".join(map(str, individual.bitstring)), 2)
        fitness = math.sin(phenotype * scaling_factor)

        # Set fitness
        individual.fitness = fitness
        individual.value = phenotype


# def lin_reg(population: Population) -> float:
#     return NotImplementedError
