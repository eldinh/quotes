package ru.sfedu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public static final String SECURITY_HISTORY_PATH = "security_history/";



    public static final String USER_TABLE_NAME = "USER";
    public static final String STOCK_TABLE_NAME = "STOCK";
    public static final String BOND_TABLE_NAME = "BOND";
    public static final String ACTON_TABLE_NAME = "ACTION";
    public static final String MARKET_TABLE_NAME = "MARKET";
    public static final String USERS_SECURITY_TABLE_NAME = "USERS_SECURITY";

    public static final String SQL_INSERT = "INSERT INTO %s ";
    public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %s ";
    public static final String SQL_SELECT_FROM = "SELECT * FROM %s ";
    public static final String SQL_WHERE = "WHERE ";
    public static final String SQL_AND = " AND ";
    public static final String SQL_UPDATE = "UPDATE %s ";

    public static final String SQL_SET_USER = "SET name = '%s' ";

    public static final String SQL_SET_STOCK = "SET name = '%s', short_name = '%s', lat_name = '%s', nominal = %f, nominal_value = '%s', issue_date = '%s', " +
                                                    "isin = '%s', issue_size = %d, market_type = '%s', type = '%s', dividend_sum = %f, capitalization = %f ";
    public static final String SQL_SET_BOND = "SET name = '%s', short_name = '%s', lat_name = '%s', nominal = %f, nominal_value = '%s', issue_date = '%s', " +
                                                    "isin = '%s', issue_size = %d, market_type = '%s', type = '%s', mat_date = '%s', coupon = %f, day_to_redemption = %d ";
    public static final String SQL_SET_SECURITY_HISTORY = "SET ticker = '%s', average_per_day = %f, open_price = %f, " +
                                                            "close_price = %f, volume = %d ";

    public static final String SQL_DELETE_FROM = "DELETE FROM %s ";
    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS %s ";
    public static final String SQL_USER_ID = "id = '%s' ";
    public static final String SQL_SECURITY_TICKER = "ticker = '%s' ";
    public static final String SQL_SECURITY_HISTORY_DATE = "date = '%s' ";
    public static final String SQL_MARKET_MARKET_TYPE = "market_type = '%s' ";
    public static final String SQL_ACTION_USER_ID = "user_id = '%s' ";
    public static final String SQL_ACTION_SECURITY = "security = '%s'";

    public static final String SQL_USER_COLUMNS =  "(id VARCHAR(255) NOT NULL, " +
                                                    " name VARCHAR(255), " +
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
                                                    "PRIMARY KEY (ticker));";
    public static final String SQL_MARKET_COLUMNS = "(market_type VARCHAR(255), PRIMARY KEY (market_type));";

    public static final String SQL_ACTION_COLUMNS = "(id VARCHAR(255) NOT NULL, date VARCHAR(255), " +
                                                     "action_type VARCHAR(255), user_id VARCHAR(255), " +
                                                     "security VARCHAR(255), PRIMARY KEY (id));";

    public static final String SQL_SECURITY_HISTORY_COLUMNS = "(date VARCHAR(255), ticker VARCHAR(255), " +
                                                                "average_per_day FLOAT, open_price FLOAT, " +
                                                                "close_price FLOAT, volume BIGINT, " +
                                                                "PRIMARY KEY (date));";
    public static final String SQL_USERS_SECURITY_COLUMNS = "(user_id VARCHAR(255), security VARCHAR(255))";

    public static final String SQL_USER_VALUES = "VALUES ('%s', '%s');";
    public static final String SQL_MARKET_VALUES = "VALUES ('%s')";
    public static final String SQL_STOCK_VALUES = "VALUES ('%s', '%s', '%s', '%s', %f, " +
                                                    "'%s', '%s', '%s', %d, '%s', '%s', %f, %f);";
    public static final String SQL_BOND_VALUES = "VALUES ('%s', '%s', '%s', '%s', %f, " +
                                                    "'%s', '%s', '%s', %d, '%s', '%s', '%s', %f, %d);";
    public static final String SQL_SECURITY_HISTORY_VALUES = "VALUES ('%s', '%s', %f, " +
                                                                     "%f, %f, %d);";
    public static final String SQL_ACTION_VALUES = "VALUES ('%s', '%s', '%s', '%s', '%s')";
    public static final String SQL_USERS_SECURITY_VALUES = "VALUES ('%s', '%s')";

    public static final String MONGODB_USER = "mongodb_user";
    public static final String MONGODB_PASSWORD = "mongodb_password";
    public static final String MONGODB_DATABASE = "mongodb_database";
    public static final String MONGODB_CONNECT_URI = "mongodb_connect_uri";

    public static final String H2_DRIVER = "h2_driver";
    public static final String JDBC_CONNECTION = "jdbc_connection";

    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_NAME = "name";

    public static final String MARKET_COLUMN_MARKET_TYPE = "market_type";

    public static final String SECURITY_COLUMN_TICKER = "ticker";
    public static final String SECURITY_COLUMN_NAME = "name";
    public static final String SECURITY_COLUMN_SHORTNAME = "short_name";
    public static final String SECURITY_COLUMN_LATNAME = "lat_name";
    public static final String SECURITY_COLUMN_NOMINAL = "nominal";
    public static final String SECURITY_COLUMN_NOMINALVALUE = "nominal_value";
    public static final String SECURITY_COLUMN_ISSUEDATE = "issue_date";
    public static final String SECURITY_COLUMN_ISIN = "isin";
    public static final String SECURITY_COLUMN_ISSUESIZE = "issue_size";
    public static final String SECURITY_COLUMN_MARKET_TYPE = "market_type";

    public static final String STOCK_COLUMN_TYPE = "type";
    public static final String STOCK_COLUMN_DIVIDENDSUM = "dividend_sum";
    public static final String STOCK_COLUMN_CAPITALIZATION = "capitalization";

    public static final String BOND_COLUMN_TYPE = "type";
    public static final String BOND_COLUMN_MATDATE = "mat_date";
    public static final String BOND_COLUMN_COUPON = "coupon";
    public static final String BOND_COLUMN_DAYTOREDEMPTION = "day_to_redemption";

    public static final String SECURITY_HISTORY_COLUMN_DATE = "date";
    public static final String SECURITY_HISTORY_COLUMN_TICKER = "ticker";
    public static final String SECURITY_HISTORY_COLUMN_AVERAGEPERDAY = "average_per_day";
    public static final String SECURITY_HISTORY_COLUMN_OPENPRICE = "open_price";
    public static final String SECURITY_HISTORY_COLUMN_CLOSEPRICE = "close_price";
    public static final String SECURITY_HISTORY_COLUMN_VOLUME = "volume";

    public static final String ACTION_COLUMN_ID = "id";
    public static final String ACTION_COLUMN_DATE = "date";
    public static final String ACTION_COLUMN_ACTION = "action_type";
    public static final String ACTION_COLUMN_USER_ID = "user_id";
    public static final String ACTION_COLUMN_SECURITY = "security";



    public static final String MONGODB_TIME_FIELD = "time";
    public static final String MONGODB_COMMAND_FIELD = "command";
    public static final String MONGODB_REPOSITORY_FIELD = "repository";
    public static final String MONGODB_CHANGES_FIELD = "changes";
    public static final String MONGODB_HISTORY = "mongodb_history";

    public static final String DATE = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    public static final String CURRENT_TIME = new Date().toString() + System.currentTimeMillis() % 1000;
}
