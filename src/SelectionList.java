class SelectionList {

    private final java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
    private java.util.LinkedList<CommEvent> list = new java.util.LinkedList<CommEvent>();

    void addEvent(CommEvent ce) {
        lock.lock();
        list.add(ce);
        lock.unlock();
    }

    Object select() {
        lock.lock();
        java.util.concurrent.locks.Condition condition = lock.newCondition();
        if (list.isEmpty()) return null;

        for (CommEvent ce : list) {
            ce.c.getLock().lock();
            if (ce.poll()) {
                ce.c.getLock().unlock();
                lock.unlock();
                return ce.o;
            }
            ce.c.getLock().unlock();
        }

        for (CommEvent ce : list) {
            ce.c.getLock().lock();
            ce.enqueue2(condition);
            ce.c.getLock().unlock();
        }

        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Object found = null;
        Boolean b = true;
        for (CommEvent ce : list) {
            ce.c.getLock().lock();
            if (!ce.c.removeFromQueues(ce.o) && b) {
                found = ce.o;
            }
            ce.c.getLock().unlock();
        }

        lock.unlock();
        return found;

    }
}

/*
To implement select, call poll for each element of the
SelectionList. If one of the calls to poll finds a matching event it causes the communication
to occur we’re done. If none of the polls succeeds, select() calls enqueue for
each element of the SelectionList, thus adding each event to the approriate queue of
the appropriate channel. After calling enqueue for all the elements, select waits. When
awoken, it figures out which event was matched (there must be only one!), removes all
the other events of the SelectionList from their channel queues, and returns the Object
associated with the successful event – either the sent Object in the case of a SendEvt
or the received Object in the case of a RecvEvt. The trick to making this work is that
all of the objects that are enqueued by a given call to select should reference the same
Condition object. Once all are enqueued, select waits on that condition object. A subsequent
poll wakes up the waiting select by notifying on the common condition object.
Select must then go through the elements of the SelectionList and remove them from
the queues of the channels. Select should not remove anything from the SelectionList
itself.
*/


/*

The final step is to add synchronous selective communication. (Note that select here
has absolutely nothing to do with the Unix select system call.) To do this we add
the SelectionList class which has methods for constructing a list of communication
events and for performing a select() operation on the list. Events in a SelectionList can
consist of a mixture of SendEvts and RecvEvts. In addition different events may refer
to different channels. Each SelectionList is used by only a single thread and you may
assume that a SendEvt and a RecvEvt for the same channel do not ever get put on the
same SelectionList. (You do not have to implement code to check these two things –
just write your demo code to obey those rules.)
select() performs exactly one of the events in the SelectionList by matching it with a
complementary event on the same channel. The complementary event may be being
sync’d directly or it may be itself part of a SelectionList on which select is being performed
(by a different thread, of course). (Don’t confuse the behavior select here with
the behavior of the select Unix system call. The Unix select call returns data indicating
which file descriptor(s) are ready to perform I/O. Our select will actually perform one
interaction with another thread.)
Now we come to the part where we really take advantage of the poll and enqueue
functions that we built before. To implement select, call poll for each element of the
SelectionList. If one of the calls to poll finds a matching event it causes the communication
to occur we’re done. If none of the polls succeeds, select() calls enqueue for
each element of the SelectionList, thus adding each event to the approriate queue of
the appropriate channel. After calling enqueue for all the elements, select waits. When
awoken, it figures out which event was matched (there must be only one!), removes all
the other events of the SelectionList from their channel queues, and returns the Object
associated with the successful event – either the sent Object in the case of a SendEvt
or the received Object in the case of a RecvEvt. The trick to making this work is that
5
all of the objects that are enqueued by a given call to select should reference the same
Condition object. Once all are enqueued, select waits on that condition object. A subsequent
poll wakes up the waiting select by notifying on the common condition object.
Select must then go through the elements of the SelectionList and remove them from
the queues of the channels. Select should not remove anything from the SelectionList
itself.
Synchronization:
Select is manipulating multiple channels: there is a serious risk of deadlock due to
acquiring locks in different orders if channel locks are used. At this stage, I suggest
using a single, global, lock (ReentrantLock).

*/