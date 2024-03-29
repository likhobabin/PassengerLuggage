package airport.flights;
//
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
//
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.Set;
//
import java.io.File;
import java.io.IOException;
//
import java.net.URISyntaxException;
import java.net.URL;
//
import java.sql.SQLException;
//
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
//
import javax.imageio.ImageIO;
//
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
//
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//
/**
 * <code>DialogFrame</code> is a simple dialog representing
 * all of necessary tasks: starting/stopping database server, 
 * generating a xml-doc, creating/deleting database table and 
 * processing passenger data.
 */

public class DialogFrame extends JFrame implements ActionListener {
    //

    public DialogFrame(String __title) throws SQLException,
                                              IOException,
                                              ClassNotFoundException,
                                              URISyntaxException {
        super(__title);
        //
        FServerJarPath = "";
        bServerStarted = false;
        bXmlGenerated = false;
        bTableExist = false;
        FDataCreator = new DataCreator();
        FDbProcess = new DataBaseProcess();
        //          
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //
        setIconImage(createIconImage("airport.png"));
        setJMenuBar(createMenuBar());
        //
        getContentPane().setLayout(new BorderLayout());
        FContentPane = new ContentPane();
        add(FContentPane, BorderLayout.CENTER);
        //
        pack();
        //
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension screenSz = toolKit.getScreenSize();
        Dimension frameSz = getPreferredSize();
        Dimension frLoc = new Dimension(
                (int) (screenSz.getWidth() / 2 - frameSz.getWidth() / 2),
                (int) (screenSz.getHeight() / 2 - frameSz.getHeight() / 2));
        //
        addWindowListener(new WindowAdapter() {
            //

            public void windowClosing(WindowEvent __cl_ev) {
                //
                Throwable thr = null;
                String msg = null;
                String msg_descr = "Error";
                int msg_type = JOptionPane.ERROR_MESSAGE;
                //
                try {
                    //
                    stopServer();
                    //
                }
                catch(SQLException ex){
                    thr = ex;
                }
                catch(IOException ex){
                    thr = ex;
                    
                }
                catch(ClassNotFoundException ex){
                    thr = ex;
                    
                }
                catch(URISyntaxException ex){
                    thr = ex;                    
                }
                catch (Exception ex) {
                    //
                    thr = ex;
                    if (MAXERRMSG >= ex.getLocalizedMessage().length()) {
                        JOptionPane.showMessageDialog(null,
                                ex.getLocalizedMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                                "Error happened: see debug.txt",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);

                    }
                    //
                }
                //
                processException(thr, msg, msg_descr, msg_type);
                //
            }
            //
        });
        //
        setLocation((int) frLoc.getWidth(), (int) frLoc.getHeight());
        setResizable(false);
        setVisible(true);
    }
    //
    
    public void actionPerformed(ActionEvent ev) {
        Object ev_src = ev.getSource();
        Throwable thr = null;
        String msg = null;
        String msg_descr = null;
        int msg_type = JOptionPane.INFORMATION_MESSAGE;
        //
        try {
            //
            if (ev_src instanceof JMenuItem) {
                //
                if (((JMenuItem) ev_src).equals(FStartStopServer)) {
                    //
                    doStartStopServer();
                    //
                } else if (((JMenuItem) ev_src).equals(FExit)) {
                    //
                    postCloseEv();
                    //
                } else if (((JMenuItem) ev_src).equals(FGenerateXml)) {
                    //
                    if (bTableExist) {
                        if (bServerStarted) {
                            //
                            doDeleteTable();
                            //
                        } else {
                            //
                            msg = "Can't generate xml\nStart Server";
                            msg_descr = "Error";
                            msg_type = JOptionPane.ERROR_MESSAGE;
                            //
                            throw new IllegalArgumentException(msg);
                        }
                    }
                    //
                    FDataCreator.generateXML();
                    bXmlGenerated = true;
                    msg = "Succeed in generating XML";
                    msg_descr = "Information";
                    msg_type = JOptionPane.INFORMATION_MESSAGE;
                    //
                } else if (((JMenuItem) ev_src).equals(FCreateDelTb)) {
                    //
                    if (!bXmlGenerated || !bServerStarted) {
                        //
                        msg = "Can't " + FCreateDelTb.getText();
                        //
                        if (!bXmlGenerated) {
                            msg += "\nGenerate Xml doc ";
                        }
                        if (!bServerStarted) {
                            msg += "\nStart server ";
                        }
                        //
                        throw new IllegalArgumentException(msg);
                        //
                    }
                    //
                    if (bTableExist) {
                        doDeleteTable();
                    } else {
                        doCreateTable();
                    }
                }
                //
            }
            //
        }
        catch(ClassNotFoundException ex){
            thr = ex;
        }
        catch(IOException ex){
            thr = ex;
        }
        catch (URISyntaxException ex) {
            thr = ex;
        }
        catch (SAXParseException ex) {
            thr = ex;
        }
        catch (ParserConfigurationException ex) {
            thr = ex;
        }
        catch (SAXException ex) {
            thr = ex;
        }
        catch (TransformerConfigurationException ex) {
            thr = ex;
        }
        catch (TransformerException ex) {
            thr = ex;
        }
        catch (SQLException ex) {
            thr = ex;
        }
        catch (IllegalArgumentException ex) {
            thr = ex;
        }
        //
        processException(thr, msg, msg_descr, msg_type);
        //
    }
    //

