import java.util.Random;

public class Main {

    // Зарегистрировалось пользователей
    private static final int USERS = 20;

    // задержка для показа
    private static final int SLEEP = 1000;
    // задержка для регистрации
    private static final int REG_SLEEP = 50;

    public static void main(String[] args) throws Exception {

        RedisStorage redis = new RedisStorage();
        redis.init();

        // регистрируем пользователей
        for (int i = 0; i < USERS; i++) {
            redis.registerUser(i); // регистрируем пользователя, задавая каждому score в зависимости от времени
            Thread.sleep(REG_SLEEP);
        }
        // Бесконечный цикл перебора юзеров
        for (; ; ) {
            for (int j = 0; j < redis.getUserLine().size(); j++) {  // выводим всех юзеров по очереди
                // Проверяем, показывали ли в данном цикле юзера и если показывали, то переходим к следующему юзеру
                User user = redis.getUserList().getUserById(j);
                if (!user.isShown()) {      // Не показывали, значит:
                    // Решаем будет ли внеочередное поднятие
                    if (Math.random() <= 0.1) {    // показ внеочередной
                        User paidPromoUser = getRandomUser(redis);  // получаем пользователя и проверяем что в этом цикле его еще не показывали
                        paidPromoUser.setHasShown(true);    // отмечаем юзера, что уже показали и показываем
                        System.out.println("Показываем на главной: " + paidPromoUser.getUserId());
                        Thread.sleep(SLEEP); // Ожидаем 1 секунду
                    }
                    if (!user.isShown()) { // Повторная проверка показа, для исключения дублирования в одном цикле
                        user.setHasShown(true); // Показываем юзеров по очереди в главном цикле
                        System.out.println("Показываем на главной: " + j);
                        Thread.sleep(SLEEP);
                    }
                } else { // Данного юзера в цикле уже показывали, пропускаем
                    continue;
                }
            }
            System.out.println("===============================================");
            // сбрасываем значение всех показанных в данном цикле пользователей
            for (int key : redis.getUserList().userList.keySet()) {
                redis.getUserList().userList.get(key).setHasShown(false);
            }
        }
    }

    private static User getRandomUser(RedisStorage redis) {
        User paidPromoUser;
        for (; ; ) {
            int paidPromoUserId = new Random().nextInt(USERS);
            paidPromoUser = redis.getUserList().getUserById(paidPromoUserId);
            if (!paidPromoUser.isShown()) {
                break;
            }
        }
        return paidPromoUser;
    }
}
