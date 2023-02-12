from src.fitnessfunction import ObjectiveSine, ObjectiveLinReg
from src.sga import SGA
import numpy as np


def ss_sine():
    params = {
        "objective_function": ObjectiveSine().get_fitness,
        "pop_size": 100,
        "individual_size": 15,
        "max_generations": 15,
        "crossover_rate": 0.6,
        "mutation_rate": 0.05,
    }

    sga = SGA(**params)
    solution = sga.simulate()
    print(solution.bitstring, solution.value, solution.fitness)


def ss_linreg():
    # Load dataset
    data_path = r"./data/dataset.txt"
    df = np.genfromtxt(data_path, delimiter=",")
    y = df[:, -1]
    X = np.delete(df, 0, -1)

    # Prepare params
    params = {
        "objective_function": ObjectiveLinReg(X, y).get_fitness,
        "pop_size": 15,
        "individual_size": X.shape[1],
        "max_generations": 0,
        "crossover_rate": 0.6,
        "mutation_rate": 0.05,
    }

    sga = SGA(**params)
    solution = sga.simulate()
    print(solution.bitstring, solution.value, solution.fitness)


if __name__ == "__main__":
    # ss_sine()
    ss_linreg()
