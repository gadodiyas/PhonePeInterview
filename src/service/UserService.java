package service;

import model.TimeSlot;
import model.User;
import repository.UserRepository;

import java.util.List;

public class UserService {
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

    public User getUser(int id) {
        return userRepository.getUser(id);
    }
}
