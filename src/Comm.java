public class Comm {
    public Object o;
    public java.util.concurrent.locks.Condition c;
    public Comm(Object in_o, java.util.concurrent.locks.Condition in_c) {
        this.o = in_o;
        this.c = in_c;
    }
}
