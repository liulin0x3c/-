package common;


public final class Entry<T, U> {
    public T edge;
    public U exp;

    public Entry(T edge, U exp) {
        this.edge = edge;
        this.exp = exp;
    }
}
