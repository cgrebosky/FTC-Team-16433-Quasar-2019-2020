package quasar.prod;

public enum Side {
    RED, BLUE;
    public Side swap() {
        if(this == RED) return BLUE;
        return RED;
    }
}