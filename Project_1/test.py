from src.fitnessfunction import sine
from src.sga import SGA


def main():
    params = {
        "objective_function": sine,
        "pop_size": 1000,
        "individual_size": 15,
        "max_generations": 5,
        "crossover_rate": 0,
        "mutation_rate": 0,
    }

    sga = SGA(**params)
    population = sga.simulate()
    print(population.generation_nr, population.fitness)


if __name__ == "__main__":
    main()
