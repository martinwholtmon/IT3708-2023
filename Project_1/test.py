from src.fitnessfunction import ObjectiveSine, ObjectiveLinReg
from src.sga import SGA
from src.lin_reg import LinReg
import numpy as np


def ss_sine(survivor_selection_type=None):
    params = {
        "objective_function": ObjectiveSine().get_fitness,
        "maximize": True,
        "pop_size": 50,
        "individual_size": 15,
        "max_generations": 15,
        "crossover_rate": 0.6,
        "mutation_rate": 0.05,
    }
    if survivor_selection_type is not None:
        params.update({"survivor_selection_type": survivor_selection_type})

    sga = SGA(**params)
    sga.simulate()
    solution = sga.get_solution()
    print(solution.bitstring, solution.value, solution.fitness)


def ss_linreg(survivor_selection_type=None):
    # Load dataset
    data_path = r"Project_1/data/dataset.txt"
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
    if survivor_selection_type is not None:
        params.update({"survivor_selection_type": survivor_selection_type})

    sga = SGA(**params)
    sga.simulate()
    solution = sga.get_solution()
    print(solution.bitstring, solution.value, solution.fitness)


if __name__ == "__main__":
    # ss_sine()
    # ss_sine("restricted_tournament")
    ss_linreg()
    # ss_linreg("restricted_tournament")
