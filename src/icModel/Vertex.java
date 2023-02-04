package icModel;

import java.util.ArrayList;

public record Vertex(int id, ArrayList<Edge> outEdges, ArrayList<Edge> inEdges) {
    public ArrayList<Edge> getOutEdges() {
        return outEdges;
    }

    public ArrayList<Edge> getInEdges() {
        return inEdges;
    }

    public void addInEdge(Edge e) {
        inEdges.add(e);
    }

    public void addOutEdge(Edge e) {
        outEdges.add(e);
    }

    public Vertex(Integer id) {
        this(id, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return id == vertex.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "V{" + "id: " + id + "}";
    }
}
