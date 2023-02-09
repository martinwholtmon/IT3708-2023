from sga import SGA, parent_selection, crossover, mutation
from fitnessfunction import sine, lin_reg


def main():
    """Entry point for the application script"""
    sga = SGA(objective_function=sine)
    a_pop = sga.init_population()
    parents = parent_selection(a_pop)
    print(parents[0].fitness)
    print(f"Population size: {len(a_pop.individuals)}")
    print(f"phenotype of first individual: {a_pop.individuals[0].fitness}")

    # test
    for i in crossover(parents, 0.6):
        mutation(i, 0.6)


if __name__ == "__main__":
    main()
