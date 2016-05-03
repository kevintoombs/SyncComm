public class SendEvent extends CommEvent {
    private Channel c;
    private Object o;
    private final java.util.concurrent.locks.Lock lock;

    SendEvent(Object inO, Channel inC) {
        o = inO;
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
        if (!c.recvQueueIsEmpty()) {
            if (true) System.out.println("recv queue not empty.");
            c.fulfillRecv(o);
            return true;
        }
        if (true) System.out.println("recv queue empty.");
        return false;
    }

    void enqueue() {
        if (true) System.out.println("adding to send.");
        c.addToSendQueueThenWait(o);
    }
}
