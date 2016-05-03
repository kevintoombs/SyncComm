public abstract class CommEvent {
    abstract Object sync();
    abstract boolean poll();
    abstract void enqueue();
}
