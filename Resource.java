public interface Resource {
    boolean free(Distinguisher distinguisher);

    boolean acquire(Distinguisher distinguisher);

    void release();
}
