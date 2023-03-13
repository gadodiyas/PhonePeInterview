package controller;

import exception.CanNotDeleteEventException;
import exception.NoSlotToday;
import model.Calender;
import model.Event;
import model.TimeSlot;
import model.User;
import repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class CalenderManager {
    UserRepository userRepository = new UserRepository();

    public int addUser(String userName) {
        User user = new User(userName);
        userRepository.addUser(user);
        return user.getUserId();
    }


    public void addWorkingHour(List<TimeSlot> workingHours, int userId) {
        User user = userRepository.getUser(userId);
        user.getCalender().getWorkingHours().addAll(workingHours);
        System.out.println("Working hours is updated for user:" + user.getUserName());
        user.printWorkingHours();
    }


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

        if(event.getHostId() != hostId) {
            throw new CanNotDeleteEventException();
        }

        for(int userId : event.getUserIds()) {
            User user = userRepository.getUser(userId);
            user.getCalender().getEventsByDate().get(event.getDate()).remove(event);
        }
        System.out.println("Event deleted");
    }

    public List<Event> getConflicts(int userId, LocalDate date) {
        User user = userRepository.getUser(userId);
        List<Event> events = user.getCalender().getEventsByDate().get(date);
        Set<Event> res = new HashSet<>();
        if(events.isEmpty()) {
            System.out.println("No events found for date : " + date);
            return new ArrayList<>();
        }

        Collections.sort(events,(x, y) -> x.getStartTime().compareTo(y.getStartTime()) );
        for (int i = 1; i < events.size(); i++) {
            if(events.get(i).getStartTime().isBefore(events.get(i-1).getEndTime())) {
                res.add(events.get(i));
                res.add(events.get(i-1));
            }
        }

        printEvents(new ArrayList<>(res)); // to do take this in var
        return new ArrayList<>(res);
    }

    //move to service layer
    public List<TimeSlot> getFavrableSlots(List<Integer> userIds, int duration, LocalDate date) {
        List<TimeSlot> res = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for(int id : userIds) {
            users.add(userRepository.getUser(id));
        }
        //find common working slot

        TimeSlot timeSlot = users.get(0).getCalender().getWorkingHours().get(0);

        LocalTime startTime = LocalTime.now().isBefore(timeSlot.getStartTime()) ? timeSlot.getStartTime() : LocalTime.now();
        LocalTime endTime = timeSlot.getEndTime();

        if(endTime.isBefore(startTime)) {
            throw new NoSlotToday();
        }

        for (LocalTime t = startTime; t.isBefore(endTime.minusHours(duration)); t = t.plusHours(duration)) {
            for(User u : users) {
                Calender c = u.getCalender();
                if( c.getEventsByDate().get(date).contains(t) ) // to do check in working hours
                    break;
            }
            res.add(new TimeSlot(t, t.plusHours(duration)));

        }
        return res;

    }
}
