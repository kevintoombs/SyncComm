public class Channel {
    private boolean DEBUG = true;
    private final java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
    private java.util.LinkedList<Comm> sendQueue, recvQueue;

    Channel() {
        this.sendQueue = new java.util.LinkedList<>();
        this.recvQueue = new java.util.LinkedList<>();
        if (DEBUG) System.out.println("Channel made.");
    }

    Object Send(Object o)
    {
        lock.lock();
        //if the receive queue is empty
        if(recvQueue.isEmpty()) {
            //make an entry in the send queue and wait to be awoken
            java.util.concurrent.locks.Condition c = lock.newCondition();
            Comm comm = new Comm(o, c);
            sendQueue.add(comm);
            while (sendQueue.contains(comm)) {
                try {
                    c.await();
                } catch(InterruptedException e) {
                    //pass
                }
            }
        } else {
            //transfer object to first recv from the queue
            recvQueue.getFirst().o = o;
            recvQueue.getFirst().c.signal();
        }
        lock.unlock();
        return o;
    }

    Object Recv()
    {
        lock.lock();
        Object o = null;
        //if the send queue is empty
        if(sendQueue.isEmpty()) {
            //make an entry in the recv queue and wait to be awoken
            java.util.concurrent.locks.Condition c = lock.newCondition();
            Comm comm = new Comm(null, c);
            recvQueue.add(comm);
            while (sendQueue.contains(comm) && comm.o == null) {
                try {
                    c.await();
                } catch(InterruptedException e) {
                    //pass
                }
            }
            Comm out = recvQueue.removeFirst();
            o = out.o;
            out.c.signal();;

        } else {
            Comm out = recvQueue.removeFirst();
            o = out.o;
            out.c.signal();;
        }
        lock.unlock();
        return o;
    }
}
