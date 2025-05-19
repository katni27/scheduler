package org.optaplanner.examples.curriculumcourse.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.optaplanner.examples.curriculumcourse.domain.*;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public final class CourseScheduleJsonExporter {

    private CourseScheduleJsonExporter() {}

    public static void write(File file, CourseSchedule schedule) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("id", schedule.getId());
        root.put("name", schedule.getName());

        ArrayNode teachers = root.putArray("teacherList");
        schedule.getTeacherList().stream()
                .sorted(Comparator.comparingLong(Teacher::getId))
                .forEach(t -> {
                    ObjectNode n = teachers.addObject();
                    n.put("id", t.getId());
                    n.put("code", t.getCode());
                });

        ArrayNode curricula = root.putArray("curriculumList");
        schedule.getCurriculumList().stream()
                .sorted(Comparator.comparingLong(Curriculum::getId))
                .forEach(c -> {
                    ObjectNode n = curricula.addObject();
                    n.put("id", c.getId());
                    n.put("code", c.getCode());
                });

        ArrayNode courses = root.putArray("courseList");
        schedule.getCourseList().stream()
                .sorted(Comparator.comparingLong(Course::getId))
                .forEach(c -> {
                    ObjectNode n = courses.addObject();
                    n.put("id", c.getId());
                    n.put("code", c.getCode());
                    n.put("teacher", c.getTeacher().getId());
                    n.put("lectureSize", c.getLectureSize());
                    n.put("minWorkingDaySize", c.getMinWorkingDaySize());
                    ArrayNode curSet = n.putArray("curriculumSet");
                    c.getCurriculumSet().stream()
                            .map(Curriculum::getId)
                            .sorted()
                            .forEach(curSet::add);
                    n.put("studentSize", c.getStudentSize());
                    n.put("roomType", c.getRoomType());
                });

        ArrayNode days = root.putArray("dayList");
        schedule.getDayList().stream()
                .sorted(Comparator.comparingLong(Day::getId))
                .forEach(d -> {
                    ObjectNode dn = days.addObject();
                    dn.put("id", d.getId());
                    dn.put("dayIndex", d.getDayIndex());
                    ArrayNode periods = dn.putArray("periodList");
                    d.getPeriodList().stream()
                            .distinct()
                            .sorted(Comparator.comparingLong(Period::getId))
                            .forEach(p -> {
                                ObjectNode pn = periods.addObject();
                                pn.put("id", p.getId());
                                pn.put("day", p.getDay().getId());

                                if (p.isOriginalTimeslotAsId()) {
                                    pn.put("timeslot", p.getTimeslot().getId());
                                } else {
                                    ObjectNode tsn = pn.putObject("timeslot");
                                    tsn.put("id", p.getTimeslot().getId());
                                    tsn.put("timeslotIndex", p.getTimeslot().getTimeslotIndex());
                                }
                            });
                });

        ArrayNode timeslots = root.putArray("timeslotList");
        schedule.getTimeslotList().stream()
                .map(Timeslot::getId)
                .sorted()
                .forEach(timeslots::add);

        ArrayNode periodList = root.putArray("periodList");
        schedule.getPeriodList().stream()
                .map(Period::getId)
                .sorted()
                .forEach(periodList::add);

        ArrayNode rooms = root.putArray("roomList");
        schedule.getRoomList().stream()
                .sorted(Comparator.comparingLong(Room::getId))
                .forEach(r -> {
                    ObjectNode rn = rooms.addObject();
                    rn.put("id", r.getId());
                    rn.put("code", r.getCode());
                    rn.put("capacity", r.getCapacity());
                    rn.put("type", r.getType());
                });

        ArrayNode penalties = root.putArray("unavailablePeriodPenaltyList");
        schedule.getUnavailablePeriodPenaltyList().stream()
                .sorted(Comparator.comparingLong(UnavailablePeriodPenalty::getId))
                .forEach(p -> {
                    ObjectNode pn = penalties.addObject();
                    pn.put("id", p.getId());
                    pn.put("course", p.getCourse().getId());
                    pn.put("period", p.getPeriod().getId());
                });

        ArrayNode lectures = root.putArray("lectureList");
        schedule.getLectureList().stream()
                .sorted(Comparator.comparingLong(Lecture::getId))
                .forEach(l -> {
                    ObjectNode ln = lectures.addObject();
                    ln.put("id", l.getId());
                    ln.put("course", l.getCourse().getId());
                    ln.put("lectureIndexInCourse", l.getLectureIndexInCourse());
                    ln.put("pinned", l.isPinned());
                    if (l.getPeriod() == null) {
                        ln.putNull("period");
                    } else {
                        ln.put("period", l.getPeriod().getId());
                    }
                    if (l.getRoom() == null) {
                        ln.putNull("room");
                    } else {
                        ln.put("room", l.getRoom().getId());
                    }
                });

        if (schedule.getScore() != null) {
            root.put("score", schedule.getScore().toString());
        } else {
            root.putNull("score");
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }
}
