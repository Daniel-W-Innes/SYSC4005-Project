public interface Resource {
    boolean acquire();
    void release();
}
