public interface Resource {
    boolean acquire(Distinguisher distinguisher);

    void release();
}
