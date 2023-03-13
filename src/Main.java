import controller.CalenderManager;
import model.Event;
import model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("-------------------------------------------");
        CalenderManager calenderManager = new CalenderManager();
        int userId_1 = calenderManager.addUser("A");
        int userId_2 = calenderManager.addUser("B");

        TimeSlot workingHr1 = new TimeSlot(LocalTime.of(9, 0), LocalTime.of(18, 0));
        TimeSlot workingHr2 = new TimeSlot(LocalTime.of(19, 0), LocalTime.of(22, 0));
        calenderManager.addWorkingHour(Arrays.asList(workingHr1), userId_1);
        calenderManager.addWorkingHour(Arrays.asList(workingHr1, workingHr2), userId_2);



        Event event1 =  calenderManager.createEvent(Arrays.asList(userId_1, userId_2), LocalDate.of(2023, Month.MARCH, 13),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Standup", userId_1);

        Event event3 =  calenderManager.createEvent(Arrays.asList(userId_1, userId_2), LocalDate.of(2023, Month.MARCH, 13),
                LocalTime.of(17, 0), LocalTime.of(18, 0), "Retro", userId_1);

        Event event2 =  calenderManager.createEvent(Arrays.asList(userId_2), LocalDate.of(2023, Month.MARCH, 13),
                LocalTime.of(12, 0), LocalTime.of(13, 0), "Refinement", userId_2);

        List<Event> eventList1 = calenderManager.getEvents(userId_1, LocalDate.of(2023, Month.MARCH, 13));

        List<Event> eventList2 = calenderManager.getEvents(userId_2);

        calenderManager.deleteEvent(event3, userId_1);

        List<Event> eventList3 = calenderManager.getConflicts(userId_1, LocalDate.of(2023, Month.MARCH, 13));
        List<TimeSlot> timeSlots = calenderManager.getFavrableSlots(Arrays.asList(userId_1, userId_2), 3, LocalDate.of(2023, Month.MARCH, 13));

        List<TimeSlot> timeSlots2 = calenderManager.getFavrableSlots(Arrays.asList(userId_1, userId_2), 3, LocalDate.of(2023, Month.MARCH, 14));
        Event event4 =  calenderManager.createEvent(Arrays.asList(userId_2), LocalDate.of(2023, Month.MARCH, 13),
                LocalTime.of(12, 0), LocalTime.of(13, 0), "Planning", userId_2);
        List<Event> eventList5 = calenderManager.getConflicts(userId_2, LocalDate.of(2023, Month.MARCH, 13));

    }


}