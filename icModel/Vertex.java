package icModel;

import java.util.ArrayList;

public class Vertex {
    private final Integer ID;
    private final ArrayList<Edge> outEdges = new ArrayList<>();
    private final ArrayList<Edge> toEdges = new ArrayList<>();
    private double activateProbability = 0;

    public double getActivateProbability() {
        return activateProbability;
    }

    public void setActivateProbability(double activateProbability) {
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

    public static Integer getCnt() {
        return cnt;
    }

    public static void setCnt(Integer cnt) {
        Vertex.cnt = cnt;
    }

    private void setActivate(boolean activate) {
        this.activate = activate;
    }

    public Integer getID() {
        return ID;
    }

    private static Integer cnt = 0;
    private boolean activate;

    public Vertex() {
        this(false);
    }

    public Vertex(boolean activate) {
        this.ID = cnt++;
        this.activate = activate;
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
        return "Vertex{" + "activate=" + activate + '}';
    }

}
