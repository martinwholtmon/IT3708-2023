# Project 1
Goals: 
- Implement a genetic algorithm to select the most impactful features in a dataset
- Implement crowding in the genetic algorithm to increase diversity within the population
- Show the results of the feature selection, and compare results when using crowding, no crowding and not doing any feature selection
- Use a simpler synthetic problem as a stepping stone towards the feature selection problem

**Deadline**: February 13th, 2023

## Assignment
Create a genetic algorithm for feature selection in a linear regression problem scored by RMSE (fitness function).
Objective: Minimize RMSE. 

### Genetic Algorithms
- Implement a Genetic Algorithm (GA) as demonstrated in the lectures. Focus on
  - A Simple Genetic Algorithm (SGA)
  - A crowding algorithm
- Represent your individuals as **bitstrings**.
- During feature selection, each bit decided whether a feature should be included or not.
- Important to optimize GA parameter values (population size, generation number, crossover rate, mutation rate, etc...)
  - To find near-optimal solutions 
  - Test different sets of parameter values
  - Study interdependence between the parameter values

#### The Simple Genetic Algorithm
Consists of generating an initial population, selecting the best parents of the offspring, doing crossover and mutation, and replacing the population. This process is repeated until an end condition is reached.

#### The Crowding Concept: Solve premature convergence
Premature convergence: A problem that may arise when using an evolutionary algorithm is having most or all individuals in the population converging to the same solution. There might be better solutions.

Crowding:
- Combats the convergence to similar solutions by increasing diversity within the population
- The survival step in the genetic algorithm is replaced with a local tournament, for example between a parent and its closest child.
  - De Jong’s scheme
  - Deterministic crowding
  - Probabilistic crowding
  - Restricted tournament selection

**In this project: Demonstrate on or two of these crowing methods**</br >
Tip: Use enthropy to measure the diversity within the population over generations. 

### Fitness Functions
Implement two different fitness functions that operate on the bitstrings.
#### Sine Function
#### Feature Selection