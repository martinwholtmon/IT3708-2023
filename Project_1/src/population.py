from src.individual import Individual


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
