from src.fitnessfunction import sine
from src.sga import SGA


def main():
    params = {
        "objective_function": sine,
        "pop_size": 100,
        "individual_size": 15,
        "max_generations": 15,
        "crossover_rate": 0.6,
        "mutation_rate": 0.05,
    }

    sga = SGA(**params)
    solution = sga.simulate()
    print(solution.bitstring, solution.value, solution.fitness)


if __name__ == "__main__":
    main()
