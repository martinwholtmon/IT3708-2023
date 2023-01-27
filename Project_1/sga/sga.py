import numpy as np
import heapq


class Individual:
    """This class represent an individual in a population"""

    def __init__(
        self,
        bitstring: list[int],
        fitness: float = 0,
        parents: "list[Individual]" = None,
    ) -> None:
        self.bitstring = bitstring
        self.fitness = fitness
        self.parents = parents or []
        self.phenotype: int = 0

    def calc_phenotype(self) -> None:
        """Will calculate the phenotype (binary to integer) given the individuals bitstring."""
        self.phenotype = int("".join(map(str, self.bitstring)), 2)


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

    def parent_selection(self) -> "list[Individual]":
        """Select the two fittest individuals in a population

        Returns:
            tuple[Individual, Individual]: The two fittest individuals
        """
        # Get all the individuals fitness and find the two largest
        individuals = [i.fitness for i in self.individuals]
        p_bit = heapq.nlargest(2, enumerate(individuals), key=lambda x: x[1])

        # Get get parents
        parents = []
        for index, _ in p_bit:
            parents.append(self.individuals[index])
        return parents


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
            individual = Individual(bitstring=generate_bitstring(self.individual_size))
            individual.calc_phenotype()

            # Append new individual
            new_population.individuals.append(individual)
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
