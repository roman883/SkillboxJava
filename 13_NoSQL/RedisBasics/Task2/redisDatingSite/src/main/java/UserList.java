import java.util.HashMap;
import java.util.HashSet;

public class UserList {

    HashMap<Integer, User> userList;

    public HashMap<Integer, User> getUserList() {
        return userList;
    }

    public UserList() {
        userList = new HashMap<>();
    }

    public void setUserList(HashMap<Integer, User> userList) {
        this.userList = userList;
    }

    public UserList(HashMap<Integer, User> userList) {
        this.userList = userList;
    }

    public void addUserToList(int userId, User user) {
        userList.put(userId, user);
    }

    public User getUserById(int user_id) {
        if (userList.containsKey(user_id)) {
            return userList.get(user_id);
        } else {
            System.out.println("Пользователя с таким рейтингом нет");
            return null;
        }
    }
}
