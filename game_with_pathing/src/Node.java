import java.util.Objects;

public class Node
{
    private final Point point;
    private Node parent;
    private final int h;
    private int g;

    public Node(Point pt, Node parent, int H, int G) {
        this.point = pt;
        this.parent = parent;
        this.h = H;
        this.g = G;
    }

    public Point getPoint() {
        return point;
    }

    public Node getParent() {
        return parent;
    }

    public int getG() {
        return g;
    }

    public int getF() {
        return h + g;
    }

    public void setParent(Node node) {
        parent = node;
    }

    public void setG(int step) {
        g = step;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Node))
            return false;

        return point.equals(((Node) o).getPoint());
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, parent, h, g);
    }
}
