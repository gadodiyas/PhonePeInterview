package model;

import java.time.LocalDate;
import java.util.*;

public class Calender {
    private List<TimeSlot> workingHours = new ArrayList<>();
    private Map<LocalDate, List<TimeSlot>> busySlotsByDate = new HashMap<>();

    private Map<LocalDate, List<Event>> eventsByDate = new HashMap<>();

    public List<TimeSlot> getWorkingHours() {
        return workingHours;
    }

    public Map<LocalDate, List<TimeSlot>> getBusySlotsByDate() {
        return busySlotsByDate;
    }

    public Map<LocalDate, List<Event>> getEventsByDate() {
        return eventsByDate;
    }
}
