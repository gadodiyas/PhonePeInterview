package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private static int cnt = 0;

    private int eventId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String eventName;
    private List<Integer> userIds = new ArrayList<>();

    private int hostId;

    private Integer recurringId;


    public Event(LocalDate date, LocalTime startTime, LocalTime endTime, String eventName, List<Integer> userIds, int hostId) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventName = eventName;
        this.userIds = userIds;
        this.hostId = hostId;
        eventId = ++cnt;
    }

    public int getHostId() {
        return hostId;
    }

    public int getEventId() {
        return eventId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getEventName() {
        return eventName;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setRecurringId(Integer recurringId) {
        this.recurringId = recurringId;
    }

    public Integer getRecurringId() {
        return recurringId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", eventName='" + eventName + '\'' +
                ", userIds=" + userIds +
                '}';
    }
}
