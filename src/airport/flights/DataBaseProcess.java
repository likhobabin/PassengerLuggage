/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;
//
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
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
 *
 * @author ilya
 */
public class DataBaseProcess {

    public static void main(String[] args) {
        try {
            //
            startServer();
            try {
                //
                DataBaseProcess.generateData();
                DataBaseProcess db_process = new DataBaseProcess( );
                //
                Iterator<String > passIt = db_process.getPassengerInfoMap()
                        .keySet().iterator();
                while(passIt.hasNext()){
                    String pass_path_nm = passIt.next();
                    db_process.getCheckedWeightBy(pass_path_nm);                    
                }
                
                DataBaseProcess.deleteTb();
                //
            }
            catch (SQLException ex) {
                for (Throwable t : ex) {
                    t.printStackTrace();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            catch(URISyntaxException ex){
                ex.printStackTrace();
            }
            catch(ClassNotFoundException ex){
                ex.printStackTrace();
            }
            finally {
                //
                try {
                    stopServer();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                //
            }
            //
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //

    public static void startServer()
            throws IOException {
        String dervy_lib_dir = System.getenv("DERBY_LIB");
        Process p = Runtime.getRuntime().exec("java -jar " + dervy_lib_dir + "/derbyrun.jar "
                + "server start");
    }
    //

    public static void stopServer()
            throws IOException {
        String dervy_lib_dir = System.getenv("DERBY_LIB");
        Process p = Runtime.getRuntime().exec("java -jar " + dervy_lib_dir + "/derbyrun.jar "
                + "server stop");
    }
    // 

    public static Connection loadDB()
            throws ClassNotFoundException,
            SQLException,
            IOException,
            URISyntaxException {
        //
        String db_driver = FConfigProps.getProperty("jdbc.driver");
        String db_url = FConfigProps.getProperty("jdbc.url");
        String db_user = FConfigProps.getProperty("jdbc.username");
        String pwd = FConfigProps.getProperty("jdbc.password");
        //
        Connection conn;
        /*
         * Load the Derby driver. When the embedded Driver is used this action
         * start the Derby engine. Catch an error and suggest a CLASSPATH
         * problem
         */
        Class.forName(db_driver);
        conn = DriverManager.getConnection(db_url, db_user, pwd);
        //
        return (conn);
    }
    //    

    public static void generateData() throws Exception {
        DataBaseProcess.deleteTb();
        DataCreator data_gen = new DataCreator();
        FPassInfoMap = data_gen.createDataBase();
    }
    //

    public static void deleteTb()
            throws SQLException,
            IOException,
            ClassNotFoundException,
            Exception {
        //
        Connection conn = loadDB();
        if (isTbExist(conn)) {
            //
            String db_driver = FConfigProps.getProperty("jdbc.driver");
            String db_url = FConfigProps.getProperty("jdbc.url");
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
    
    Map<String , PassengerInfo > getPassengerInfoMap( ){
        return(Collections.unmodifiableMap(FPassInfoMap));
    }
    //
    
    public int getCheckedWeightBy(String __pass_name) throws Exception {
        if(null == FPassInfoMap){
            throw new Exception("Error DataBaseProcess.getCheckedWeightBy "
                    + " Passenger Info Map is null");
        }
        //
        String find_id = FPassInfoMap.get(__pass_name).Id;
        String tbName = FConfigProps.getProperty("table.name");
        Connection conn = loadDB();
        String query = "SELECT checked_weight FROM " + tbName 
                + " WHERE id = "+find_id;
        //
        try {
            Statement stmnt = conn.createStatement();
            ResultSet q_result;
            //
            q_result = stmnt.executeQuery(query);
            while (q_result.next()) {
                return(q_result.getInt(1));
            }
        }
        catch (SQLException ex) {
            for (Throwable t : ex) {
                t.printStackTrace();
            }
        }
        finally {
            conn.close();
        }
        //
        return(-1);
    }
    //
    
    static boolean isTbExist(Connection __conn) throws SQLException {
        //
        String find_schema_nm = FConfigProps.getProperty("username");
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
        URL prop_url = DataBaseProcess.class.getResource(__config_props_fnm);
        FileInputStream fis = new FileInputStream(new File(prop_url.toURI()));
        InputStreamReader is = new InputStreamReader(fis, "UTF8");
        Properties out_props = new Properties();
        //
        out_props.load(is);
        is.close();
        fis.close();
        //
        return (out_props);
        //
    }
    //   
    
    private static Map<String, PassengerInfo> FPassInfoMap;
    //
    final static Properties FConfigProps;
    static 
    {
        Properties temp=null;
        try {
            temp = loadProperties("config.properties");
        }
        catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally 
        {
         
            FConfigProps = temp;   
        }
    }
}
