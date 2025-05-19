package org.optaplanner.examples.curriculumcourse.domain;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Day extends AbstractPersistable implements Labeled {

    private static final String[] WEEKDAYS = {
            "Пн (1-я)", "Вт (1-я)", "Ср (1-я)",
            "Чт (1-я)", "Пт (1-я)", "Сб (1-я)",
            "Пн (2-я)", "Вт (2-я)", "Ср (2-я)",
            "Чт (2-я)", "Пт (2-я)", "Сб (2-я)"
    };

    private int dayIndex;

    @JsonManagedReference
    private List<Period> periodList;

    public Day() {
    }

    public Day(int dayIndex, Period... periods) {
        super(dayIndex);
        this.dayIndex = dayIndex;
        this.periodList = Arrays.stream(periods)
                .collect(Collectors.toList());
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<Period> periodList) {
        this.periodList = periodList;
    }

    @Override
    public String getLabel() {
        String weekday = WEEKDAYS[dayIndex % WEEKDAYS.length];
        if (dayIndex > WEEKDAYS.length) {
            return "Day " + dayIndex;
        }
        return weekday;
    }

    @Override
    public String toString() {
        return Integer.toString(dayIndex);
    }

}
