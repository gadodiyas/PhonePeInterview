package controller;

import exception.CanNotDeleteEventException;
import exception.NoFavraoubleSlotFoundForGivenDate;
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
        List<Event> resList = new ArrayList<>(res);
        if(resList.isEmpty()) {
            System.out.println("No Conflicting Events found for user:" + user.getUserName() + "    date:" + date);
        } else {
            System.out.println("Below Conflicting Events found for user:" + user.getUserName() + "    date:" + date);
            printEvents(resList); // to do take this in var
        }
        return resList;
    }

    //move to service layer
    /*public List<TimeSlot> getFavrableSlots(List<Integer> userIds, int duration, LocalDate date) {
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

    }*/

    public List<TimeSlot> getFavrableSlots(List<Integer> userIds, int duration, LocalDate date) {
        List<TimeSlot> res = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for(int id : userIds) {
            users.add(userRepository.getUser(id));
        }
        //iterate through each working slot of an user

        for(TimeSlot timeSlot : users.get(0).getCalender().getWorkingHours()) {
            //Assuming we show max 5 available slots
            if(res.size() == 5)
                break;
            //handle if meeting is for today
            if(date.equals(LocalDate.now()) && LocalTime.now().isAfter(timeSlot.getEndTime())) {
                continue;
            }
            LocalTime startTime;
            //handle if meeting is for today
            if(date.equals(LocalDate.now()))
                startTime = LocalTime.now().isBefore(timeSlot.getStartTime()) ? timeSlot.getStartTime() : LocalTime.of(LocalTime.now().getHour(),0);
            else
                startTime = timeSlot.getStartTime();

            LocalTime endTime = timeSlot.getEndTime();

            if(endTime.isBefore(startTime)) {
                throw new NoFavraoubleSlotFoundForGivenDate();
            }

            for (LocalTime t = startTime; t.isBefore(endTime.minusHours(duration)) ; t = t.plusHours(1)) {
                if(isSlotAvailableForAllUsers(date, users, t, t.plusHours(duration))){
                    res.add(new TimeSlot(t, t.plusHours(duration)));
                    if(res.size() == 5)
                        break;
                }
            }

        }
        if(res.isEmpty()) {
            throw new NoFavraoubleSlotFoundForGivenDate();
        }
        System.out.println("Favourable slots for date:" + date + "     duration:" + duration);
        printAvailableSlots(res);
        return res;

    }

    private void printAvailableSlots(List<TimeSlot> res) {
        for (TimeSlot slot : res) {
            System.out.println("StartTime: " + slot.getStartTime() + "    EndTime: " + slot.getEndTime());
        }
    }

    private boolean isSlotAvailableForAllUsers(LocalDate date, List<User> users, LocalTime startTime, LocalTime endTime) {
        for(User u : users) {
            Calender c = u.getCalender();
            if(isSlotBookedForAnyEvent(c.getEventsByDate().get(date), startTime,endTime)
                    || !isSlotInWorkingHours(u,startTime, endTime)) // to do check in working hours
                return false;

        }
        return true;
    }

    private boolean isSlotBookedForAnyEvent(List<Event> events, LocalTime targetStartTime, LocalTime targetEndTime) {
        if(events == null) {
            return false;
        }
        for (Event e : events) {
            if(isTimeSlotInTheRange(targetStartTime, targetEndTime, e.getStartTime(), e.getEndTime()))
                return true;
        }
        return false;
    }

    private boolean isSlotInWorkingHours(User u, LocalTime startTime, LocalTime endTime) {
        List<TimeSlot> timeSlots = u.getCalender().getWorkingHours();

        for (TimeSlot timeSlot: timeSlots) {
            if(isTimeSlotInTheRange(startTime, endTime, timeSlot.getStartTime(), timeSlot.getEndTime()))
                return true;
        }
        return false;

    }

    private boolean isTimeSlotInTheRange(LocalTime targetStartTime, LocalTime targetEndTime, LocalTime rangeStartTime, LocalTime rangeEndTime ) {
        if((rangeStartTime.equals(targetStartTime) || rangeStartTime.isBefore(targetStartTime)) && (rangeEndTime.isAfter(targetEndTime)) || rangeEndTime.equals(targetEndTime))
            return true;
        return false;
    }
}
