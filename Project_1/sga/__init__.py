from sga import SGA


def main():
    """Entry point for the application script"""
    sga = SGA()
    a_pop = sga.init_population()
    print(f"Population size: {len(a_pop.individuals)}")
    print(f"phenotype of first individual: {a_pop.individuals[0].phenotype}")


if __name__ == "__main__":
    main()
