package icModel;

import java.util.Objects;

public class Edge {
    private final Vertex from;
    private final Vertex to;
    private final Double pp;

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }

    public Double getPp() {
        return pp;
    }


    public Edge(Vertex from, Vertex to, Double pp) {
        this.from = from;
        this.to = to;
        this.pp = pp;
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
        return Objects.hash(from, to, pp);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", pp=" + pp +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(new Edge(new Vertex(), new Vertex(), 0.1).toString());

    }
}
