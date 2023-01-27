package icModel;

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
