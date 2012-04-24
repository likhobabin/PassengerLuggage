/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;
//
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
/**
 *
 * @author ilya
 */
public class DataBaseProcess {    
    public static void main(String[] args) {
        try {
            String db_url = DataBaseProcess.CreatDb("PassengerLuggage", "dispatcher", "hero");
            String tb_name = "OffPutter";
            String create_tb_query = "CREATE TABLE " + tb_name
                    + " (id INTEGER NOT NULL PRIMARY KEY, "
                    + "  luggage INTEGER, "
                    + "  hand_lugg INTEGER )";
            DataBaseProcess.CreatTable(db_url, create_tb_query);
            //
            
        } catch (Throwable ex) {
            DataBaseProcess.errReport(ex);
        }
    }
    //

    public static String CreatDb(String __title, String __userName,
            String __userPwd) throws Throwable {
        String fullDbURL = DB_URL + __title + ";create=true;user=" + __userName
                + ";password=" + __userPwd;
        Connection conn;
        /*
         * Load the Derby driver. When the embedded Driver is used this action
         * start the Derby engine. Catch an error and suggest a CLASSPATH
         * problem
         */
        Class.forName(DRIVER);
        conn = DriverManager.getConnection(fullDbURL);
        //
        return (fullDbURL);
    }
    //
    
    public static void CreatTable(String __dbURL, String __create_tb_query)
            throws Throwable {
        Connection conn;
        Class.forName(DRIVER);
        conn = DriverManager.getConnection(__dbURL);
        Statement stmnt;
        stmnt = conn.createStatement();
        stmnt.execute(__create_tb_query);
        stmnt.close();
    }
    //
    
    public static void errReport(Throwable ex) {
        if (ex instanceof SQLException) {
            SQLException sql_ex = (SQLException) ex;
            System.out.println("\n\tState: " + sql_ex.getSQLState());
            System.out.println("\n\tMessage: " + sql_ex.getMessage());
        } else {
            System.out.println("Not sql Exception ");
            ex.printStackTrace();
        }
    }
    //
    
    public static final String DB_URL="jdbc:derby://localhost:1527/";
    public static final String DRIVER = "org.apache.derby.jdbc.ClientDriver";
}
