public class SendEvent extends CommEvent {
    private Channel c;
    private Object o;

    SendEvent(Object inO, Channel inC) {
        o = inO;
        c = inC;
    }

    Object sync() {
        if (!poll()) enqueue();
        return o;
    }

    boolean poll() {
        return false;
    }

    void enqueue() {

    }
}
