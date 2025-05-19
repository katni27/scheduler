package org.optaplanner.examples.curriculumcourse.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.optaplanner.examples.curriculumcourse.domain.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class CourseScheduleJsonImporter {

    private CourseScheduleJsonImporter() {}

    public static CourseSchedule read(Path jsonPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonPath.toFile());

        List<Teacher> teacherList = new ArrayList<>();
        Map<Integer, Teacher> teacherMap = new HashMap<>();
        for (JsonNode node : root.withArray("teacherList")) {
            int id = node.get("id").asInt();
            Teacher teacher = new Teacher(id, node.get("code").asText());
            teacherList.add(teacher);
            teacherMap.put(id, teacher);
        }

        List<Curriculum> curriculumList = new ArrayList<>();
        Map<Integer, Curriculum> curriculumMap = new HashMap<>();
        for (JsonNode node : root.withArray("curriculumList")) {
            int id = node.get("id").asInt();
            Curriculum cur = new Curriculum(id, node.get("code").asText());
            curriculumList.add(cur);
            curriculumMap.put(id, cur);
        }

        List<Timeslot> timeslotList = new ArrayList<>();
        Map<Integer, Timeslot> timeslotMap = new HashMap<>();
        for (JsonNode node : root.withArray("timeslotList")) {
            int id = node.asInt();
            Timeslot ts = new Timeslot(id);
            timeslotList.add(ts);
            timeslotMap.put(id, ts);
        }

        List<Day> dayList = new ArrayList<>();
        Map<Integer, Day> dayMap = new HashMap<>();
        List<Period> periodList = new ArrayList<>();
        Map<Integer, Period> periodMap = new HashMap<>();

        for (JsonNode dNode : root.withArray("dayList")) {
            int dayId = dNode.get("id").asInt();
            Day day = new Day(dayId);
            day.setDayIndex(dNode.get("dayIndex").asInt());
            day.setPeriodList(new ArrayList<>());
            dayList.add(day);
            dayMap.put(dayId, day);

            for (JsonNode pNode : dNode.withArray("periodList")) {
                int periodId = pNode.get("id").asInt();
                JsonNode tsNode = pNode.get("timeslot");
                Timeslot ts;
                if (tsNode.isObject()) {
                    int tsId = tsNode.get("id").asInt();
                    ts = timeslotMap.computeIfAbsent(tsId, id -> {
                        Timeslot t = new Timeslot(id);
                        return t;
                    });
                    if (tsNode.has("timeslotIndex")) {
                        ts.setTimeslotIndex(tsNode.get("timeslotIndex").asInt());
                    }
                } else {
                    int tsId = tsNode.asInt();
                    ts = timeslotMap.computeIfAbsent(tsId, Timeslot::new);
                }

                Period period = new Period(periodId, day, ts);
                period.setOriginalTimeslotAsId(!tsNode.isObject());
                day.getPeriodList().add(period);
                periodList.add(period);
                periodMap.put(periodId, period);
            }
        }

        List<Room> roomList = new ArrayList<>();
        Map<Integer, Room> roomMap = new HashMap<>();
        for (JsonNode node : root.withArray("roomList")) {
            int id = node.get("id").asInt();
            Room room = new Room(id,
                    node.get("code").asText(),
                    node.get("capacity").asInt(),
                    node.get("type").asInt());
            roomList.add(room);
            roomMap.put(id, room);
        }

        List<Course> courseList = new ArrayList<>();
        Map<Integer, Course> courseMap = new HashMap<>();
        for (JsonNode node : root.withArray("courseList")) {
            int id = node.get("id").asInt();
            Teacher teacher = teacherMap.get(node.get("teacher").asInt());
            Course course = new Course(id,
                    node.get("code").asText(),
                    teacher,
                    node.get("lectureSize").asInt(),
                    node.get("studentSize").asInt(),
                    node.get("minWorkingDaySize").asInt(),
                    node.get("roomType").asInt());

            for (JsonNode curId : node.withArray("curriculumSet")) {
                Curriculum cur = curriculumMap.get(curId.asInt());
                if (cur != null) course.getCurriculumSet().add(cur);
            }
            courseList.add(course);
            courseMap.put(id, course);
        }

        List<UnavailablePeriodPenalty> penaltyList = new ArrayList<>();
        for (JsonNode node : root.withArray("unavailablePeriodPenaltyList")) {
            int id = node.get("id").asInt();
            Course course = courseMap.get(node.get("course").asInt());
            Period period = periodMap.get(node.get("period").asInt());
            penaltyList.add(new UnavailablePeriodPenalty(id, course, period));
        }

        List<Lecture> lectureList = new ArrayList<>();
        for (JsonNode node : root.withArray("lectureList")) {
            int id = node.get("id").asInt();
            Course course = courseMap.get(node.get("course").asInt());
            Lecture lecture = new Lecture(
                    id,
                    course,
                    node.get("lectureIndexInCourse").asInt(),
                    node.get("pinned").asBoolean());

            if (!node.get("period").isNull()) {
                lecture.setPeriod(periodMap.get(node.get("period").asInt()));
            }
            if (!node.get("room").isNull()) {
                lecture.setRoom(roomMap.get(node.get("room").asInt()));
            }
            lectureList.add(lecture);
        }

        CourseSchedule schedule = new CourseSchedule(root.path("id").asInt());
        schedule.setName(root.path("name").asText());

        schedule.setTeacherList(teacherList);
        schedule.setCurriculumList(curriculumList);
        schedule.setCourseList(courseList);
        schedule.setDayList(dayList);
        schedule.setTimeslotList(timeslotList);
        schedule.setPeriodList(periodList);
        schedule.setRoomList(roomList);
        schedule.setUnavailablePeriodPenaltyList(penaltyList);
        schedule.setLectureList(lectureList);

        return schedule;
    }
}