"""The fitness (objective) functions that operate on the individuals
"""
from sga import Population


def sine(population: Population) -> float:
    """calculate the fitness value in the range [0, 1]
    scaling factor = upper bound / max value

    Returns:
        float: fitness value
    """
    scaling_factor = 2 ** (-8)
    for individual in population.individuals:
        phenotype = int("".join(map(str, individual.bitstring)), 2)
        individual.fitness = phenotype * scaling_factor


def lin_reg(population: Population) -> float:
    return NotImplementedError
