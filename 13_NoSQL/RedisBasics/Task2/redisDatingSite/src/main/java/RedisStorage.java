import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static java.lang.System.out;

public class RedisStorage {

    // Объект для работы с Redis
    private RedissonClient redisson;

    // Объект для работы с ключами
    private RKeys rKeys;

    // Объект для работы с Sorted Set'ом
    private RScoredSortedSet<String> registeredUsers;

    private final static String KEY = "USERS";

    // Хранилище пользователей
    private UserList userList = new UserList();

    // Дата регистрации
    private double getTs() {
        return new Date().getTime() / 1000;
    }

    // Добавление пользователя в сортед сет
    void registerUser(int user_id) {
        //ZADD USERS
        double score = getTs();
        registeredUsers.add(score, String.valueOf(user_id));
        userList.addUserToList(user_id, new User(score, user_id));
//        User user = new User(score, user_id);
    }

    // Получает случайного пользователя
    int getRandomUser() {
        return new Random().nextInt(usersCount());
    }

    // Общее число users
    int usersCount() {
        return registeredUsers.count(Double.NEGATIVE_INFINITY, true, Double.POSITIVE_INFINITY, true);
    }

    ArrayList<Integer> getUserLine() {
        // очередь из пользователей
        ArrayList<Integer> userLine = new ArrayList<Integer>();
        registeredUsers.valueRange(0, -1).forEach(s -> userLine.add(Integer.parseInt(s)));
        return userLine;
    }

    // Подключение к Редис
    void init() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        try {
            redisson = Redisson.create(config);
        } catch (RedisConnectionException Exc) {
            out.println("Не удалось подключиться к Redis");
            out.println(Exc.getMessage());
        }
        rKeys = redisson.getKeys();
        registeredUsers = redisson.getScoredSortedSet(KEY);
        // rKeys.delete(KEY);
    }

    void shutdown() {
        redisson.shutdown();
    }

    public UserList getUserList() {
        return userList;
    }
}
