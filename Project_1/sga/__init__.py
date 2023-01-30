from sga import SGA, calc_pop_fitness, parent_selection


def main():
    """Entry point for the application script"""
    sga = SGA()
    a_pop = sga.init_population()
    parents = parent_selection(a_pop)
    print(parents)
    print(f"Population size: {len(a_pop.individuals)}")
    print(f"phenotype of first individual: {a_pop.individuals[0].phenotype}")


if __name__ == "__main__":
    main()
