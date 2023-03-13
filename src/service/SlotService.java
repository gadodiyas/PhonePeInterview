package service;

import exception.NoFavraoubleSlotFoundForGivenDate;
import model.Calender;
import model.Event;
import model.TimeSlot;
import model.User;
import repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SlotService {

    private UserRepository userRepository = new UserRepository();

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

    private void printAvailableSlots(List<TimeSlot> res) {
        for (TimeSlot slot : res) {
            System.out.println("StartTime: " + slot.getStartTime() + "    EndTime: " + slot.getEndTime());
        }
    }
}
