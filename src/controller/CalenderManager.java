package controller;

import exception.CanNotDeleteEventException;
import exception.NoFavraoubleSlotFoundForGivenDate;
import exception.NotValidFrequencyException;
import model.Calender;
import model.Event;
import model.TimeSlot;
import model.User;
import model.enums.RecurringFrequency;
import repository.UserRepository;
import service.EventService;
import service.SlotService;
import service.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class CalenderManager {
    UserRepository userRepository = new UserRepository();

    UserService userService = new UserService();

    EventService eventService = new EventService();

    SlotService slotService = new SlotService();

    private  static  int cnt = 0;

    public int addUser(String userName) {
       return userService.addUser(userName);
    }

    public void addWorkingHour(List<TimeSlot> workingHours, int userId) {
       userService.addWorkingHour(workingHours, userId);
    }


    public List<Event> getEvents(int userId, LocalDate date) {
        return eventService.getEvents(userId, date);
    }

    public List<Event> getEvents(int userId) {
        return eventService.getEvents(userId);
    }

    public Event createEvent(List<Integer> userIds, LocalDate date, LocalTime startTime, LocalTime endTime, String eventName, int hostId) {
        return eventService.createEvent(userIds, date, startTime, endTime, eventName, hostId);
    }

    public void deleteEvent(Event event, int hostId) {
        if(event.getHostId() != hostId) {
            throw new CanNotDeleteEventException();
        }
        eventService.deleteEvent(event, hostId);
    }

    public List<Event> getConflicts(int userId, LocalDate date) {
        User user = userRepository.getUser(userId);
        List<Event> events = eventService.getEvents(userId, date);

        if(events.isEmpty()) {
            System.out.println("No events found for date : " + date);
            return new ArrayList<>();
        }
        return new ArrayList<>(eventService.getConflicts(events));
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
       return slotService.getFavrableSlots(userIds, duration, date);
    }


    public int createEvent(List<Integer> userIds, LocalDate date, LocalTime startTime, LocalTime endTime, String eventName, int userId_2, RecurringFrequency recurringFrequency, int instances) {

        int recurringId = ++cnt;
        LocalDate recurringDate = date;
        System.out.println("Creating recurring events");
        for(int i = 0; i < instances; i++) {

            Event event = createEvent(userIds, recurringDate, startTime, endTime, eventName, userId_2);
            recurringDate = updateDateForNextOccurance(recurringDate, recurringFrequency);
            event.setRecurringId(recurringId);
        }
        return recurringId;

    }

    private LocalDate updateDateForNextOccurance(LocalDate recurringDate, RecurringFrequency recurringFrequency) {
        switch (recurringFrequency) {
            case DAILY:
                return recurringDate.plusDays(1);
            case WEEKLY:
                return recurringDate.plusWeeks(1);
            case MONTHLY:
                return recurringDate.plusMonths(1);
            default:
                throw new NotValidFrequencyException();
        }
    }
}
