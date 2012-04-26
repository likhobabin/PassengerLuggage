/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;

/**
 *
 * @author Ilya
 */
import java.io.*;
//
import java.net.URISyntaxException;
import java.net.URL;
//
import java.util.Random;
//
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
//
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
//
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXParseException;
//

public class XMLCreator {
    //

    public static void main(String[] args) {
        try {
            //
            String data_dir = new File(".").getCanonicalPath()
                    + System.getProperty("file.separator") + "data"
                    + System.getProperty("file.separator");
            String in_names_path = data_dir + "in" + System.getProperty("file.separator")
                    + "names.txt";
            String in_sirnames_path = data_dir + "in" + System.getProperty("file.separator")
                    + "sirnames.txt";
            String outxml_fpath = data_dir + "out" + System.getProperty("file.separator")
                    + "flights.xml";
            XMLCreator xml_creator = new XMLCreator(in_names_path, in_sirnames_path, "UTF8");
            //
            xml_creator.wrXMLTree(outxml_fpath);
            //
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        } 
        catch (URISyntaxException ex) {
            ex.printStackTrace();
        } 
        catch(ParserConfigurationException ex){
            ex.printStackTrace();
        } 
        catch(TransformerConfigurationException ex) {
            ex.printStackTrace();
        } 
        catch(TransformerException ex) {
            ex.printStackTrace();
        }
        catch (Throwable ex) {
            ex.printStackTrace();
        } 
    }
    //
    
    public XMLCreator(String __names_path, 
                      String __sirname_path, 
                      String __charSet) 
            throws IOException, URISyntaxException {
        FCharSet = __charSet;
        //
        FNames = new ProperNameExtract( );
        FNames.extract(__names_path, FCharSet);
        
        FSirnames = new ProperNameExtract( );
        FSirnames.extract(__sirname_path, FCharSet);
    }
    //
    
    public String getCharSet( ){
        return(FCharSet);
    }
    //
    
    public void wrXMLTree(String __file_path) 
            throws IOException, URISyntaxException, 
                   ParserConfigurationException,
                   TransformerConfigurationException,
                   TransformerException {
        File wrf_path = new File(__file_path);
        FileOutputStream wrf_os = new FileOutputStream(wrf_path);
        Writer wrf = new OutputStreamWriter(wrf_os, getCharSet( ));
        String xmlString = XMLCreator.xmlToString(createXMLTree());
        //
        wrf.write(xmlString);        
        wrf.close(); 
    }
    //
    
    static private String xmlToString(Document __doc) 
            throws NullPointerException, TransformerConfigurationException,
                   TransformerException {
        //
        if(null == __doc){
            throw new NullPointerException("Error XMLCreator.writeXML __doc is null");
        }
        //
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer trans = transFactory.newTransformer();
        //
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        //
        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(__doc);
        trans.transform(source, result);
        String xmlString = sw.toString();
        //
        return(xmlString);
        //
    }
    //
        
    private Document createXMLTree( ) throws ParserConfigurationException {
        //
        Random f_num_rand = new Random();
        Random f_pass_count_rand = new Random();
        int maxFlightNum = 1000;
        int pathNamesSz=0x0;
        //
        if(FNames.getProperNameList().size()
                >= FSirnames.getProperNameList().size()){
            pathNamesSz = FSirnames.getProperNameList().size();
        }
        //
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        //
        Element rootEl = doc.createElement("flights");
        doc.appendChild(rootEl);
        //
        rootEl.setAttribute("xmlns:xsi", 
                "http://www.w3.org/20x00x01/XMLSchema-instance");
        rootEl.setAttribute("xsi:noNamespaceSchemaLocation", "flights.xsd");
        //
        int count = pathNamesSz;
        int cFlightHasPass=-1;
        int id=0x0;
        //
        while (0x1<count && 0x0 <= (count-=cFlightHasPass=1+f_pass_count_rand.nextInt(count-1))) {
            //
            int cFlightNumber = f_num_rand.nextInt(maxFlightNum);
            Element cFlight = doc.createElement("flight");
            cFlight.setAttribute("number", Integer.toString(cFlightNumber));
            //
            for (int i=0x0; cFlightHasPass > i; i++, id++) {
                cFlight.appendChild((Node) (createPassenger(id, doc)));
            }
            rootEl.appendChild((Node) cFlight);            
        }
        //
        return(doc);        
        //
    }
    //
    
    private Element createPassenger(int __id, Document doc) {
        //
        Element passenger = doc.createElement("passenger"); 
        Random pass_nm_rand = new Random( );
        Random pass_sir_rand = new Random( );
        int nm_idx = pass_nm_rand.nextInt(FNames.getProperNameList().size());
        int sir_idx = pass_sir_rand.nextInt(FSirnames.getProperNameList().size());
        String pathname = FNames.getProperNameList().get(nm_idx) 
                + " " + FSirnames.getProperNameList().get(sir_idx);
        //
        passenger.setAttribute("name", pathname);
        //
        Element pass_luggage = doc.createElement("luggage");
        //
        pass_luggage.setAttribute("id", Integer.toString(__id));
        passenger.appendChild((Node)pass_luggage);
        //
        return(passenger);        
        //
    }
    //
    
    private String FCharSet;
    private ProperNameExtract FNames;
    private ProperNameExtract FSirnames;
    //
}
