package repository;

import exception.UserNotFoundException;
import model.Calender;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    static Map<Integer, User> map = new HashMap<>();

    public User getUser(int id) {
        User user = map.get(id);
        if(user== null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    public void addUser(User u) {
        System.out.println("------------------------------------");
        System.out.println("Added User,Name :" +  u.getUserName() + "   Id: " + u.getUserId() );
        map.put(u.getUserId(), u);
    }

}
