from sga import SGA
from fitnessfunction import sine, lin_reg


def main():
    """Entry point for the application script"""
    sga = SGA(objective_function=sine)
    population = sga.simulate()
    print(population.fitness)


if __name__ == "__main__":
    main()
