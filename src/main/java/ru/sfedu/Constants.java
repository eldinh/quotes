package ru.sfedu;

public class Constants {
    public static final String MOEX_LINK = "moex_link";

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

    public static final String CSV_PATH = "csv_path";
    public static final String CSV_FILE_EXTENTION = "csv";
    public static final String XML_PATH = "xml_path";
    public static final String XML_FILE_EXTENTION = "xml";

    public static final String DB_PATH =  "db_path";
    public static final String DB_NAME = "db_name";
    public static final String DB_PASSWORD = "db_password";
    public static final String DB_LOGIN = "db_login";

    public static final String SQL_INSERT_USERS = "INSERT INTO users" +
            "  (id, name, age) VALUES (?, ?, ?);";
    public static final String SQL_INSERT_USERS_WITHOUT_ID = "INSERT INTO users" +
            "  (name, age) VALUES (?, ?);";
    public static final String SQL_CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS USERS " +
            "(id INT NOT NULL AUTO_INCREMENT, " +
            " name VARCHAR(255), " +
            " age INTEGER," +
            "PRIMARY KEY (id))";

    public static final String SQL_SELECT_FROM_USERS = "SELECT * FROM USERS";
    public static final String SQL_SELECT_FROM_USERS_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String SQL_UPDATE_USERS = "UPDATE USERS SET name = ?, age = ? WHERE id = ?";
    public static final String SQL_DELETE_ALL_USERS = "DELETE FROM users";
    public static final String SQL_DELETE_BY_ID_USERS = "DELETE FROM users WHERE ID = ?;";
    public static final String SQL_DROP_USERS_TABLE = "DROP TABLE IF EXISTS USERS";

}
