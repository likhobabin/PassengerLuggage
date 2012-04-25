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
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
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
import java.util.Random;
import java.util.Map;
import java.util.Properties;
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
                DataBaseProcess db_process 
                        = new DataBaseProcess("config.properties");
                //
                db_process.deleteTb();
                //
                db_process.generateSrc();
                //
                db_process.loadTable();
                db_process.deleteTb();
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
    //
    
    public static Properties loadProperties(String __config_props_fnm) 
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
        return(out_props);
        //
    }
    //    
    
    public static Connection loadDB( ) 
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
        return(conn);
    }
    //    
    
    public DataBaseProcess(String __config_props_fnm) throws Exception{
        FConfigProps = loadProperties(__config_props_fnm);
    }
    //
    
    public void generateSrc( ) throws Exception {
        SrcCreator src_gen = new SrcCreator();
        src_gen.createSource();
    }
    //
    
    public void loadTable()
            throws SQLException,
            IOException,
            ClassNotFoundException,
            Exception {
        String tbName = FConfigProps.getProperty("table.name");
        Connection conn = loadDB();
        String query = FLoadIdsQuery + tbName;
        try
        {
            Statement stmnt = conn.createStatement();
            ResultSet q_result;
            q_result = stmnt.executeQuery(query);
            System.out.println("Debug DataBaseProcess.loadTable Passenger Ids: \n\t");
            while(q_result.next()){
                int id = q_result.getInt(1);
                System.out.println(Integer.toString(id) + "\n\t");
            }            
        }
        catch (SQLException ex) {
            for (Throwable t : ex) {
                t.printStackTrace();
            }
        }
        finally{
            conn.close();
        }
    }
    //
    
    void deleteTb()
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
        
    private boolean isTbExist(Connection __conn) throws SQLException {
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
    
    class SrcCreator
    {
        SrcCreator( ) throws Exception {
            initXMLCreator();
        }
        //
        
        void createSource( ) throws Exception {
            String f_xml_path = 
                    DataBaseProcess.FConfigProps.getProperty("pl.xml_path");
            String curr_dir_path = new File(".").getCanonicalPath();
            //
            FXmlCreator.wrXMLTree(curr_dir_path+f_xml_path);
            XMLLoader xml_loader = new XMLLoader(curr_dir_path+f_xml_path);
            creatTable(xml_loader);
        }
        //
        
        private void creatTable(XMLLoader __xml_loader)
                throws Exception {
            //
            Connection conn = DataBaseProcess.loadDB();
            String db_driver = 
                    DataBaseProcess.FConfigProps.getProperty("jdbc.driver");
            String db_url = 
                    DataBaseProcess.FConfigProps.getProperty("jdbc.url");
            String tb_name = 
                    DataBaseProcess.FConfigProps.getProperty("table.name");
            String create_tb_query = "CREATE TABLE " + tb_name
                    + " (id INTEGER NOT NULL PRIMARY KEY, "
                    + "  luggage INTEGER, "
                    + "  hand_lugg INTEGER )";
            Random lugg_rand = new Random();
            //
            try {
                //
                if (!isTbExist(conn)) {
                    //
                    Statement stmnt;
                    stmnt = conn.createStatement();
                    stmnt.execute(create_tb_query);
                    //
                    Map<String, PassengerInfo> xml_data = __xml_loader.loadDoc();
                    Iterator<String> keyIt = xml_data.keySet().iterator();
                    String insert_tb_query = "INSERT INTO " + tb_name + " VALUES ( ";
                    //
                    while (keyIt.hasNext()) {
                        String key = keyIt.next();
                        String id = xml_data.get(key).Id;
                        int lugg = lugg_rand.nextInt(17);
                        int hand_lugg = lugg_rand.nextInt(10);
                        //
                        insert_tb_query += id + ", " + Integer.toString(lugg) + ", "
                                + Integer.toString(hand_lugg) + ")";
                        stmnt.executeUpdate(insert_tb_query);
                    }
                    //
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
        }
        //
        
        private XMLCreator xmlCreator(String f_nms_path,
                              String f_sirnms_path ) throws Exception {
            return((FXmlCreator==null) ? 
                    new XMLCreator(f_nms_path, f_sirnms_path, "UTF8") 
                    : FXmlCreator);
        }
        //
              
        private void initXMLCreator( ) throws Exception{
            String f_names_path = 
                    DataBaseProcess.FConfigProps.getProperty("pl.names_path");
            String f_sirnames_path = 
                    DataBaseProcess.FConfigProps.getProperty("pl.sirnames_path");
            String curr_dir_path = new File(".").getCanonicalPath();
            //
            FXmlCreator = xmlCreator(curr_dir_path + f_names_path,
                                     curr_dir_path + f_sirnames_path);
        }
        //
        
        private XMLCreator FXmlCreator;
        //
    }
    private SrcCreator src_creator;
    private String FLoadIdsQuery = "SELECT Id FROM ";
    private Map<String, PassengerInfo> FPassInfoMap;
    //
    static Properties FConfigProps = new Properties();
}
