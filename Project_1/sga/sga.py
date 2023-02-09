import math
import numpy as np
import heapq
import copy
from random import random


class Individual:
    """This class represent an individual in a population"""

    def __init__(
        self,
        bitstring: list[int],
        parents: "list[Individual]" = None,
    ) -> None:
        self.bitstring = bitstring
        self.parents = parents or []
        self.fitness: float = 0


class Population:
    """This class represent a population in the SGA"""

    def __init__(
        self,
        individuals: "list[Individual]" = None,
        prev_gen: "Population" = None,
        generation_nr: int = 0,
    ) -> None:
        self.individuals = individuals or []
        self.prev_gen = prev_gen
        self.generation_nr = generation_nr


class SGA:
    """This class represent a simple genetic algorithm"""

    def __init__(
        self,
        pop_size: int = 1000,
        individual_size: int = 15,
        max_generations: int = 0,
        crossover_rate: float = 0,
        mutation_rate: float = 0,
    ) -> None:
        self.pop_size = pop_size
        self.individual_size = individual_size
        self.max_generations = max_generations
        self.crossover_rate = crossover_rate
        self.mutation_rate = mutation_rate
        self.generations = []

    def init_population(self) -> Population:
        """Initialize a population in the SGA

        Returns:
            Population: A new population in the SGA
        """
        new_population = Population()
        for _ in range(self.pop_size):
            new_population.individuals.append(
                Individual(bitstring=generate_bitstring(self.individual_size))
            )
        self.generations.append(new_population)
        return new_population


def generate_bitstring(individual_size: int) -> "list[int]":
    """Generate a bitstring for an individual: [1,1,1,0,1,0,1,0,1]

    Args:
        individual_size (int): bitstring size/shape

    Returns:
        list[int]: bitstring as a list of integers
    """
    return np.random.randint(0, 2, individual_size).tolist()


def calc_fitness(population: Population) -> float:
    """calculate the fitness value in the range [0, 1]
    scaling factor = upper bound / max value

    Returns:
        float: fitness value
    """
    scaling_factor = 2 ** (-8)
    for individual in population.individuals:
        phenotype = int("".join(map(str, individual.bitstring)), 2)
        individual.fitness = phenotype * scaling_factor


def parent_selection(
    population: Population, num_parents: int = 2
) -> "list[Individual]":
    """Select the fittest individuals in a population

    Args:
        population (Population): A population
        num_parents (int, optional): How many parents to select. Defaults to 2.

    Returns:
        list[Individual]: The fittest individuals
    """
    # check for even number
    if num_parents % 2 != 0:
        raise ValueError(
            f"num_parents={num_parents} is invalid! Number of parents must be even"
        )
    return select_fittest_individuals(population, num_parents)


def crossover(
    parents: "list[Individual]", crossover_rate: float
) -> "tuple[Individual, Individual]":
    """Creates offsprings from pairs of parents through single point crossover

    Returns:
        list[Individual]: The offsprings
    """
    # Prepare copies
    offspring = copy.deepcopy(parents)

    # Chance of crossover
    if random() < crossover_rate:
        # TODO: How to handle crossover point
        crossover_point = math.floor(0.5 * len(parents[0].bitstring))
        for i in range(1, len(offspring), 2):  # Every other, e.g. pairs
            # Prepare parents
            p1, p2 = parents[i - 1], parents[i]

            # Mutate copies
            offspring[i - 1].bitstring = (
                p1.bitstring[:crossover_point] + p2.bitstring[crossover_point:]
            )
            offspring[i].bitstring = (
                p2.bitstring[:crossover_point] + p1.bitstring[crossover_point:]
            )
    return offspring


def mutation(individual: Individual, mutation_rate) -> Individual:
    """Mutate the individual. Controlled by the mutation_rate
    Iterate over the bits, and given a chance, mutate.

    Args:
        individual (Individual): An individual
        mutation_rate (float): Probability of mutation

    Returns:
        Individual: Mutated individual
    """
    for bit_idx in individual.bitstring:
        if random() < mutation_rate:
            # XOR -> flip bit
            individual.bitstring[bit_idx] = individual.bitstring[bit_idx] ^ 1
    return individual


def survivor_selection(population: Population, pop_size: int) -> Population:
    """Selects the survivors of a population based on the individuals fitness
    Select the fittest individuals in the new population, to create the next generation.

    Args:
        poulation (Population): A population
        pop_size (int): Target size of the population

    Returns:
        population: New generation
    """
    # Prepare new generation
    new_generation = Population()
    new_generation.prev_gen = population
    new_generation.generation_nr = population.generation_nr
    new_generation.individuals = select_fittest_individuals(population, pop_size)
    return new_generation


def select_fittest_individuals(pop: Population, n_individuals) -> "list[Individual]":
    """Select fittest individuals in a population

    Args:
        pop (Population): A population
        n_individuals (int): Number of individuals to select

    Returns:
        list[Individual]: Fittest individuals
    """
    # Get all the individuals fitness and find the largest
    individuals = [i.fitness for i in pop.individuals]
    p_bit = heapq.nlargest(
        n_individuals, enumerate(individuals), key=lambda x: x[1]
    )  # [(index, fitness), ...}

    # Get get parents
    parents = []
    for index, _ in p_bit:
        parents.append(pop.individuals[index])
    return parents
