# Scheduler

Java 17 | Maven 3 | OptaPlanner 9.44

## Структура входного JSON

```jsonc
{
  "id": 0,
  "name": "Toy01",

  "teacherList": [ { "id": 0, "code": "Иванов И.И." }, … ],
  "curriculumList": [ { "id": 0, "code": "ЛМ-233" }, … ],
  "timeslotList":   [ 0, 1, 2, 3, 4, 5, 6 ],
  "roomList": [
    { "id": 0, "code": "814/3б", "capacity": 28, "type": 0 },
    { "id": 1, "code": "B",     "capacity":110, "type": 1 }
  ],

  "courseList": [
    {
      "id": 0,
      "code": "Теория управления",
      "teacher": 0,
      "lectureSize": 2,
      "minWorkingDaySize": 1,
      "curriculumSet": [0,1],
      "studentSize": 100,
      "roomType": -1
    }, …
  ],

  "dayList": [
    {
      "id": 0,
      "dayIndex": 0,
      "periodList": [
        { "id": 0, "day": 0, "timeslot": {"id":0,"timeslotIndex":0} },
        …
      ]
    },
    {
      "id": 1,
      "dayIndex": 1,
      "periodList": [
        { "id": 7,  "day": 1, "timeslot": 0 },
        { "id": 8,  "day": 1, "timeslot": 1 }, …
      ]
    }
  ],
  "periodList": [0,1,2,3,4,5,6,7,8,9,10,11,12,13],

  "unavailablePeriodPenaltyList": [
    { "id": 0, "course": 1, "period": 1 }
  ],

  "lectureList": [
    { "id":0, "course":0, "lectureIndexInCourse":0, "pinned":false, "period":null, "room":null },
    …
  ],

  "score": null
}
```

## Сборка и запуск

### без jar (Maven exec)

```bash
cd scheduler

mvn -q compile exec:java \
    -Dexec.mainClass=org.optaplanner.examples.curriculumcourse.app.SchedulerApp \
    -Dexec.jvmArgs="-DmoveThreadCount=AUTO" \
    -Dexec.args="src/main/resources/schedule.json src/main/resources/solverConfig.xml"
```
