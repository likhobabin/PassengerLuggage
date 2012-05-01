/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;

import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.StringWriter;
//
import java.net.URISyntaxException;
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
//

/**
 * A <code>XMLCreator</code> represent a generator of xml-docs 
 * of desired format that is reported by task.  
 */
public class XMLCreator {
    //
    /**
     * <code>XMLCreator</code> uses a <code>ProperNameExtract</code> objects
     * to extract string list of proper name words
     * @param __names_path Path to file with names
     * @param __surname_path Path to file with surname
     * @param __charSet Charset of input files 
     * @throws IOException
     * @throws URISyntaxException 
     */
    public XMLCreator(String __names_path, 
                      String __surname_path, 
                      String __charSet) 
            throws IOException, URISyntaxException {
        FCharSet = __charSet;
        //
        FNames = new ProperNameExtract( );
        FNames.extract(__names_path, FCharSet);
        
        FSirnames = new ProperNameExtract( );
        FSirnames.extract(__surname_path, FCharSet);
    }
    //
    
    public String getCharSet( ){
        return(FCharSet);
    }
    //
    /**
     * 
     * @param __file_path Path to writing file 
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
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
