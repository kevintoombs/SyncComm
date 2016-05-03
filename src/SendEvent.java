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
        if (!c.recvQueueIsEmpty()) {
            c.fulfillRecv(o).notify();
            return true;
        }
        return false;
    }

    void enqueue() {
        try {
            c.addToSendQueue(o).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
