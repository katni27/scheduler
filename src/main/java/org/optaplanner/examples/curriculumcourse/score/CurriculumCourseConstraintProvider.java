package org.optaplanner.examples.curriculumcourse.score;

import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ONE_HARD;
import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ofHard;
import static org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore.ofSoft;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countDistinct;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.optaplanner.examples.curriculumcourse.domain.Curriculum;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.UnavailablePeriodPenalty;
import org.optaplanner.examples.curriculumcourse.domain.solver.CourseConflict;

public class CurriculumCourseConstraintProvider implements ConstraintProvider {

    private static final int MAX_LECTURES_PER_DAY = 5;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                conflictingLecturesDifferentCourseInSamePeriod(factory),
                conflictingLecturesSameCourseInSamePeriod(factory),
                roomOccupancy(factory),
                unavailablePeriodPenalty(factory),
                curriculumDailyLoadLimit(factory),
                roomCapacity(factory),
                minimumWorkingDays(factory),
                curriculumCompactness(factory),
                roomStability(factory),
                roomTypeMatch(factory)
        };
    }

    // ************************************************************************
    // Жёсткие ограничения (Hard constraints)
    // ************************************************************************

    /**
     * Ограничение: лекции, принадлежащие конфликтующим курсам, не должны проходить в одном и том же периоде.
     */
    Constraint conflictingLecturesDifferentCourseInSamePeriod(ConstraintFactory factory) {
        return factory.forEach(CourseConflict.class)
                .join(Lecture.class,
                        equal(CourseConflict::getLeftCourse, Lecture::getCourse))
                .join(Lecture.class,
                        equal((courseConflict, lecture1) ->
                                courseConflict.getRightCourse(), Lecture::getCourse),
                        equal((courseConflict, lecture1) ->
                                lecture1.getPeriod(), Lecture::getPeriod))
                .filter(((courseConflict, lecture1, lecture2) ->
                        lecture1 != lecture2))
                .penalize(ONE_HARD, (courseConflict, lecture1, lecture2) ->
                        courseConflict.getConflictCount())
                .asConstraint("conflictingLecturesDifferentCourseInSamePeriod");
    }

    /**
     * Ограничение: лекции одного и того же курса не могут проходить в одном и том же периоде.
     */
    Constraint conflictingLecturesSameCourseInSamePeriod(ConstraintFactory factory) {
        return factory.forEachUniquePair(Lecture.class,
                        equal(Lecture::getPeriod),
                        equal(Lecture::getCourse))
                .penalize(ONE_HARD, (lecture1, lecture2) -> 1 + lecture1.getCurriculumSet().size())
                .asConstraint("conflictingLecturesSameCourseInSamePeriod");
    }

    /**
     * Ограничение: одна и та же комната не может быть занята более чем одной лекцией в один и тот же период.
     */
    Constraint roomOccupancy(ConstraintFactory factory) {
        return factory.forEachUniquePair(Lecture.class,
                        equal(Lecture::getRoom),
                        equal(Lecture::getPeriod))
                .penalize(ONE_HARD)
                .asConstraint("roomOccupancy");
    }

    /**
     * Ограничение: если лекция назначена на период, когда курс недоступен, накладывается дополнительный штраф.
     */
    Constraint unavailablePeriodPenalty(ConstraintFactory factory) {
        return factory.forEach(UnavailablePeriodPenalty.class)
                .join(Lecture.class,
                        equal(UnavailablePeriodPenalty::getCourse, Lecture::getCourse),
                        equal(UnavailablePeriodPenalty::getPeriod, Lecture::getPeriod))
                .penalize(ofHard(10))
                .asConstraint("unavailablePeriodPenalty");
    }

    private Constraint curriculumDailyLoadLimit(ConstraintFactory factory) {
        return factory.forEach(Lecture.class)
                .filter(lec -> lec.getPeriod() != null)
                .join(Curriculum.class,
                        Joiners.filtering((lec, cur) ->
                                lec.getCourse().getCurriculumSet().contains(cur)))
                .groupBy(
                        (lec, cur) -> cur,
                        (lec, cur) -> lec.getPeriod().getDay(),
                        ConstraintCollectors.countBi())
                .filter((cur, day, count) -> count > (long) MAX_LECTURES_PER_DAY)
                .penalize(
                        HardSoftScore.ONE_HARD,
                        (cur, day, count) -> (int) (count - MAX_LECTURES_PER_DAY))
                .asConstraint("curriculumDailyLoadLimit");
    }

    // ************************************************************************
    // Мягкие ограничения (Soft constraints)
    // ************************************************************************

    /**
     * Ограничение: вместимость аудитории должна соответствовать количеству студентов.
     */
    Constraint roomCapacity(ConstraintFactory factory) {
        return factory.forEach(Lecture.class)
                .filter(lecture -> lecture.getStudentSize() > lecture.getRoom().getCapacity())
                .penalize(ofSoft(1),
                        lecture -> lecture.getStudentSize() - lecture.getRoom().getCapacity())
                .asConstraint("roomCapacity");
    }

    /**
     * Ограничение: тип аудитории должен соответствовать требуемому типу курса.
     */
    Constraint roomTypeMatch(ConstraintFactory factory) {
        return factory.forEach(Lecture.class)
                .filter(lecture -> lecture.getCourse().getRoomType() != -1
                        && lecture.getRoom() != null
                        && lecture.getCourse().getRoomType() != lecture.getRoom().getType())
                .penalize(ofSoft(5))
                .asConstraint("roomTypeMatch");
    }

    /**
     * Ограничение: курс должен быть распределён по минимальному количеству рабочих дней.
     */
    Constraint minimumWorkingDays(ConstraintFactory factory) {
        return factory.forEach(Lecture.class)
                .groupBy(Lecture::getCourse, countDistinct(Lecture::getDay))
                .filter((course, dayCount) -> course.getMinWorkingDaySize() > dayCount)
                .penalize(ofSoft(5),
                        (course, dayCount) -> course.getMinWorkingDaySize() - dayCount)
                .asConstraint("minimumWorkingDays");
    }

    /**
     * Ограничение: компактность расписания для учебных программ.
     */
    Constraint curriculumCompactness(ConstraintFactory factory) {
        return factory.forEach(Curriculum.class)
                .join(Lecture.class,
                        filtering((curriculum, lecture) -> lecture.getCurriculumSet().contains(curriculum)))
                .ifNotExists(Lecture.class,
                        equal((curriculum, lecture) -> lecture.getDay(), Lecture::getDay),
                        equal((curriculum, lecture) -> lecture.getTimeslotIndex(), lecture -> lecture.getTimeslotIndex() + 1),
                        filtering((curriculum, lectureA, lectureB) -> lectureB.getCurriculumSet().contains(curriculum)))
                .ifNotExists(Lecture.class,
                        equal((curriculum, lecture) -> lecture.getDay(), Lecture::getDay),
                        equal((curriculum, lecture) -> lecture.getTimeslotIndex(), lecture -> lecture.getTimeslotIndex() - 1),
                        filtering((curriculum, lectureA, lectureB) -> lectureB.getCurriculumSet().contains(curriculum)))
                .penalize(ofSoft(2))
                .asConstraint("curriculumCompactness");
    }

    /**
     * Ограничение: стабильность использования аудиторий.
     */
    Constraint roomStability(ConstraintFactory factory) {
        return factory.forEach(Lecture.class)
                .groupBy(Lecture::getCourse, countDistinct(Lecture::getRoom))
                .filter((course, roomCount) -> roomCount > 1)
                .penalize(HardSoftScore.ONE_SOFT,
                        (course, roomCount) -> roomCount - 1)
                .asConstraint("roomStability");
    }

}
