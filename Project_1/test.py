from src.fitnessfunction import ObjectiveSine, ObjectiveLinReg
from src.sga import SGA
from src.lin_reg import LinReg
import numpy as np


def ss_sine():
    params = {
        "objective_function": ObjectiveSine().get_fitness,
        "maximize": True,
        "pop_size": 100,
        "individual_size": 15,
        "max_generations": 10,
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
    X = np.delete(df, -1, axis=1)
    seed = 99

    # Run on entire dataset
    linreg_err = LinReg().get_fitness(X, y, seed)
    print(f"Error on entire dataset: {linreg_err}")

    # Prepare params
    params = {
        "objective_function": ObjectiveLinReg(X, y, seed=seed).get_fitness,
        "maximize": False,
        "pop_size": 250,
        "individual_size": X.shape[1],
        "max_generations": 30,
        "crossover_rate": 0.6,
        "mutation_rate": 0.05,
    }

    sga = SGA(**params)
    solution = sga.simulate()
    print(solution.bitstring, solution.value, solution.fitness)


if __name__ == "__main__":
    ss_sine()
    # ss_linreg()
