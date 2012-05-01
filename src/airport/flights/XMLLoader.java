/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;
//
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
//
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
//
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
//
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
//
/**
 *<code>PassengerInfo</code> represents a simple structure to store id of
 * passenger and remains state of passenger luggage offloading.
 * 
 */
class PassengerInfo{
    String Id;
    boolean LuggOutput;
    //
    public PassengerInfo(){
        Id = "-1";
        LuggOutput=false;
    }
}
//
/**
 * <code>XMLLoader</code> loads information from input xml-doc and remains in 
 * <code>HashMap</code> object
 */
public class XMLLoader {    
    public XMLLoader(String __xmlPath){
        FXMLPath = __xmlPath;
        FPassInfoMap = new HashMap<String, PassengerInfo >();
    }
    //
    /**
     * Loads xml-doc and returns map with passenger information
     * @param __unmodif Signs to return unmodifiable or not passenger info map  
     * @return
     * @throws Exception 
     */
    public Map<String , PassengerInfo > loadDoc(boolean __unmodif)
            throws Exception {
        //
        Document doc = getParsedDoc();
        //
        Element root = doc.getDocumentElement();
        NodeList flights = root.getChildNodes();
        //
        for (int i = 0x0; getChildCount(root) > i; i++) {
            Node c_flight = getChild(flights, i);
            processFlight(c_flight);
        }
        //
        if(__unmodif){
        return(Collections.unmodifiableMap(FPassInfoMap));
        }
        return(FPassInfoMap);
    }
    //
    
    private static int getChildCount(Object __parent){
        int child_count;
        Node in_node = (Node)(__parent);
        NodeList child_list = in_node.getChildNodes();
        //
        child_count = child_list.getLength();
        //
        return(child_count);
    }
    //
    
    private static Element getChild(NodeList __childs, int __idx) 
            throws Exception {
        //
        if(__childs.getLength()<__idx){
            throw new Exception("XMLLoader.getChild Input Index "
                    + "is out of NodeList borders");
        }
        //
        Node child = (Node)__childs.item(__idx);
        return((Element)child);
        //
    }
    //
    
    private void processFlight(Node __flight)
            throws Exception {
        //
        NodeList f_passengers = __flight.getChildNodes();
        for(int i=0x0; f_passengers.getLength()>i; i++){
            Element f_pass = getChild(f_passengers, i);
            //
            PassengerInfo cPassInfo = new PassengerInfo();
            String pass_nm = f_pass.getAttribute("name");
            NodeList pass_luggs = f_pass.getChildNodes();
            Element pass_lugg = getChild(pass_luggs, 0x0);
            //
            cPassInfo.Id = pass_lugg.getAttribute("id");
            FPassInfoMap.put(pass_nm, cPassInfo);
        }
        //
    }
    //
    
    private static String getAttrByName(Node __node, String __attrName){
        if(__node instanceof Element){
            Element element = (Element)(__node);
            return(element.getAttribute(__attrName));
        }
        //
        return(null);
    }
    //
    
    private Document getParsedDoc( ) throws ParserConfigurationException,
                                          SAXException,
                                          IOException {         
        //
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        //
        final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
        //
        factory.setIgnoringElementContentWhitespace(true);
        //
        String xsd_path = new File(".").getCanonicalPath() 
                + DataBaseProcess.FConfigProps.getProperty("pl.xsd_path");
        InputStream fis = new FileInputStream(xsd_path);
        InputStreamReader isr = new InputStreamReader(fis, 
                DataBaseProcess.FConfigProps.getProperty("pl.charset"));
        //
        SchemaFactory schema_fact = SchemaFactory.newInstance(W3C_XML_SCHEMA);
        StreamSource ssisr = new StreamSource(isr);
        Schema xsd_schema = schema_fact.newSchema(new Source[] { ssisr });
        //
        factory.setSchema(xsd_schema);
        //
        DocumentBuilder builder = factory.newDocumentBuilder();
        //
        builder.setErrorHandler(new ErrorHandler() {

            public void warning(SAXParseException exception)
                    throws SAXException {
                System.err.println("warning: " + exception);
            }
            //

            public void error(SAXParseException exception)
                    throws SAXException {
                throw new SAXException("error: " + exception);
            }
            //

            public void fatalError(SAXParseException exception)
                    throws SAXException {
                throw new SAXException("fatalerror: " + exception);
            }
        });
        //        
        return(builder.parse(new File(FXMLPath)));
        //
    } 
    
    private Map<String , PassengerInfo > FPassInfoMap;
    private String FXMLPath;
    //
}
//
