/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author ilya
 */
class DataCreator {

    DataCreator() throws Exception {
        String f_names_path =
                DataBaseProcess.FConfigProps.getProperty("pl.names_path");
        String f_sirnames_path =
                DataBaseProcess.FConfigProps.getProperty("pl.sirnames_path");
        String curr_dir_path = new File(".").getCanonicalPath();
        //
        FXmlCreator = getXmlCreator(curr_dir_path + f_names_path,
                                    curr_dir_path + f_sirnames_path);
    }
    //
    
    void generateXML() throws Exception {
        String f_xml_path =
                DataBaseProcess.FConfigProps.getProperty("pl.xml_path");
        String curr_dir_path = new File(".").getCanonicalPath();
        //
        FXmlCreator.wrXMLTree(curr_dir_path + f_xml_path);
        //
    }

    Map<String , PassengerInfo > createDataBase() throws Exception {
        String f_xml_path =
                DataBaseProcess.FConfigProps.getProperty("pl.xml_path");
        String curr_dir_path = new File(".").getCanonicalPath();
        //
        XMLLoader xml_loader = new XMLLoader(curr_dir_path + f_xml_path);
        //
        return(creatTable(xml_loader));
    }
    //
    
    private Map<String , PassengerInfo > creatTable(XMLLoader __xml_loader)
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
                + " ( id INTEGER NOT NULL PRIMARY KEY, "
                + "   checked_weight INTEGER )";
        Random weight_rand = new Random();
        Map<String , PassengerInfo > xml_data_map=null;
        //
        try {
            //
            if (!DataBaseProcess.isTbExist(conn)) {
                //
                Statement stmnt;
                stmnt = conn.createStatement();
                stmnt.execute(create_tb_query);
                //
                xml_data_map = __xml_loader.loadDoc();
                Iterator<String> keyIt = xml_data_map.keySet().iterator();
                //
                while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    String id = xml_data_map.get(key).Id;
                    int checked_weight = weight_rand.nextInt(30);
                    //
                    String insert_tb_query = "INSERT INTO " + tb_name  
                            + " VALUES ("+ id + ", "
                            + checked_weight + ")";
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
        //
        return(xml_data_map);
    }
    //

    private XMLCreator getXmlCreator(String f_nms_path,
            String f_sirnms_path) throws Exception {
        String char_set =
                DataBaseProcess.FConfigProps.getProperty("pl.charset");
        return ((FXmlCreator == null)
                ? new XMLCreator(f_nms_path, f_sirnms_path, char_set)
                : FXmlCreator);
    }
    //
    
    private XMLCreator FXmlCreator;
    //
}