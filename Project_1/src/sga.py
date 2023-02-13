from src.individual import Individual
from src.population import Population
import numpy as np
import copy
from random import random, sample


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
        survivor_selection_type: str = "fittest",
    ) -> None:
        self.objective_function = objective_function
        self.maximize = maximize
        self.pop_size = pop_size
        self.individual_size = individual_size
        self.max_generations = max_generations
        self.crossover_rate = crossover_rate
        self.mutation_rate = mutation_rate
        self.survivor_selection_type = survivor_selection_type
        self.generations = []

    def simulate(self):
        population = self.__init_population()
        print(population)
        while population.generation_nr < self.max_generations:
            population = self.__generation(population)
            print(population)
            self.generations.append(population)

    def get_solution(self, constraint=None) -> Individual:
        pop: Population = self.generations[-1]
        individuals = select_fittest_individuals(
            pop.individuals, len(pop.individuals), self.maximize
        )

        if constraint is None:
            return individuals[0]
        else:
            solution = None
            for s in individuals:
                if s.value <= constraint[0] <= constraint[1]:
                    solution = s
                    break
            return solution

    def get_generations(self) -> "list[Population]":
        return self.generations

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

        # Calculate fitness of offspring
        self.objective_function(offsprings)

        # return new generation
        new_generation = survivor_selection(
            self.survivor_selection_type, offsprings, population, self.maximize
        )
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

    for i in range(1, len(offspring), 2):  # Every other, e.g. pairs
        # Prepare parents
        p1, p2 = parents[i - 1], parents[i]

        # Chance of crossover
        if random() <= crossover_rate:
            # Select a point between (0,len(bitstring)-1) -> [1,len(bitstring)-2]
            x_point = np.random.randint(1, len(parents[0].bitstring) - 1)

            # Mutate copies
            # Keep the beginning of the bitstring, swap the last part from the other parent
            # Could have been done in place without the use of parents, but this improves readability.
            # E.g. o1[x:], o2[x:] = o2[x:].copy, o1[x:].copy()
            offspring[i - 1].bitstring[x_point:] = p2.bitstring[x_point:].copy()
            offspring[i].bitstring[x_point:] = p1.bitstring[x_point:].copy()

        # Set parents
        offspring[i - 1].parents = [p1, p2]
        offspring[i].parents = [p1, p2]
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
    survivor_selection_type: str,
    individuals: "list[Individual]",
    old_population: Population,
    maximize: bool,
) -> Population:
    """Select a survivor selection function.
    "Replacement", "Fittest", "restricted_tournament"

    Args:
        survivor_selection_type (str): _description_
        individuals (list[Individual]): _description_
        old_population (Population): _description_
        maximize (bool): _description_

    Raises:
        NotImplementedError: _description_

    Returns:
        Population: _description_
    """
    match survivor_selection_type.lower():
        case "replacement":
            return create_new_generation(individuals, old_population)
        case "fittest":
            return survivor_selection_fittest(individuals, old_population, maximize)
        case "restricted_tournament":
            return survivor_selection_restricted_tournament(
                individuals, old_population, maximize
            )
        case _:
            raise NotImplementedError("Use another survivor selection function")


def survivor_selection_fittest(
    individuals: "list[Individual]", old_population: Population, maximize: bool
) -> Population:
    """Select the fittest individuals in a set of individuals and the prior population

    Args:
        individuals (list[Individual]): Offsprings from parents
        old_population (Population): The old population

    Returns:
        Population: Fittest individuals as a new population
    """
    # add parents to individuals
    individuals.extend(old_population.individuals)

    # Create new generation
    selected = select_fittest_individuals(
        individuals, len(old_population.individuals), maximize
    )
    return create_new_generation(selected, old_population)


def survivor_selection_restricted_tournament(
    individuals: "list[Individual]",
    old_population: Population,
    maximize: bool,
    k: int = 5,
) -> Population:
    """A survivor selection teqchnique using restricted tournament selection

    Args:
        individuals (list[Individual]): offspring
        old_population (Population): old population
        maximize (bool): Select best or worst individual
        k (int, optional): Number of individuals to select for each tournament. Defaults to 5.

    Returns:
        Population: The new population
    """
    maximize = not maximize
    individuals.extend(old_population.individuals)
    selected = []
    for _ in range(len(old_population.individuals)):
        # Select individuals
        tournament = sample(individuals, k)
        tournament.sort(key=lambda i: i.fitness, reverse=True)

        # Calculate crowding distance
        distances = [
            abs(tournament[j + 1].fitness - tournament[j].fitness) for j in range(k - 1)
        ]
        distances.append(abs(tournament[0].fitness - tournament[k - 1].fitness))

        # select winner
        if not maximize:
            winner_idx = distances.index(min(distances))
        else:
            winner_idx = distances.index(max(distances))
        selected.append(tournament[winner_idx])
        individuals.remove(tournament[winner_idx])
    return create_new_generation(selected, old_population)


def create_new_generation(
    individuals: "list[Individual]", old_population: Population
) -> Population:
    """Create a new generation

    Args:
        individuals ("list[Individual]"): list of new individuals
        old_population (Population): Old population

    Returns:
        Population: New population
    """
    new_generation = Population()
    new_generation.prev_gen = old_population
    new_generation.generation_nr = old_population.generation_nr + 1
    new_generation.individuals = individuals
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