    void stopServer() throws SQLException, 
                             IOException, 
                             ClassNotFoundException, 
                             URISyntaxException {
        //Stopping database server 
        if (bServerStarted) {
            DataBaseProcess.deleteTb();
            DataBaseProcess.stopServer(FServerJarPath);
        }
    }
    //

    void postCloseEv() {
        //Create event 
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        //
    }
    //
    
    void putoutLuggageTo(String __pathname){
        if(!FPassengerInfo.isEmpty() && FPassengerInfo.containsKey(__pathname)){
            FPassengerInfo.get(__pathname).LuggOutput = true;
        }
    }
    //
    
    boolean isLuggPutOut(String __pathname){
        if(!FPassengerInfo.isEmpty() && FPassengerInfo.containsKey(__pathname)){
            return(FPassengerInfo.get(__pathname).LuggOutput);
        }
        return(false);
    }
    //
    
    String getFlightNumber(String __pathname) throws Exception {
        if(!FPassengerInfo.isEmpty() && FPassengerInfo.containsKey(__pathname)){
            return(FPassengerInfo.get(__pathname).FlightNum);
        }
        return(null);
    }
    
    int getCheckedweightOfLuggage(String __pathname) throws Exception {
        int ch_weight = -1;
        String find_id = null;
        //
        if (bServerStarted && bTableExist) {
            if (!FPassengerInfo.isEmpty() && FPassengerInfo.containsKey(__pathname)) {
                find_id = FPassengerInfo.get(__pathname).Id;
            }
            ch_weight = DataBaseProcess.getCheckedWeightBy(find_id);
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Can't get checked-weight of luggage\nStart Server",
                    "Warrning",
                    JOptionPane.WARNING_MESSAGE);
        }
        //
        return (ch_weight);
    }
    //

    private JMenuBar createMenuBar() {
        //
        JMenuBar menu_bar = new JMenuBar();
        JMenu menu = new JMenu("Hand Wheel");
        FStartStopServer = new JMenuItem("Start Server");
        FStartStopServer.setSelected(false);
        FStartStopServer.addActionListener(this);
        FGenerateXml = new JMenuItem("Generate xml-doc");
        FGenerateXml.addActionListener(this);
        FCreateDelTb = new JMenuItem("Create Table");
        FCreateDelTb.addActionListener(this);
        FExit = new JMenuItem("Exit");
        FExit.addActionListener(this);
        //
        menu.add(FStartStopServer);
        menu.addSeparator();
        menu.add(FGenerateXml);
        menu.add(FCreateDelTb);
        menu.addSeparator();
        menu.addSeparator();
        menu.add(FExit);
        //
        menu_bar.add(menu);
        //
        return (menu_bar);
        //
    }
    //

    private static Image createIconImage(String path) {
        java.net.URL imgURL = DialogFrame.class.getResource(path);
        if (imgURL != null) {
            return (new ImageIcon(imgURL).getImage());
        } else {
            return null;
        }
    }
    //

    private void doStartStopServer() throws SQLException,
                                            ClassNotFoundException {
        //
        String msg = null;
        String msg_descr = null;
        int msg_type = JOptionPane.INFORMATION_MESSAGE;
        Pattern find_jar = Pattern.compile("derbyrun\\.jar$");
        Throwable thr=null;
        try {
            //
            if (bServerStarted) {

                String jar_file_path = FServerJarPath;
                //
                DataBaseProcess.stopServer(jar_file_path);
                //
                msg = "Succeed in stoping server";
                msg_descr = "Information";
                msg_type = JOptionPane.INFORMATION_MESSAGE;
                //
                FStartStopServer.setText("Start Server");
                FFileCh.setSelectedFile(null);
                bServerStarted = false;
            } else {
                Matcher matcher = find_jar.matcher(FServerJarPath);
                if (!matcher.find()) {

                    if (null == FFileCh) {

                        FFileCh = new JFileChooser();
                        JarFilter jar_filer = new JarFilter();
                        FFileCh.addChoosableFileFilter(jar_filer);
                        FFileCh.setFileFilter(jar_filer);
                        FFileCh.setAcceptAllFileFilterUsed(false);

                    }
                    //
                    //Show it.
                    FFileCh.setDialogTitle("derbyrun.jar searching...");
                    int returnVal = FFileCh.showOpenDialog(this);
                    String jar_file_path;
                    //Process the results.
                    if (returnVal == JFileChooser.APPROVE_OPTION) {

                        File jar_file = FFileCh.getSelectedFile();
                        jar_file_path = jar_file.getPath();
                        //
                        matcher = find_jar.matcher(jar_file_path);
                        if (matcher.find()) {
                            FServerJarPath = jar_file_path;
                        } else {
                            msg_type = JOptionPane.ERROR_MESSAGE;
                            msg_descr = "Warrning";
                            //
                            FFileCh.setSelectedFile(null);
                            throw new IllegalArgumentException("Can't start/stop server "
                                                + "\nIncorrect .jar");
                        }
                    }
                    //
                    if (returnVal == JFileChooser.CANCEL_OPTION) {
                        msg_descr = "Warrning";
                        msg_type = JOptionPane.WARNING_MESSAGE;
                        throw new IllegalArgumentException("Have been chosen a cancel btn");
                    }
                }
                //
                DataBaseProcess.startServer(FServerJarPath);
                bServerStarted = true;
                //
                msg = "Succeed in Starting Server";
                msg_type = JOptionPane.INFORMATION_MESSAGE;
                msg_descr = "Information";
                //
                FStartStopServer.setText("Stop Server");
            }
        }
        catch (IOException ex) {
            thr = ex;
            //
            msg = "Error " + ex.getLocalizedMessage();
            msg_type = JOptionPane.ERROR_MESSAGE;
            msg_descr = "Error";
            FFileCh.setSelectedFile(null);
            //
        }
        catch(IllegalArgumentException ex){
            thr = ex;
            FFileCh.setSelectedFile(null);
            msg = "Error " + ex.getMessage();
        }
        //
        processException(thr, msg, msg_descr, msg_type);
        //
    }
    //

    private void doDeleteTable() 
            throws SQLException,
            IOException,
            ClassNotFoundException, 
            URISyntaxException {
        //
        DataBaseProcess.deleteTb();
        bTableExist = false;
        FCreateDelTb.setText("Create Table");
        //
        if (!FPassengerInfo.isEmpty()) {
            FPassengerInfo.clear();
        }
        //
        FContentPane.clearAll();
        pack();
        //
    }
    //

    private void doCreateTable() throws IOException, 
                                        ClassNotFoundException, 
                                        SQLException, 
                                        URISyntaxException, 
                                        ParserConfigurationException, 
                                        SAXException {
        FPassengerInfo = FDataCreator.createDataBase();
        Set<String > pass_list = FPassengerInfo.keySet();
        FContentPane.fillList(pass_list);
        FContentPane.setOwner(this);
        updatePassengerList();
        //
        bTableExist = true;
        FCreateDelTb.setText("Delete Table");
    }
    //
    
    private void updatePassengerList( ){
        FContentPane.updateList();
        pack();
    }
    //
    
    public static void processException(Throwable __thr, String __msg, 
                                                   String __msg_descr,
                                                   int __msg_type) {

        //
        if (__thr != null) {
            __msg = __thr.getLocalizedMessage();
            __msg_descr = "Error";
            __msg_type = JOptionPane.ERROR_MESSAGE;
            DataBaseProcess.doWriteStackTrace(__thr, "debug.txt");
        }
        //
        if (__msg != null) {
            if (MAXERRMSG >= __msg.length()) {
                JOptionPane.showMessageDialog(null,
                        __msg,
                        __msg_descr,
                        __msg_type);
            }
            else if (JOptionPane.ERROR_MESSAGE == __msg_type) {
                JOptionPane.showMessageDialog(null,
                        "Error happened: see debug.txt",
                        __msg_descr,
                        __msg_type);

            }
        }
    }
    //
    
    public static final int MAXERRMSG=256;
    //
    
    private String FServerJarPath;
    private JMenuItem FStartStopServer;
    private JMenuItem FGenerateXml;
    private JMenuItem FCreateDelTb;
    private JMenuItem FExit;
    private JFileChooser FFileCh;
    //
    private ContentPane FContentPane;
    //
    private boolean bServerStarted;
    private boolean bXmlGenerated;
    private boolean bTableExist;
    private DataCreator FDataCreator;
    private DataBaseProcess FDbProcess;
    private Map<String, PassengerInfo> FPassengerInfo;
}