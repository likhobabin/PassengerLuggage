/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;
//
import java.io.File;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
//
import java.io.IOException;
//
import java.util.Random;
import java.util.Map;
/**
 *
 * @author ilya
 */
public class DataBaseProcess {

    public static void main(String[] args) {
        try {
            startServer();
            try {
                //
                String db_url = DataBaseProcess.CreatDb("PassengerLuggage", "dispatcher", "hero");
                String tb_name = "OffPutter";
                String create_tb_query = "CREATE TABLE " + tb_name
                        + " (id INTEGER NOT NULL PRIMARY KEY, "
                        + "  luggage INTEGER, "
                        + "  hand_lugg INTEGER )";
                DataBaseProcess.CreatTable(db_url, create_tb_query, tb_name);
                //
            }
            catch (Throwable ex) {
                DataBaseProcess.errReport(ex);
            }
            finally {
                //
                try {
                    stopServer();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                catch (Throwable ex) {
                    ex.printStackTrace();
                }
                //
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    //
    
    public static void startServer( ) 
            throws IOException {
        String dervy_lib_dir = System.getenv("DERBY_LIB");
        Process p = Runtime.getRuntime().exec("java -jar "+dervy_lib_dir+"/derbyrun.jar "
                + "server start");
    }
    //
    public static void stopServer( ) 
            throws IOException {
        String dervy_lib_dir = System.getenv("DERBY_LIB");
        Process p = Runtime.getRuntime().exec("java -jar "+dervy_lib_dir+"/derbyrun.jar "
                + "server stop");
    }
    
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
        conn = DriverManager.getConnection(fullDbURL, "app", "app");
        //
        return (fullDbURL);
    }
    //
    
    public static void CreatTable(String __dbURL, String __create_tb_query,
            String __tb_name)
            throws Throwable {
        Connection conn;
        Random lugg_rand = new Random();
        String data_dir = new File(".").getCanonicalPath()
                + System.getProperty("file.separator") + "data"
                + System.getProperty("file.separator");
        String input_xml_fp = data_dir + "out" + System.getProperty("file.separator")
                + "flights.xml";
        //
        Class.forName(DRIVER);
        conn = DriverManager.getConnection(__dbURL);
        Statement stmnt;
        stmnt = conn.createStatement();
        stmnt.execute(__create_tb_query);
        //
        XMLLoader xml_loader = new XMLLoader(input_xml_fp);
        Map xml_result = xml_loader.loadDoc();
        String insert_tb_query = "INSERT INTO "+__tb_name+" VALUES( ";
        //
        for(int i=0; xml_result.size()>i; i++){
            int lugg = lugg_rand.nextInt(10);
            int hand_lugg = lugg_rand.nextInt(10);
            insert_tb_query+=Integer.toString(lugg) +", "
                    +Integer.toString(hand_lugg) + ");";
        }
        //
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
