class Individual:
    """This class represent an individual in a population"""

    def __init__(
        self,
        bitstring: list[int],
        fitness: float = 0,
        parents: "list[Individual]" = None,
    ) -> None:
        self.bitstring = bitstring
        self.phenotype = phenotype
        self.fitness = fitness
        self.parents = parents or []


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
        init_pop_size: int = 1000,
        individual_size: int = 15,
        max_generations: int = 0,
        crossover_rate: float = 0,
        mutation_rate: float = 0,
    ) -> None:
        self.init_size = init_pop_size
        self.individual_size = individual_size
        self.max_generations = max_generations
        self.crossover_rate = crossover_rate
        self.mutation_rate = mutation_rate
        self.generations = {}
