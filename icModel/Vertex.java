package icModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Vertex {
    private final Integer ID;
    private final ArrayList<Edge> outEdges;
    private final ArrayList<Edge> inEdges;
    private Boolean active;

    public Vertex(Integer ID, ArrayList<Edge> outEdges, ArrayList<Edge> inEdges, boolean active) {
        this.ID = ID;
        this.outEdges = new ArrayList<>(outEdges);
        this.inEdges = new ArrayList<>(inEdges);
        this.active = active;
    }

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Integer> getAdjacentVertexId() {
        return outEdges.stream().map(Edge::getToId).collect(Collectors.toList());
    }

    public Vertex(Integer ID) {
        this(ID, new ArrayList<>(), new ArrayList<>(), false);
    }


    public Integer getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "V{" + "ID=" + ID + ", active=" + active + '}';
    }
}
