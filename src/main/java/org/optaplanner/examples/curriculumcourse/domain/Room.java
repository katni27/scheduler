package org.optaplanner.examples.curriculumcourse.domain;
import java.util.Objects;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

public class Room extends AbstractPersistable implements Labeled {

    private static final String[] ROOMTYPE = {
            "Аудитория",
            "Админ. помещение",
            "Уч. лаборатория",
            "Науч. лаборатория",
            "Комп. класс",
            "АХЧ помещение"
    };

    private String code;
    private int capacity;
    private int type;

    public Room() {
    }

    public Room(int id, String code, int capacity, int type) {
        super(id);
        this.code = Objects.requireNonNull(code);
        this.capacity = capacity;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFullLabel() {
        return code + "\n" + ROOMTYPE[type] + "\nРазмер: " + capacity;
    }

    @Override
    public String getLabel() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

}
