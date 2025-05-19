package org.optaplanner.examples.curriculumcourse.domain;
import static java.util.Objects.requireNonNull;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;

public class Period extends AbstractPersistable implements Labeled {

    @JsonBackReference
    private Day day;

    @JsonIdentityReference(alwaysAsId = true)
    private Timeslot timeslot;

    @JsonIgnore
    private boolean originalTimeslotAsId;

    public Period() {
    }

    public Period(int id, Day day, Timeslot timeslot) {
        super(id);
        this.day = requireNonNull(day);
        day.getPeriodList().add(this);
        this.timeslot = requireNonNull(timeslot);
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public boolean isOriginalTimeslotAsId() { return originalTimeslotAsId; }

    public void setOriginalTimeslotAsId(boolean originalTimeslotAsId) {
        this.originalTimeslotAsId = originalTimeslotAsId;
    }

    @Override
    @JsonIgnore
    public String getLabel() {
        return day.getLabel() + " " + timeslot.getLabel();
    }

    @Override
    public String toString() {
        return day.getLabel() + " " + timeslot.getLabel();
    }

}
