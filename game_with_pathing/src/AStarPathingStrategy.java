import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy
{
    public List<Point> computePath(Point start, Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors)
    {
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator
                .comparingInt(Node::getF)
                .thenComparingInt(Node::getG));
        Set<Point> closed = new HashSet<>();

        open.add(new Node(start, null, calcH(start, end), 0));

        while (!open.isEmpty())
        {
            Node current = open.poll();
            closed.add(current.getPoint());

            if (current.getPoint().adjacent(end))
                return getPath(current);

            Set<Node> neighbors = getNeighbors(current, end, canPassThrough, withinReach, potentialNeighbors);
            for (Node neighbor : neighbors) {
                if (closed.contains(neighbor.getPoint()))
                    continue;

                if (open.contains(neighbor)) {
                    List<Node> openList = new ArrayList<>(open);
                    openList.get(openList.indexOf(neighbor)).setG(neighbor.getG());
                    openList.get(openList.indexOf(neighbor)).setParent(current);
                } else
                    open.add(neighbor);
            }
        }

        return new ArrayList<>(); // return empty list if no path found
    }

    private int calcH(Point pt, Point end) {
        return Math.abs(pt.x - end.x) + Math.abs(pt.y - end.y);
    }

    private HashSet<Node> getNeighbors(Node node, Point end, Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors)
    {
        Point pt = node.getPoint();
        HashSet<Node> neighbors = potentialNeighbors.apply(pt)
                .filter(canPassThrough)
                .filter(p -> withinReach.test(p, pt))
                .map(p -> new Node(p, node, calcH(p, end),node.getG() + 10))
                .collect(Collectors.toCollection(HashSet::new));
        // if there are diagonals:
        neighbors.addAll(potentialNeighbors.apply(pt)
                .filter(canPassThrough)
                .filter(p -> !withinReach.test(p, pt))
                .map(p -> new Node(p, node, calcH(p, end),node.getG() + 5))
                .collect(Collectors.toCollection(HashSet::new)));
        return neighbors;
    }

    private List<Point> getPath(Node node) {
        List<Point> path = new ArrayList<>();
        while (node.getParent() != null) {
            path.add(node.getPoint());
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}
