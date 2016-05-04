public class SendEvent extends CommEvent {

    SendEvent(Object inO, Channel inC) {
        o = inO;
        c = inC;
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

    void enqueue2(java.util.concurrent.locks.Condition condition) {
        if (true) System.out.println("adding to send.");
        c.addToSendQueue(o, condition);
    }
}
