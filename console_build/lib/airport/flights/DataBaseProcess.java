/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;
//
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.io.FileOutputStream;
import java.io.PrintStream;
//
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
//
import java.util.Map;
import java.util.Properties;
import java.util.Collections;
import java.util.Iterator;

/**
 * <code>DataBaseProcess</code> provides the following capabilities: <UL>
 * <LI>Controlling state of data base server.</LI> <LI>Creating and deleting
 * table.</LI> <LI>Processing simple query setting in task.</LI> </UL>
 * <code>DataBaseProcess</code> uses Apache Derby Database. All of needed
 * database settings are in the property file: <UL> <LI>JDBC driver name.</LI>
 * <LI>URL to connect server database.</LI> <LI>Name of creating table.</LI>
 * </UL> This file also has a following settings: <UL> <LI>Path to the files
 * that contain names and surnames to generate xml-doc.</LI> <LI>Using charset
 * of above files.</LI> <LI>Path to output xml-doc.</LI> </UL>
 */
public class DataBaseProcess {
    //

    /**
     * @param __derby_lib_path
     * @throws IOException usr
     */
    public static void startServer(String __derby_lib_path)
            throws IOException {
        if (null == __derby_lib_path) {
            __derby_lib_path = FConfigProps.getProperty("pl.server_jar_path");
        }
        //
        Process p = Runtime.getRuntime().exec("java -jar " + __derby_lib_path
                + " server start");
    }
    //

    public static void stopServer(String __derby_lib_path)
            throws IOException {
        if (null == __derby_lib_path) {
            __derby_lib_path = FConfigProps.getProperty("pl.server_jar_path");
        }
        //
        Process p = Runtime.getRuntime().exec("java -jar " + __derby_lib_path
                + " server shutdown");
    }
    // 

    /**
     * Connect to database
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Connection loadDB()
            throws ClassNotFoundException,
                   SQLException,
                   IOException,
                   URISyntaxException {
        //
        String db_driver = FConfigProps.getProperty("jdbc.driver");
        String db_url = FConfigProps.getProperty("jdbc.url");
        //
        Connection conn;
        /*
         * Load the Derby driver. When the embedded Driver is used this action
         * start the Derby engine. Catch an error and suggest a CLASSPATH
         * problem
         */
        Class.forName(db_driver);
        conn = DriverManager.getConnection(db_url);
        //
        return (conn);
    }
    //    

    /**
     * Delete table
     *
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public static void deleteTb()
            throws SQLException,
                   IOException,
                   ClassNotFoundException, 
                   URISyntaxException {
        //
        Connection conn = loadDB();
        if (isTbExist(conn)) {
            //
            String tb_name = FConfigProps.getProperty("table.name");
            String delete_tb_query = "DROP TABLE " + tb_name;
            Statement stmnt;
            //
            stmnt = conn.createStatement();
            stmnt.execute(delete_tb_query);
            stmnt.close();
            //
        }
    }
    //

    /**
     * Made a query to return a checked weight of passenger luggage
     *
     * @param __find_id Id of passenger
     * @return
     * @throws Exception
     */
    public static int getCheckedWeightBy(String __find_id) throws Exception {
        String tbName = FConfigProps.getProperty("table.name");
        Connection conn = DataBaseProcess.loadDB();
        String query = "SELECT checked_weight FROM " + tbName
                + " WHERE id = " + __find_id;
        Throwable thr = null;
        //
        try {
            Statement stmnt = conn.createStatement();
            ResultSet q_result;
            //
            q_result = stmnt.executeQuery(query);
            while (q_result.next()) {
                return (q_result.getInt(1));
            }
        }
        catch (SQLException ex) {
            thr = ex;
            for (Throwable t : ex) {
                t.printStackTrace();
            }
        }
        finally {
            if (null != thr) {
                doWriteStackTrace(thr, "debug.txt");
            }
            conn.close();
        }
        //
        return (-1);
    }
    //

    static boolean isTbExist(Connection __conn) throws SQLException {
        //
        String find_tb_nm = FConfigProps.getProperty("table.name");
        boolean b_exist = false;
        DatabaseMetaData db_meta_data = __conn.getMetaData();
        ResultSet meta_data_res = db_meta_data.getTables(null, null,
                null, new String[]{"TABLE"});
        //
        if (null != meta_data_res) {
            String column_nm = "TABLE_NAME";
            String tb_nm;
            while (meta_data_res.next() && !b_exist) {
                tb_nm = meta_data_res.getString(column_nm);
                b_exist = tb_nm.equals(find_tb_nm);
            }
        }
        //
        return (b_exist);
    }
    //

    private static Properties loadProperties(String __config_props_fnm)
            throws IOException, URISyntaxException {
        //
        InputStream is = DataBaseProcess.class.getResourceAsStream(__config_props_fnm);
        InputStreamReader isr = new InputStreamReader(is, "UTF8");
        Properties out_props = new Properties();
        //
        out_props.load(is);
        is.close();
        isr.close();
        //
        return (out_props);
        //
    }
    //   
    final static Properties FConfigProps;

    static {
        Properties temp = null;
        Throwable thr = null;
        try {

            temp = loadProperties("config.properties");
        }
        catch (URISyntaxException ex) {
            thr = ex;
            ex.printStackTrace();
        }
        catch (IOException ex) {
            thr = ex;
            ex.printStackTrace();
        }
        catch (Exception ex) {
            thr = ex;
            ex.printStackTrace();
        }
        finally {
            FConfigProps = temp;
            if (null != thr) {
                doWriteStackTrace(thr, "debug.txt");
            }
        }
    }
    //
    static boolean bDebugExists = false;
    //

    public static void doWriteStackTrace(Throwable __thr, String __df_nm) {
        //
        try {
            FileOutputStream fos = new FileOutputStream(__df_nm, bDebugExists);
            //
            if (!bDebugExists) {
                bDebugExists = true;
            }
            //
            PrintStream ps = new PrintStream(fos);
            //
            if (__thr instanceof SQLException) {
                SQLException temp = (SQLException) __thr;
                for (Throwable t : temp) {
                    t.printStackTrace(ps);
                }
            }
            else {
                __thr.printStackTrace(ps);
            }
            //
            ps.close();
            fos.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        //
    }
    //
}