/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;

/**
 *
 * @author ilya
 */
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

//
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
//
import java.io.File;
import java.io.IOException;
//
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
//

public class DialogFrame extends JFrame implements ActionListener {
    //
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            //
            public void run() {
                DialogFrame dlgFrame = null;
                //
                try {
                    //
                    dlgFrame = new DialogFrame("Airoport");
                    //
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //
        });
    }
    //
    
    public DialogFrame(String __title) throws Exception {
        super(__title);
        //
        FServerJarPath="";
        bServerStarted = false;
        bXmlGenerated = false;
        FDataCreator = new DataCreator();
        //
        BoxLayout fr_layout = new BoxLayout(getContentPane(),
                BoxLayout.Y_AXIS);
        getContentPane().setLayout(fr_layout);
        //          
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //
        setIconImage(createImageIcon("mf_airoport.png"));
        setJMenuBar(createMenuBar());
        //
        pack();
        //
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension screenSz = toolKit.getScreenSize();
        Dimension frameSz = this.getPreferredSize();
        Dimension frLoc = new Dimension(
                (int)(screenSz.getWidth()/2 - frameSz.getWidth()/2), 
                (int)(screenSz.getHeight()/2 - frameSz.getHeight()/2)
                );  
        //
        addWindowListener(new WindowAdapter() {
            //
            public void windowClosing(WindowEvent __cl_ev) {
                //
                try {
                    //
                    stopServer();
                    //
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    
                }
                //
            }
            //
        });
        //
        setLocation((int)frLoc.getWidth(), (int)frLoc.getHeight());
        setResizable(true);
        setVisible(true);
    }
    //

    public void actionPerformed(ActionEvent ev) {
        Object ev_src = ev.getSource();
        String msg = "";
        String msg_descr = "";
        int msg_type = JOptionPane.INFORMATION_MESSAGE;
        //
        try {
            //
            if (ev_src instanceof JMenuItem) {
                if (((JMenuItem) ev_src).equals(FStartStopServer)) {
                    doStartStopServer();
                }
                else if (((JMenuItem) ev_src).equals(FExit)) {
                    postCloseEv();
                }
                else if (((JMenuItem) ev_src).equals(FGenerateXml)) {
                    //
                    FDataCreator.generateXML();
                    //
                    msg = "Can't generate xml";
                    msg_descr = "Error";
                    msg_type = JOptionPane.ERROR_MESSAGE;
                    //
                }
                else if (((JMenuItem) ev_src).equals(FCreateDb)) {
                    //
                    if (!bXmlGenerated && !bServerStarted) {
                        msg = "Can't Create Data Base";
                        msg_descr = "Error";
                        msg_type = JOptionPane.ERROR_MESSAGE;
                        throw new Exception();
                    }
                    //
                    if(FPassengerInfo!=null){
                        FPassengerInfo.clear();
                    }
                    //
                    if(FCreateDb.getText().equals("Delete Table")){
                        DataBaseProcess.deleteTb();
                        FCreateDb.setText("Create Table");
                    }
                    else
                    {
                    //
                    FPassengerInfo = FDataCreator.createDataBase();
                    FCreateDb.setText("Delete Table");
                    bXmlGenerated = false;
                    //
                    }
                }
                //
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            //
            JOptionPane.showMessageDialog(this,
                    msg,
                    msg_descr,
                    msg_type);
        }
    }
    //
    
    void stopServer( ) throws Exception {
        //Stopping database server 
        if(bServerStarted){
            DataBaseProcess.stopServer(FServerJarPath);
        }
    }
    //
    
    void postCloseEv( ){
        //Create event 
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        //
    }
    
    private JMenuBar createMenuBar() {
        //
        JMenuBar menu_bar = new JMenuBar();
        JMenu menu = new JMenu("Hand Wheel");
        FStartStopServer = new JMenuItem("Start Server");
        FStartStopServer.setSelected(false);
        FStartStopServer.addActionListener(this);
        FGenerateXml = new JMenuItem("Generate xml-doc");
        FGenerateXml.addActionListener(this);
        FCreateDb = new JMenuItem("Create Data Base");
        FCreateDb.addActionListener(this);
        FExit = new JMenuItem("Exit");
        FExit.addActionListener(this);
        //
        menu.add(FStartStopServer);
        menu.addSeparator();
        menu.add(FGenerateXml);
        menu.add(FCreateDb);
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

    private static Image createImageIcon(String path) {
        java.net.URL imgURL = DialogFrame.class.getResource(path);
        if (imgURL != null) {
            return (new ImageIcon(imgURL).getImage());
        }
        else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    //
    
    private void doStartStopServer( ) {
        //
        boolean bShowMsg=true;
        String msg = "";
        String msg_descr = "";
        int msg_type = JOptionPane.INFORMATION_MESSAGE;
        Pattern find_jar = Pattern.compile("derbyrun\\.jar$");
        try {
            //
            if (bServerStarted) {

                String jar_file_path = FServerJarPath;
                //
                DataBaseProcess.stopServer(jar_file_path);
                //
                msg += "Succeed stoped server";
                msg_descr += "Information";
                msg_type = JOptionPane.INFORMATION_MESSAGE;
                //
                FStartStopServer.setText("Start Server");
                FFileCh.setSelectedFile(null);
                bServerStarted = false;
            }
            else {
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
                        }
                        else {
                            msg += "Incorrect .jar";
                            msg_type = JOptionPane.ERROR_MESSAGE;
                            msg_descr += "Warrning";
                            //
                            FFileCh.setSelectedFile(null);
                        }
                    }
                    //
                    if(returnVal == JFileChooser.CANCEL_OPTION){
                        bShowMsg = false;
                    }
                }
                matcher = find_jar.matcher(FServerJarPath);
                if (matcher.find()) {
                    DataBaseProcess.startServer(FServerJarPath);
                    bServerStarted = true;
                    //
                    msg += "Succeed Starting Server";
                    msg_type = JOptionPane.INFORMATION_MESSAGE;
                    msg_descr += "Information";
                    //
                    FStartStopServer.setText("Stop Server");
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            msg += "Can't start/stop server";
            msg_type = JOptionPane.ERROR_MESSAGE;
            msg_descr += "Warrning";
            FFileCh.setSelectedFile(null);
        }
        //
        if (bShowMsg) {
            JOptionPane.showMessageDialog(this,
                    msg,
                    msg_descr,
                    msg_type);
        }
        //
    }
    //
    
    private String FServerJarPath;
    private JMenuItem FStartStopServer;
    private JMenuItem FGenerateXml;
    private JMenuItem FCreateDb;
    private JMenuItem FExit;
    private JFileChooser FFileCh;    
    //
    
    private boolean bServerStarted;
    private boolean bXmlGenerated;
    private DataCreator FDataCreator;
    private DataBaseProcess FDbProcess;
    private Map<String , PassengerInfo > FPassengerInfo;
    //
}
