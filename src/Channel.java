public class Channel {
    private boolean DEBUG = false;
    private final java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
    public volatile java.util.LinkedList<Comm> sendQueue, recvQueue;

    Channel() {
        this.sendQueue = new java.util.LinkedList<>();
        this.recvQueue = new java.util.LinkedList<>();
        if (DEBUG) System.out.println("Channel made.");
    }

    ///STAGE 1
    Object Send(Object o)
    {
        lock.lock();
        if (DEBUG) System.out.println("Send lock.");
        //if the receive queue is empty
        if(recvQueue.isEmpty()) {
            if (DEBUG) System.out.println("Send found empty queue.");
            //make an entry in the send queue and wait to be awoken
            java.util.concurrent.locks.Condition c = lock.newCondition();
            Comm comm = new Comm(o, c);
            sendQueue.add(comm);
            while (sendQueue.contains(comm)) {
                try {
                    if (DEBUG) System.out.println("Send waiting. " + c.toString());
                    c.await();
                } catch(InterruptedException e) {
                    //pass
                }
            }
            if (DEBUG) System.out.println("Send done waiting.");
        } else {
            //transfer object to first recv from the queue
            recvQueue.getFirst().o = o;
            if (DEBUG) System.out.println("Send notifying. " + recvQueue.getFirst().c.toString());
            recvQueue.getFirst().c.signal();
            recvQueue.removeFirst();
        }
        if (DEBUG) System.out.println("Send unlock.");
        lock.unlock();
        return o;
    }

    Object Recv()
    {
        lock.lock();
        if (DEBUG) System.out.println("Recv lock.");
        Object o;
        //if the send queue is empty
        if(sendQueue.isEmpty()) {
            if (DEBUG) System.out.println("Recv found empty queue.");
            //make an entry in the recv queue and wait to be awoken
            java.util.concurrent.locks.Condition c = lock.newCondition();
            Comm comm = new Comm(null, c);
            recvQueue.add(comm);
            while (recvQueue.contains(comm) && comm.o == null) {
                try {
                    if (DEBUG) System.out.println("Recv waiting. " + c.toString());
                    c.await();
                } catch(InterruptedException e) {
                    //pass
                }
            }
            if (DEBUG) System.out.println("Recv done waiting. " + c.toString());

            o = comm.o;
        } else {
            Comm out = sendQueue.removeFirst();
            o = out.o;
            if (DEBUG) System.out.println("Recv notifying. " + out.c.toString());
            out.c.signal();
        }
        if (DEBUG) System.out.println("Recv unlock.");
        lock.unlock();
        return o;
    }
    ///END STAGE 1

    //STAGE 2
    boolean recvQueueIsEmpty() {
        lock.lock();
        boolean b = recvQueue.isEmpty();
        lock.unlock();
        return b;
    }

    boolean sendQueueIsEmpty() {
        lock.lock();
        boolean b = sendQueue.isEmpty();
        lock.unlock();
        return b;
    }

    Object addToSendQueueThenWait(Object o) {
        if (DEBUG) System.out.println("add to send.");
        lock.lock();
        java.util.concurrent.locks.Condition c = lock.newCondition();
        Comm comm = new Comm(o, c);
        sendQueue.add(comm);
        try {
            if (DEBUG) System.out.println("send waiting.");
            c.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
        return o;
    }

    Object addToRecvQueueThenWait(Object o) {
        if (DEBUG) System.out.println("add to recv.");
        lock.lock();
        java.util.concurrent.locks.Condition c = lock.newCondition();
        Comm comm = new Comm(o, c);
        recvQueue.add(comm);
        try {
            if (DEBUG) System.out.println("recv waiting.");
            c.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
        return o;
    }

    Object fulfillSend(Object o) {
        if (DEBUG) System.out.println("fulfill send.");
        lock.lock();
        Comm out = sendQueue.removeFirst();
        o = out.o;
        out.c.signal();
        lock.unlock();
        return o;
    }

    Object fulfillRecv(Object o) {
        if (DEBUG) System.out.println("fulfill recv.");
        lock.lock();
        Comm out = recvQueue.removeFirst();
        out.o = o;
        out.c.signal();
        lock.unlock();
        return out.o;
    }

    java.util.concurrent.locks.Lock getLock() {
        return lock;
    }

    //STAGE 3
    Object addToSendQueue(Object o, java.util.concurrent.locks.Condition c) {
        lock.lock();
        Comm comm = new Comm(o, c);
        sendQueue.add(comm);
        lock.unlock();
        return o;
    }

    Object addToRecvQueue(Object o, java.util.concurrent.locks.Condition c) {
        lock.lock();
        Comm comm = new Comm(o, c);
        recvQueue.add(comm);
        lock.unlock();
        return o;
    }

    boolean removeFromQueues(Object o) {
        lock.lock();
        boolean b = false;
        if (recvQueue.remove(o)) b = true;
        if (sendQueue.remove(o)) b = true;
        lock.unlock();
        return b;
    }
}
