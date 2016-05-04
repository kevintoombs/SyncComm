public abstract class CommEvent {
    protected Channel c;
    protected Object o = null;

    Object sync() {
        c.getLock().lock();
        if (!poll()) enqueue();
        c.getLock().unlock();
        return o;
    }
    abstract boolean poll();

    abstract void enqueue(); //used for stage 2

    abstract void enqueue2(java.util.concurrent.locks.Condition condition); //used for stage 3
}
