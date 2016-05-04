public class RecvEvent extends CommEvent {

    RecvEvent(Channel inC) {
        c = inC;
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

    void enqueue2(java.util.concurrent.locks.Condition condition) {
        if (true) System.out.println("adding to recv.");
        c.addToRecvQueue(o, condition);
    }
}
