package icModel;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Vertex {
    private final Integer ID;
    private final ArrayList<Edge> outEdges = new ArrayList<>();
    private final ArrayList<Edge> toEdges = new ArrayList<>();
    private BigDecimal activateProbability = new BigDecimal(0);

    public BigDecimal getActivateProbability() {
        return activateProbability;
    }

    public void setActivateProbability(BigDecimal activateProbability) {
        this.activateProbability = activateProbability;
    }

    public ArrayList<Edge> getOutEdges() {
        return outEdges;
    }

    public void addOutEdge(Edge edge) {
        this.outEdges.add(edge);
    }

    public ArrayList<Edge> getToEdges() {
        return toEdges;
    }

    public void addToEdge(Edge edge) {
        this.toEdges.add(edge);
    }


    private void setActivate(boolean activate) {
        this.activate = activate;
    }

    public Integer getID() {
        return ID;
    }

    private boolean activate;

    public Vertex(Integer ID) {
        this.ID = ID;
    }


    public boolean isActivate() {
        return activate;
    }

    public void activate() {
        this.activate = true;
    }

    public void deactivate() {
        this.activate = false;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "ID=" + ID +
                ", outEdges=" + outEdges +
                ", toEdges=" + toEdges +
                ", activateProbability=" + activateProbability +
                ", activate=" + activate +
                '}';
    }
}
