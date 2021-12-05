package ru.sfedu;

public class Constants {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
    public static final String WARN = "WARN";

    public static final String CSV_PATH = "csv_path";
    public static final String CSV_FILE_EXTENTION = "csv";
    public static final String XML_PATH = "xml_path";
    public static final String XML_FILE_EXTENTION = "xml";

    public static final String DB_PATH =  "db_path";
    public static final String DB_NAME = "db_name";
    public static final String DB_PASSWORD = "db_password";
    public static final String DB_LOGIN = "db_login";

    public static final String SQL_INSERT_USERS = "INSERT INTO USERS " +
            "VALUES (%d, '%s', %d);";
    public static final String SQL_INSERT_USERS_WITHOUT_ID = "INSERT INTO USERS " +
            "(name, age) VALUES ('%s', %d);";;
    public static final String SQL_CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS USERS " +
            "(id INT NOT NULL AUTO_INCREMENT, " +
            " name VARCHAR(255), " +
            " age INTEGER," +
            "PRIMARY KEY (id))";

    public static final String SQL_SELECT_FROM_USERS = "SELECT * FROM USERS";
    public static final String SQL_SELECT_FROM_USERS_BY_ID = "SELECT * FROM users WHERE id = %d";
    public static final String SQL_UPDATE_USERS = "UPDATE USERS SET name = '%s', age = %d WHERE id = %d";
    public static final String SQL_DELETE_ALL_USERS = "DELETE FROM users";
    public static final String SQL_DELETE_BY_ID_USERS = "DELETE FROM users WHERE ID = %d;";
    public static final String SQL_DROP_USERS_TABLE = "DROP TABLE IF EXISTS USERS";

}
