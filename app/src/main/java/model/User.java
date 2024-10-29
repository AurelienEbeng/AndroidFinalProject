package model;


public class User {
    public int userId;
    public int personId;
    public String password;

    public User(int userId, String password){
        this.userId = userId;
        personId = userId;
        this.password = password;
    }

    public User(String password) {
        this.password = password;
        userId += 100;
        this.personId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User id: "+userId+"\tPerson id: "+"\tPassword: "+ password;
    }
}
