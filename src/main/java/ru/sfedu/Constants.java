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
    public static final String DB_PASSWORD = "db_password";
    public static final String DB_LOGIN = "db_login";

    public static final String SQL_INSERT_USER = "INSERT INTO USER " +
                                                "VALUES (%d, '%s', %d);";
    public static final String SQL_INSERT_USER_WITHOUT_ID = "INSERT INTO USER " +
                                                            "(name, age) VALUES ('%s', %d);";
    public static final String SQL_CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS USER " +
                                                        "(id INT NOT NULL AUTO_INCREMENT, " +
                                                        " name VARCHAR(255), " +
                                                        " age INTEGER," +
                                                        "PRIMARY KEY (id))";
    public static final String SQL_SELECT_FROM_USER = "SELECT * FROM USER";
    public static final String SQL_SELECT_FROM_USER_BY_ID = "SELECT * FROM user WHERE id = %d";
    public static final String SQL_UPDATE_USER = "UPDATE USER SET name = '%s', age = %d WHERE id = %d";
    public static final String SQL_DELETE_ALL_USER = "DELETE FROM user";
    public static final String SQL_DELETE_BY_ID_USER = "DELETE FROM user WHERE ID = %d;";
    public static final String SQL_DROP_USER_TABLE = "DROP TABLE IF EXISTS USER";


    public static final String USER_TABLE_NAME = "USER";
    public static final String STOCK_TABLE_NAME = "STOCK";
    public static final String BOND_TABLE_NAME = "BOND";

    public static final String SQL_INSERT = "INSERT INTO %s ";
    public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s ";
    public static final String SQL_SELECT_FROM = "SELECT * FROM %s ";
    public static final String SQL_WHERE = "WHERE ";
    public static final String SQL_UPDATE = "UPDATE %s ";

    public static final String SQL_SET_USER = "SET name = '%s', age = %d ";
    public static final String SQL_SET_STOCK = "SET name = '%s', short_name = '%s', lat_name = '%s', nominal = %f, nominal_value = '%s', issue_date = '%s', " +
                                                    "isin = '%s', issue_size = %d, market_type = '%s', type = '%s', dividend_sum = %f, capitalization = %f ";
    public static final String SQL_SET_BOND = "SET name = '%s', short_name = '%s', lat_name = '%s', nominal = %f, nominal_value = '%s', issue_date = '%s', " +
            "isin = '%s', issue_size = %d, market_type = '%s', type = '%s', mat_date = '%s', coupon = %f, day_to_redemption = %d ";

    public static final String SQL_DELETE_FROM = "DELETE FROM %s ";
    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS %s ";
    public static final String SQL_USER_ID = "id = %d";
    public static final String SQL_SECURITY_TICKER = "ticker = '%s'";
    public static final String SQL_USER_COLUMNS =  "(id INT NOT NULL AUTO_INCREMENT, " +
                                                    " name VARCHAR(255), " +
                                                    " age INTEGER," +
                                                    "PRIMARY KEY (id))";
    public static final String SQL_STOCK_COLUMNS = "(ticker VARCHAR(255) NOT NULL,  name VARCHAR(255), " +
                                                    "short_name VARCHAR(255),  lat_name VARCHAR(255), " +
                                                    "nominal FLOAT,  nominal_value VARCHAR(255), " +
                                                    "issue_date VARCHAR(255),  isin VARCHAR(255), " +
                                                    "issue_size BIGINT,  market_type VARCHAR(255), " +
                                                    "type VARCHAR(255),  dividend_sum FLOAT, " +
                                                    "capitalization FLOAT,  PRIMARY KEY (ticker));";
    public static final String SQL_BOND_COLUMNS = "(ticker VARCHAR(255), name VARCHAR(255), " +
                                                    "short_name VARCHAR(255), lat_name VARCHAR(255), " +
                                                    "nominal FLOAT, nominal_value VARCHAR(255), " +
                                                    "issue_date VARCHAR(255), isin VARCHAR(255), " +
                                                    "issue_size BIGINT, market_type VARCHAR(255), " +
                                                    "type VARCHAR(255), mat_date VARCHAR(255), " +
                                                    "coupon FLOAT, day_to_redemption INT, " +
                                                    "PRIMARY KEY (ticker))";
    public static final String SQL_USER_VALUES = "VALUES (%d, '%s', %d);";
    public static final String SQL_USER_VALUES_WITHOUT_ID = "(name, age) VALUES ('%s', %d);";
    public static final String SQL_STOCK_VALUES = "VALUES ('%s', '%s', '%s', '%s', %f, " +
                                                    "'%s', '%s', '%s', %d, '%s', '%s', %f, %f);";
    public static final String SQL_BOND_VALUES = "VALUES ('%s', '%s', '%s', '%s', %f, " +
                                                    "'%s', '%s', '%s', %d, '%s', '%s', '%s', %f, %d);";

    public static final String MONGODB_USER = "mongodb_user";
    public static final String MONGODB_PASSWORD = "mongodb_password";
    public static final String MONGODB_DATABASE = "mongodb_database";
    public static final String MONGODB_CONNECT_URI = "mongodb_connect_uri";

    public static final String H2_DRIVER = "h2_driver";
    public static final String JDBC_CONNECTION = "jdbc_connection";

    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_AGE = "age";

    public static final String SECURITY_COLUMN_TICKER = "ticker";
    public static final String SECURITY_COLUMN_NAME = "name";
    public static final String SECURITY_COLUMN_SHORTNAME = "short_name";
    public static final String SECURITY_COLUMN_LATNAME = "lat_name";
    public static final String SECURITY_COLUMN_NOMINAL = "nominal";
    public static final String SECURITY_COLUMN_NOMINALVALUE = "nominal_value";
    public static final String SECURITY_COLUMN_ISSUEDATE = "issue_date";
    public static final String SECURITY_COLUMN_ISIN = "isin";
    public static final String SECURITY_COLUMN_ISSUESIZE = "issue_size";
    public static final String SECURITY_COLUMN_MARKETTYPE = "market_type";

    public static final String STOCK_COLUMN_TYPE = "type";
    public static final String STOCK_COLUMN_DIVIDENDSUM = "dividend_sum";
    public static final String STOCK_COLUMN_CAPITALIZATION = "capitalization";

    public static final String BOND_COLUMN_TYPE = "type";
    public static final String BOND_COLUMN_MATDATE = "mat_date";
    public static final String BOND_COLUMN_COUPON = "coupon";
    public static final String BOND_COLUMN_DAYTOREDEMPTION = "day_to_redemption";

    

    public static final String MONGODB_TIME_FIELD = "time";
    public static final String MONGODB_COMMAND_FIELD = "command";
    public static final String MONGODB_REPOSITORY_FIELD = "repository";
    public static final String MONGODB_CHANGES_FIELD = "changes";


}
