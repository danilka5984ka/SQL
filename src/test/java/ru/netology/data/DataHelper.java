package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

public class DataHelper {

    private static final String vasyaPassEncrypted = "$2a$10$CNrT4S.Oo27u9x/iujHEvuqwsMTeYAn7xa5H0qAq/dhe1P1Yttr7m";
    private static final String petyaPassEncrypted = "$2a$10$QqnfLHmNwAKFTpaesao90ud169TVhznCueMzmObXFgkoZXQIPCcSe";
    private static final String vasyaLogin = "vasya";
    private static final String vasyaPass = "qwerty123";

    private DataHelper() {
    }

    @SneakyThrows
    private static String requestCode(User user) {
        Thread.sleep(500);
        var runner = new QueryRunner();
        var sqlRequestSortByTime = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app-db", "user", "pass"
                );

        ) {
            return runner.query(conn, sqlRequestSortByTime, user.getId(), new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static User getAuthInfo() {
        return new User(getDataBaseId(vasyaLogin), vasyaLogin, vasyaPass);
    }

    public static String getVerificationCodeFor(User authInfo) {
        return requestCode(authInfo);
    }

    public static String getRandomPass() {
        return new Faker().internet().password();
    }

    @SneakyThrows
    private static String getDataBaseId(String login) {
        var runner = new QueryRunner();
        var sqlRequestTakeUserId = "SELECT id FROM users WHERE login = ?";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app-db", "user", "pass"
                );
        ) {
            return runner.query(conn, sqlRequestTakeUserId, login, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static void clearSUTData() {
        var runner = new QueryRunner();
        var sqlDeleteAllAuthCodes = "DELETE FROM auth_codes;";
        var sqlDeleteAllCards = "DELETE FROM cards;";
        var sqlDeleteAllUsers = "DELETE FROM users;";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app-db", "user", "pass"
                );
        ) {
            runner.update(conn, sqlDeleteAllAuthCodes);
            runner.update(conn, sqlDeleteAllCards);
            runner.update(conn, sqlDeleteAllUsers);
        }
    }

    @SneakyThrows
    public static void resetSUTData() {
        var runner = new QueryRunner();
        var sqlInsertUsers = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";

        String vasyaId = "1";
        String vasyaLogin = "vasya";
        String vasyaPass = vasyaPassEncrypted;
        String petyaId = "2";
        String petyaLogin = "petya";
        String petyaPass = petyaPassEncrypted;

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app-db", "user", "pass"
                );
        ) {
            runner.update(conn, sqlInsertUsers, vasyaId, vasyaLogin, vasyaPass);
            runner.update(conn, sqlInsertUsers, petyaId, petyaLogin, petyaPass);
        }
    }
}