package icModel;

import java.util.Objects;

public record Edge(Vertex from, Vertex to, Double pp) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from.equals(edge.from) && to.equals(edge.to);
    }

    @Override
    public int hashCode() {
//        return ("" + from + to + "").hashCode();
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "(" + "" + from.getId() + "->" + to.getId() + ')';
    }
}
