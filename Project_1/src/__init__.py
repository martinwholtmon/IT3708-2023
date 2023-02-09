from sga import SGA, calc_fitness, parent_selection, crossover, mutation


def main():
    """Entry point for the application script"""
    sga = SGA()
    a_pop = sga.init_population()
    calc_fitness(a_pop)
    parents = parent_selection(a_pop)
    print(parents[0].fitness)
    print(f"Population size: {len(a_pop.individuals)}")
    print(f"phenotype of first individual: {a_pop.individuals[0].fitness}")

    # test
    for i in crossover(parents, 0.6):
        mutation(i, 0.6)


if __name__ == "__main__":
    main()
