/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;
//
import java.io.File;
import java.io.IOException;
//
import java.util.Map;
import java.util.HashMap;
//
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
//
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//
/**
 *
 * @author Ilya
 */
class PassengerInfo{
    int Id;
    boolean LuggOutput;
    //
    public PassengerInfo(){
        Id = -1;
        LuggOutput=false;
    }
}
//

public class XMLLoader {
    public static void main(String[] args) {
        try{
        String data_dir = new File(".").getCanonicalPath()
                + System.getProperty("file.separator") + "data"
                + System.getProperty("file.separator");
        String outxml_pf = data_dir + "out" + System.getProperty("file.separator")
                + "flights.xml";
        XMLLoader xml_loader = new XMLLoader(outxml_pf);
        //
        xml_loader.loadDoc();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        catch(SAXParseException ex){
            ex.printStackTrace();
        }
        catch(Throwable ex){
            ex.printStackTrace();
        }
    }
    //
    
    public XMLLoader(String __xmlPath){
        FXMLPath = __xmlPath;
        FPassInfoMap = new HashMap<>();
    }
    //
    
    public void loadDoc( )
            throws Throwable, SAXParseException {
        //
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //
        factory.setValidating(true);
        //Specifies that the parser produced by this code will provide 
        //support for XML namespaces. 
        //By default the value of this is set to false
        factory.setNamespaceAware(true);
        final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/"
                + "schemaLanguage";
        final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
        factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        factory.setIgnoringElementContentWhitespace(true);
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
        Document doc = builder.parse(new File(FXMLPath));
        Element root = doc.getDocumentElement();
        NodeList flights = root.getChildNodes();
        //
        for (int i = 0; getChildCount(root) > i; i++) {
            Node c_flight = getChild(flights, i);
            processFlight(c_flight);
        }
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
        if(child instanceof Element && null != (Element)child){
            System.out.println("Karaylll!!!");
        }
        return((Element)child);
        //
    }
    //
    private void processFlight(Node __flight)
            throws Exception {
        System.out.println("Flight number: " + 
                getAttrByName(__flight, "number"));
        //
        NodeList f_passengers = __flight.getChildNodes();
        for(int i=0; f_passengers.getLength()>i; i++){
            Element f_pass = getChild(f_passengers, i);
            //
            PassengerInfo cPassInfo = new PassengerInfo();
            String pass_nm = f_pass.getAttribute("name");
            //
            System.out.println("\n\tPassenger name: " +
                    pass_nm);
            //
            NodeList pass_luggs = f_pass.getChildNodes();
            Element pass_lugg = getChild(pass_luggs, 0x0);
            String id = pass_lugg.getAttribute("id");
            if(null!= id){
                System.out.println("\nid karayl");
            }
            cPassInfo.Id = Integer.parseInt(id);
            //
            System.out.println("\n\tPassenger lugg_id: " +
                    cPassInfo.Id);
            //
            FPassInfoMap.put(pass_nm, cPassInfo);
        }
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
    private Map<String , PassengerInfo > FPassInfoMap;
    private String FXMLPath;
}
