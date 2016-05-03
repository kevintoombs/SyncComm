public class RecvEvent extends CommEvent {
    Channel c;

    RecvEvent(Channel inC) {
        c = inC;
    }

    Object sync() {
        return this;
    }

    boolean poll() {
        return false;
    }

    void enqueue() {

    }
}
