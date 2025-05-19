package org.optaplanner.examples.common.domain;
import org.optaplanner.core.api.domain.lookup.PlanningId;

public abstract class AbstractPersistable {

    protected int id;

    protected AbstractPersistable() {
    }

    protected AbstractPersistable(int id) {
        this.id = id;
    }

    @PlanningId
    public long getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getName().replaceAll(".*\\.", "") + "-" + id;
    }

}
