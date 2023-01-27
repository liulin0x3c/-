package icModel;

import java.util.ArrayList;

public class Vertex {
    private final Integer ID;
    private final ArrayList<Vertex> adjacentVertices = new ArrayList<>();

    public ArrayList<Vertex> getAdjacentVertices() {
        return adjacentVertices;
    }

    public void addAdjacentVertex(Vertex v) {
        this.adjacentVertices.add(v);
    }

    public static Integer getCnt() {
        return cnt;
    }

    public static void setCnt(Integer cnt) {
        Vertex.cnt = cnt;
    }

    public void setActivate(boolean activate) {
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

    @Override
    public String toString() {
        return "Vertex{" + "activate=" + activate + '}';
    }

    public static void main(String[] args) {
        new Vertex();
        new Vertex();
        System.out.println(new Vertex().getID().toString());
    }
}
