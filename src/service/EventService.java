package service;

import exception.CanNotDeleteEventException;
import model.Event;
import model.User;
import repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class EventService {

    UserRepository userRepository = new UserRepository();

    public List<Event> getEvents(int userId, LocalDate date) {
        User user = userRepository.getUser(userId);
        List<Event> events = user.getCalender().getEventsByDate().get(date);

        System.out.println("Events for User:" + user.getUserName() + "    Date: " + date);
        printEvents(events);
        return events;
    }

    private void printEvents(List<Event> events) {
        if(events != null) {
            for (Event event : events) {
                System.out.println(event.toString());
            }
        }
    }

    public List<Event> getEvents(int userId) {
        User user = userRepository.getUser(userId);
        Map<LocalDate, List<Event>> map = user.getCalender().getEventsByDate();


        List<Event> events = new ArrayList<>();
        for (LocalDate date: map.keySet()) {
            events.addAll(map.get(date));
        }
        System.out.println("Events for User:" + user.getUserName());
        printEvents(events);
        return events;
    }

    public Event createEvent(List<Integer> userIds, LocalDate date, LocalTime startTime, LocalTime endTime, String eventName, int hostId) {

        Event event = new Event(date, startTime, endTime, eventName, userIds, hostId);

        for (int userId : userIds) {
            User user = userRepository.getUser(userId);
            //isTimeinWorkingHour() to do
            Map<LocalDate, List<Event>> map = user.getCalender().getEventsByDate();
            if(!map.containsKey(date)) {
                map.put(date, new ArrayList<>());
            }
            map.get(date).add(event);
        }

        System.out.println("Event created: " + event.toString());
        return  event;
    }

    public void deleteEvent(Event event, int hostId) {

        for(int userId : event.getUserIds()) {
            User user = userRepository.getUser(userId);
            user.getCalender().getEventsByDate().get(event.getDate()).remove(event);
        }
        System.out.println("Event deleted");
    }

    public Set<Event> getConflicts(List<Event> events) {
        Set<Event> res = new HashSet<>();

        Collections.sort(events,(x, y) -> x.getStartTime().compareTo(y.getStartTime()) );
        for (int i = 1; i < events.size(); i++) {
            if(events.get(i).getStartTime().isBefore(events.get(i-1).getEndTime())) {
                res.add(events.get(i));
                res.add(events.get(i-1));
            }
        }
        if(res.isEmpty()) {
            System.out.println("No Conflicting Events found");
        } else {
            System.out.println("Below Conflicting Events found");
            printEvents(new ArrayList<>(res)); // to do take this in var
        }
        return res;
    }
}
