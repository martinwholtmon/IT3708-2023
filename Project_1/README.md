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

Why feature selection?
- Good approximation, sometimes better model. 
- Reducing dimentions: Less overfitting, less data entries, faster training time. 

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
Implement two synthetic fitness functions that operate on the bitstrings, before implementing the natural fitness function for feature selection.
#### Sine Function
- Sine function in the interval `[0,128]`
- Goal: Maximize the sine function
- Convert bitstrings to a real value, then scale it to fit the interval. E.g. Bitstring of size 15: 
  - Max value of 2<sup>15</sup>
  - Gives scaling factor of 2<sup>-8</sup>
- Handle constraints by adding penalty to the fitness function:
  - f(x) -> f'(x) = f(x) + P(d(x,F))
  - P(d(x,F)) - distance metric of the infeasible point to the feasible region F
  - The penalty function P is zero for feasible solutions and increases proportionally with the distance to the feasible region

#### Feature Selection
Using the Machine Learning Algorithm provided in `LinReg.py`:
- Simple linear regression
- Return the RMSE. Use this value as a fitness function for the GA.
- Goal: Minimize RMSE
- Methods to use from `LinReg.py`:
  - `get_fitness(x, y)`: Gives the root-mean-square error on the data `x` with targets `y`. 
  - `get_columns(x, bitstring)`: Selects features from data `x` based on the bits in `bitstring`. Filter out columns by providing data and bitstring. 

## Dataset
- 1994 rows
- 102 columns. 
  - First 101 columns represent the data (`x`)
  - Last column represent the value (`y`)

## Syllabus:
- A. E. Eiben and J. E. Smith. “Introduction to Evolutionary Computing,” 2nd
Edition, Springer 2015
  - Page 99-100: Table 6.1 (SGA)
  - Page 91-95: Population diversity and crowding
  - Page 203-211: Constraint handling
- D. Simon. “Evolutionary Optimization Algorithms,” Wiley 2013
  - Page 44-55: SGA
  - Page 192-198: Population diversity, crowding
- D. E. Goldberg “Genetic Algorithms in Search, Optimization, and Machine Learning,” Addison-Wesley, 1989
  - Oage 59-75
- O. J. Mengshoel and D. E. Goldberg. 2008. “The Crowding Approach to Niching in Genetic Algorithms.” Evol. Comput. 16, 3 (Fall 2008)
  - Page 315-354