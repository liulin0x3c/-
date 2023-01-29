package icModel;

import java.util.Objects;

public class Edge {
    private final Vertex from;
    private final Vertex to;
    private final Double pp;
    private Boolean blocked;

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }

    public Double getPp() {
        return pp;
    }


    public Edge(Vertex from, Vertex to, Double pp, Boolean blocked) {
        this.from = from;
        this.to = to;
        this.pp = pp;
        this.blocked = blocked;
    }

    public Edge(Vertex from, Vertex to, Double pp) {
        this(from, to, pp, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(from, edge.from) && Objects.equals(to, edge.to) && Objects.equals(pp, edge.pp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from.getID(), to.getID());
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void block() {
        this.blocked = true;
    }

    public void restore() {
        this.blocked = true;
    }

    @Override
    public String toString() {
        return "Edge{" + "from=" + from.getID() + ", to=" + to.getID() + ", blocked=" + blocked + '}';
    }

    public static void main(String[] args) {

    }
}
