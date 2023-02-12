import numpy as np
import copy
from random import random


class Individual:
    """This class represent an individual in a population"""

    def __init__(
        self,
        bitstring: np.ndarray[int],
        parents: "list[Individual]" = None,
    ) -> None:
        self.bitstring = bitstring
        self.parents = parents or []
        self.value: float = 0
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

    def __str__(self):
        return (
            f"Generation {self.generation_nr} avg. fitness: {self.__calc_avg_fitness()}"
        )

    def __calc_avg_fitness(self):
        fitness = sum([individual.fitness for individual in self.individuals])
        return fitness / len(self.individuals)


class SGA:
    """This class represent a simple genetic algorithm"""

    def __init__(
        self,
        objective_function: callable,
        maximize: bool = True,
        pop_size: int = 1000,
        individual_size: int = 15,
        max_generations: int = 15,
        crossover_rate: float = 0.6,
        mutation_rate: float = 0.06,
    ) -> None:
        self.objective_function = objective_function
        self.maximize = maximize
        self.pop_size = pop_size
        self.individual_size = individual_size
        self.max_generations = max_generations
        self.crossover_rate = crossover_rate
        self.mutation_rate = mutation_rate

    def simulate(self) -> Individual:
        population = self.__init_population()
        print(population)
        while population.generation_nr < self.max_generations:
            population = self.__generation(population)
            print(population)
        return select_fittest_individuals(population.individuals, 1, self.maximize)[0]

    def __init_population(self) -> Population:
        """Initialize a population in the SGA

        Returns:
            Population: A new population in the SGA
        """
        new_population = Population()
        for _ in range(self.pop_size):
            new_population.individuals.append(
                Individual(bitstring=generate_bitstring(self.individual_size))
            )
        self.objective_function(new_population.individuals)
        new_population.calc_avg_fitness()
        return new_population

    def __generation(self, population: Population) -> Population:
        """Create a new generation

        Args:
            population (Population): Old population

        Returns:
            Population: New population
        """
        # Select pool of parents
        mating_pool = parent_selection(population, self.pop_size, self.maximize)

        # Create offspring with chance of mutation
        offsprings = crossover(mating_pool, self.crossover_rate)
        for offspring in offsprings:
            mutation(offspring, self.mutation_rate)

        # TODO: calculate fitness of offspring
        self.objective_function(offsprings)

        # return new generation
        new_generation = survivor_selection_fittest(
            offsprings, population, self.maximize
        )
        new_generation.calc_avg_fitness()
        return new_generation


def generate_bitstring(individual_size: int) -> np.ndarray[int]:
    """Generate a bitstring for an individual: [1 1 1 0 1 0 1 0 1]

    Args:
        individual_size (int): bitstring size/shape

    Returns:
        list[int]: bitstring as a list of integers
    """
    return np.random.randint(0, 2, individual_size)


def parent_selection(
    population: Population, num_parents: int, maximize: bool
) -> "list[Individual]":
    """Select the fittest individuals in a population proportionally, by the use of roulette wheel

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

    # Get all fitness values
    if maximize:
        population_fitness = [
            individual.fitness for individual in population.individuals
        ]
    else:
        # To minimize, negate the fitness value (maximize the negation of the fitness)
        population_fitness = [
            -individual.fitness for individual in population.individuals
        ]

    # Scale to positive values -> keep proportions
    min_fitness = min(population_fitness)
    if min_fitness < 0:
        # Scale with a base of 1 to not have the sum be zero.
        population_fitness = [
            (fitness + abs(min_fitness) + 1) for fitness in population_fitness
        ]
    population_fitness_sum = sum(population_fitness)

    # Create probabilities
    individual_probabilities = [
        fitness / population_fitness_sum for fitness in population_fitness
    ]

    # Return the parents
    return np.random.choice(
        population.individuals,
        size=num_parents,
        p=individual_probabilities,
    ).tolist()


def crossover(parents: "list[Individual]", crossover_rate: float) -> "list[Individual]":
    """Creates offsprings from pairs of parents through single point crossover

    Returns:
        list[Individual]: The offsprings
    """
    # Prepare copies
    offspring = copy.deepcopy(parents)

    # Chance of crossover
    if random() <= crossover_rate:
        # Select a point between (0,len(bitstring)-1) -> [1,len(bitstring)-2]
        x_point = np.random.randint(1, len(parents[0].bitstring) - 1)
        for i in range(1, len(offspring), 2):  # Every other, e.g. pairs
            # Prepare parents
            p1, p2 = parents[i - 1], parents[i]

            # Mutate copies
            # Keep the beginning of the bitstring, swap the last part from the other parent
            # Could have been done in place without the use of parents, but this improves readability.
            # E.g. o1[x:], o2[x:] = o2[x:].copy, o1[x:].copy()
            offspring[i - 1].bitstring[x_point:] = p2.bitstring[x_point:].copy()
            offspring[i].bitstring[x_point:] = p1.bitstring[x_point:].copy()
    return offspring


def mutation(individual: Individual, mutation_rate):
    """Mutate the individual. Controlled by the mutation_rate
    Iterate over the bits, and given a chance, mutate.

    Args:
        individual (Individual): An individual
        mutation_rate (float): Probability of mutation

    Returns:
        Individual: Mutated individual
    """
    for bit_idx in individual.bitstring:
        if random() <= mutation_rate:
            # XOR -> flip bit
            individual.bitstring[bit_idx] = individual.bitstring[bit_idx] ^ 1


def survivor_selection(
    individuals: "list[Individual]", old_population: Population
) -> Population:
    """For the SGA, the survivor selection is simply a generational replacement of the prior population

    Args:
        individuals (list[Individual]): The new individuals

    Returns:
        Population: The new population
    """
    new_generation = Population()
    new_generation.prev_gen = old_population
    new_generation.generation_nr = old_population.generation_nr + 1
    new_generation.individuals = individuals
    return new_generation


def survivor_selection_fittest(
    individuals: "list[Individual]", old_population: Population, maximize: bool
) -> Population:
    """Select the fittest individuals in a set of individuals

    Args:
        individuals (list[Individual]): _description_
        old_population (Population): _description_

    Returns:
        Population: _description_
    """
    # add parents to individuals
    individuals.extend(old_population.individuals)

    # Create new generation
    new_generation = Population()
    new_generation.prev_gen = old_population
    new_generation.generation_nr = old_population.generation_nr + 1
    new_generation.individuals = select_fittest_individuals(
        individuals, len(old_population.individuals), maximize
    )
    return new_generation


def select_fittest_individuals(
    individuals: "list[Individual]", n_individuals, maximize: bool
) -> "list[Individual]":
    """Select fittest individuals in a population

    Args:
        pop (Population): A population
        n_individuals (int): Number of individuals to select

    Returns:
        list[Individual]: Fittest individuals
    """
    return sorted(individuals, key=lambda i: i.fitness, reverse=maximize)[
        :n_individuals
    ]
