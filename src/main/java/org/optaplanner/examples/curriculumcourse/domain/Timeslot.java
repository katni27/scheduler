package org.optaplanner.examples.curriculumcourse.domain;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

public class Timeslot extends AbstractPersistable implements Labeled {

    private static final String[] TIMES = { "08:00", "09:45", "11:30", "13:35", "15:20", "17:05", "18:45", "20:25"};

    private int timeslotIndex;

    public Timeslot() {
    }

    public Timeslot(int timeslotIndex) {
        super(timeslotIndex);
        this.timeslotIndex = timeslotIndex;
    }

    public int getTimeslotIndex() {
        return timeslotIndex;
    }

    public void setTimeslotIndex(int timeslotIndex) {
        this.timeslotIndex = timeslotIndex;
    }

    @Override
    public String getLabel() {
        String time = TIMES[(timeslotIndex % TIMES.length)];
        if (timeslotIndex > TIMES.length) {
            return "Timeslot " + timeslotIndex;
        }
        return time;
    }

    @Override
    public String toString() {
        return Integer.toString(timeslotIndex);
    }

}
