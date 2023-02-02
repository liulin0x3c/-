package icModel;

import java.util.Objects;

public class Edge {
    private final Integer fromId;
    private final Integer toId;
    private final Double pp;
    private Boolean blocked;


    public Double getPp() {
        return pp;
    }

    public Edge(Integer fromId, Integer toId, Double pp, Boolean blocked) {
        this.fromId = fromId;
        this.toId = toId;
        this.pp = pp;
        this.blocked = blocked;
    }

    public Edge(Integer fromId, Integer toId, Double pp) {
        this(fromId, toId, pp, false);
    }

    public Edge(Edge e) {
        this(e.fromId, e.toId, e.pp, e.blocked);
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public Integer getFromId() {
        return fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }


    @Override
    public String toString() {
        return "(" + "" + fromId + "->" + toId + ')';
    }
}
