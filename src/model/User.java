package model;

public class User {
    private static int cnt = 0;
    private int userId;
    private String userName;
    private Calender calender = new Calender();

    public User(String userName) {
        cnt++;
        userId = cnt;
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Calender getCalender() {
        return calender;
    }

    public void  printWorkingHours(){
        System.out.println("Working hrs for user:" + userName);
        for (TimeSlot timeSlot : calender.getWorkingHours()){
            System.out.println("startTime:" + timeSlot.getStartTime() + "    endtime:" + timeSlot.getEndTime());
        }

    }


}
