package org.optaplanner.examples.curriculumcourse.domain;
import static java.util.Objects.requireNonNull;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

public class Course extends AbstractPersistable {

    private String code;

    @JsonIdentityReference(alwaysAsId = true)
    private Teacher teacher;

    private int lectureSize;
    private int minWorkingDaySize;

    @JsonIdentityReference(alwaysAsId = true)
    private Set<Curriculum> curriculumSet;

    private int studentSize;
    private int roomType;

    public Course() {
    }

    public Course(int id, String code, Teacher teacher, int lectureSize, int studentSize, int minWorkingDaySize,
                  int roomType, Curriculum... curricula) {
        super(id);
        this.code = requireNonNull(code);
        this.teacher = requireNonNull(teacher);
        this.lectureSize = lectureSize;
        this.minWorkingDaySize = minWorkingDaySize;
        this.curriculumSet = Arrays.stream(curricula).collect(Collectors.toCollection(LinkedHashSet::new));
        this.studentSize = studentSize;
        this.roomType = roomType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getLectureSize() {
        return lectureSize;
    }

    public void setLectureSize(int lectureSize) {
        this.lectureSize = lectureSize;
    }

    public int getMinWorkingDaySize() {
        return minWorkingDaySize;
    }

    public void setMinWorkingDaySize(int minWorkingDaySize) {
        this.minWorkingDaySize = minWorkingDaySize;
    }

    public Set<Curriculum> getCurriculumSet() {
        return curriculumSet;
    }

    public void setCurriculumSet(Set<Curriculum> curriculumSet) {
        this.curriculumSet = curriculumSet;
    }

    public int getStudentSize() {
        return studentSize;
    }

    public void setStudentSize(int studentSize) {
        this.studentSize = studentSize;
    }

    @Override
    public String toString() {
        return code;
    }

}
