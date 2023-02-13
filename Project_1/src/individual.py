import numpy as np


class Individual:
    """This class represent an individual in a population"""

    def __init__(
        self,
        bitstring: np.ndarray[int],
        parents: "list[Individual]" = None,
    ) -> None:
        self.bitstring = bitstring
        self.parents = parents or []
        self.value: float = 0
        self.fitness: float = 0
