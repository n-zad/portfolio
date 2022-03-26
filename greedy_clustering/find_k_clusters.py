"""Find k-clusters.
Author: Nickzad Bayati
"""
import sys
from edge import Edge


def main(argv):
    """Finds all k-clusters for n points.
    1. Get points from input file.
    2. Compute edge weights (distance between points).
    3. Sort edges by ascending weight.
    4. Greedily make clusters for each k-clustering and print them.
    """
    # check program args
    if len(argv) != 1:
        print("Error: incorrect # of program arguments.")
        return

    # gather points from input file
    points = []
    file = open(argv[0])
    line = file.readline()
    numbers = line.strip('\n').replace('(', '').replace(')', '')
    numbers = list(map(int, numbers.split(', ')))
    file.close()

    # convert numbers into points
    for i in range(0, len(numbers), 2):
        point = (numbers[i], numbers[i+1])
        points.append(point)

    # Graph definition:
    #  - points are vertices
    #  - distances between points are weighted edges

    # create weighted edge list
    edges = set()  # use set for better performance
    for u in points:
        for v in points:
            edge = Edge(u, v, point_distance(u, v))
            # check if edge is not a duplicate and u and v are not the same
            if edge not in edges and edge.get_weight() != 0:
                edges.add(edge)
    edges = list(edges)  # convert to list for use in algorithm

    # ----------------------- #
    # Algorithm starts here:
    # ----------------------- #
    # sort edge list
    edges = sorted(edges)

    # create cluster list
    clusters = sorted(list(points))
    clusters = [[point] for point in clusters]

    # initialize k = n
    k = len(clusters)

    # print k = n clustering
    print("{}-clustering:".format(str(k)))
    for cluster in clusters:
        print(", ".join(map(str, cluster)))

    # iterate sorted edge list
    for edge in edges:
        # greedily merge the points w/ shortest edge to cluster
        u = edge.get_u()
        v = edge.get_v()
        # find clusters containing u and v
        u_cluster = find_cluster(u, clusters)
        v_cluster = find_cluster(v, clusters)
        # ignore edges from already clustered points
        if u_cluster == v_cluster:
            continue
        # replace clusters with combined cluster
        clusters.remove(u_cluster)
        clusters.remove(v_cluster)
        clusters.append(sorted(u_cluster + v_cluster))
        clusters = sorted(clusters)
        # print k-1 cluster
        k -= 1
        print("{}-clustering:".format(str(k)))
        for cluster in clusters:
            print(", ".join(map(str, cluster)))
        # end algorithm when k = 1
        if k == 1:
            break


def point_distance(u, v):
    """Calculates the distance between two points.
    Args:
        u (tuple): an x-y coordinate
        v (tuple): another x-y coordinate
    Returns:
        float: the distance between u and v
    """
    return ((v[0] - u[0]) ** 2 + (v[1] - u[1]) ** 2) ** 0.5


def find_cluster(u, clusters):
    """Find the cluster containing the given point.
    Args:
        u (tuple): an x-y coordinate
        clusters (list): the cluster list
    Returns:
        list: the cluster containing u
    """
    # create sets for comparisons
    u_set = {u}
    cluster_set = [set(cluster) for cluster in clusters]

    # iterate cluster list to find u
    for cluster in cluster_set:
        if bool(cluster.intersection(u_set)):
            return sorted(list(cluster))


if __name__ == '__main__':
    main(sys.argv[1:])
