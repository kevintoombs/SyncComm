public class RecvEvent extends CommEvent {
    private Channel c;
    private Object o = null;
    private final java.util.concurrent.locks.Lock lock;

    RecvEvent(Channel inC) {
        c = inC;
        lock = c.getLock();
    }

    Object sync() {
        lock.lock();
        if (!poll()) enqueue();
        lock.unlock();
        return o;
    }

    boolean poll() {
        if (!c.sendQueueIsEmpty()) {
            if (true) System.out.println("send queue not empty.");
            c.fulfillSend(o);
            return true;
        }
        if (true) System.out.println("send queue empty.");
        return false;
    }

    void enqueue() {
        if (true) System.out.println("adding to recv.");
        c.addToRecvQueueThenWait(o);
    }
}
