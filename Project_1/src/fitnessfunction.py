"""The fitness (objective) functions that operate on the individuals
"""
import math
from sga import Population


def sine(population: Population) -> float:
    """calculate the fitness value in the interval [0, 128]
    scaling factor = ((upper_bound - lower_bound) / max_value) + lower_bound

    Returns:
        float: fitness value
    """
    scaling_factor = 2 ** (-8)
    for individual in population.individuals:
        phenotype = int("".join(map(str, individual.bitstring)), 2)
        individual.fitness = math.sin(phenotype * scaling_factor)


def lin_reg(population: Population) -> float:
    return NotImplementedError
