"""Edge class.
Author: Nickzad Bayati
"""


class Edge:
    """Represents the edge, e=(u,v), of a graph."""

    def __init__(self, p1, p2, weight):
        """Initializes edge given the 2 points it connects.
        Args:
            p1 (tuple): the first x-y coordinate
            p2 (tuple): the second x-y coordinate
            weight (float): the distance between the two points
        Returns:
            Edge
        """
        self.u = p1
        self.v = p2
        self.w = weight

    def __repr__(self):
        """Defines string representation of Edge objects"""
        return "[{}, {}]".format(self.u, self.v)

    def __eq__(self, other):
        """Defines equality between two Edge objects"""
        return (self.u == other.get_u() and self.v == other.get_v()) \
            or (self.u == other.get_v() and self.v == other.get_u())

    def __lt__(self, other):
        """Defines less than between two Edge objects"""
        return self.w < other.get_weight()

    def __hash__(self):
        return hash((self.u, self.v))

    def get_u(self):
        """Returns u"""
        return self.u

    def get_v(self):
        """Returns v"""
        return self.v

    def get_weight(self):
        """Returns the edge weight"""
        return self.w
