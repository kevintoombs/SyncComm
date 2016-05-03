public class RecvEvent extends CommEvent {
    private Channel c;
    private Object o = null;

    RecvEvent(Channel inC) {
        c = inC;
    }

    Object sync() {
        if (!poll()) enqueue();
        return o;
    }

    boolean poll() {
        if (!c.sendQueueIsEmpty()) {
            c.fulfillSend(o).notify();
            return true;
        }
        return false;
    }

    void enqueue() {
        try {
            c.addToRecvQueue(o).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
