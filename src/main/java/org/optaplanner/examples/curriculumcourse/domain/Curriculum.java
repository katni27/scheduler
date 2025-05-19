package org.optaplanner.examples.curriculumcourse.domain;
import static java.util.Objects.requireNonNull;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

public class Curriculum extends AbstractPersistable implements Labeled {

    private String code;

    public Curriculum() {
    }

    public Curriculum(int id, String code) {
        super(id);
        this.code = requireNonNull(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
