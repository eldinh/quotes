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

    public static final String SQL_INSERT_USER = "INSERT INTO USER " +
            "VALUES (%d, '%s', %d);";
    public static final String SQL_INSERT_USER_WITHOUT_ID = "INSERT INTO USER " +
            "(name, age) VALUES ('%s', %d);";;
    public static final String SQL_CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS USER " +
            "(id INT NOT NULL AUTO_INCREMENT, " +
            " name VARCHAR(255), " +
            " age INTEGER," +
            "PRIMARY KEY (id))";
    public static final String SQL_SELECT_FROM_USER = "SELECT * FROM USER";
    public static final String SQL_SELECT_FROM_USER_BY_ID = "SELECT * FROM user WHERE id = %d";
    public static final String SQL_UPDATE_USER = "UPDATE USERS SET name = '%s', age = %d WHERE id = %d";
    public static final String SQL_DELETE_ALL_USER = "DELETE FROM user";
    public static final String SQL_DELETE_BY_ID_USER = "DELETE FROM user WHERE ID = %d;";
    public static final String SQL_DROP_USER_TABLE = "DROP TABLE IF EXISTS USER";



    public static final String MONGODB_USER = "mongodb_user";
    public static final String MONGODB_PASSWORD = "mongodb_password";
    public static final String MONGODB_DATABASE = "mongodb_database";
    public static final String MONGODB_CONNECT_URI = "mongodb_connect_uri";

    public static final String H2_DRIVER = "h2_driver";
    public static final String JDBC_CONNECTION = "jdbc_connection";

    public static final String USER_ID_COLUMN = "id";
    public static final String USER_NAME_COLUMN = "name";
    public static final String USER_AGE_COLUMN = "age";

    public static final String MONGODB_TIME_FIELD = "time";
    public static final String MONGODB_COMMAND_FIELD = "command";
    public static final String MONGODB_REPOSITORY_FIELD = "repository";
    public static final String MONGODB_CHANGES_FIELD = "changes";


}
