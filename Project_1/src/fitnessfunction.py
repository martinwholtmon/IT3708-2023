"""The fitness (objective) functions that operate on the individuals
"""
import math
from sga import Population


def sine(population: Population) -> float:
    """calculate the fitness value in the interval [0, 128]
    scaling factor = ((upper_bound - lower_bound) / max_value) + lower_bound

    Returns:
        float: average fitness value for a population
    """
    scaling_factor = 2 ** (-8)
    total_fitness = 0
    for individual in population.individuals:
        # Get fitness
        phenotype = int("".join(map(str, individual.bitstring)), 2)
        fitness = math.sin(phenotype * scaling_factor)

        # Set fitness
        individual.fitness = fitness
        total_fitness = total_fitness + fitness
    # Set avg fitness for population
    population.fitness = total_fitness / len(population.individuals)
    print(population.fitness)


def lin_reg(population: Population) -> float:
    return NotImplementedError
