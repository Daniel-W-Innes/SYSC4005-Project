package controller.resource;

import model.Distinguisher;

public interface Resource {
    boolean acquire(Distinguisher distinguisher);

    void release();
}
