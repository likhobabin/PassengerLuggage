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
import java.sql.SQLException;
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
        FServerJarPath = "";
        bServerStarted = false;
        bXmlGenerated = false;
        bTableExist = false;
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
                (int) (screenSz.getWidth() / 2 - frameSz.getWidth() / 2),
                (int) (screenSz.getHeight() / 2 - frameSz.getHeight() / 2));
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
        setLocation((int) frLoc.getWidth(), (int) frLoc.getHeight());
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
                            bTableExist = false;
                            DataBaseProcess.deleteTb();
                            FCreateDelTb.setText("Create Table");
                            //
                        } else {
                            //
                            msg = "Can't generate xml\nStart Server";
                            msg_descr = "Error";
                            msg_type = JOptionPane.ERROR_MESSAGE;
                            //
                            throw new Exception();
                        }
                    }
                    //
                    FDataCreator.generateXML();
                    bXmlGenerated = true;
                    //
                } else if (((JMenuItem) ev_src).equals(FCreateDelTb)) {
                    //
                    if (!bXmlGenerated || !bServerStarted) {
                        //
                        msg = "Can't " + FCreateDelTb.getText();
                        //
                        if (!bXmlGenerated) {
                            msg += "\n\tGenerate Xml doc ";
                        }
                        if (!bServerStarted) {
                            msg += "\n\tStart server ";
                        }
                        //    
                        msg_descr = "Error";
                        msg_type = JOptionPane.ERROR_MESSAGE;
                        throw new Exception();
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

    void stopServer() throws Exception {
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

    private static Image createImageIcon(String path) {
        java.net.URL imgURL = DialogFrame.class.getResource(path);
        if (imgURL != null) {
            return (new ImageIcon(imgURL).getImage());
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    //

    private void doStartStopServer() throws SQLException,
                                            ClassNotFoundException,
                                            Exception {
        //
        boolean bShowMsg = true;
        String msg = "Can't start/stop server";
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
                            msg += "\nIncorrect .jar";
                            msg_type = JOptionPane.ERROR_MESSAGE;
                            msg_descr += "Warrning";
                            //
                            FFileCh.setSelectedFile(null);
                            throw new Exception();
                        }
                    }
                    //
                    if (returnVal == JFileChooser.CANCEL_OPTION) {
                        bShowMsg = false;
                        throw new Exception("Chose cancel btn");
                    }
                }
                //
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
        catch (IOException ex) {
            ex.printStackTrace();
            //
            msg_type = JOptionPane.ERROR_MESSAGE;
            msg_descr += "Error";
            FFileCh.setSelectedFile(null);
        }
        //
        catch(Exception ex){
            ex.printStackTrace();
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

    private void doDeleteTable() throws Exception {
        DataBaseProcess.deleteTb();
        bTableExist = false;
        FCreateDelTb.setText("Create Table");
        //
        if (!FPassengerInfo.isEmpty()) {
            FPassengerInfo.clear();
        }
    }
    //

    private void doCreateTable() throws Exception {
        FPassengerInfo = FDataCreator.createDataBase();
        bTableExist = true;
        FCreateDelTb.setText("Delete Table");
    }
    //
    private String FServerJarPath;
    private JMenuItem FStartStopServer;
    private JMenuItem FGenerateXml;
    private JMenuItem FCreateDelTb;
    private JMenuItem FExit;
    private JFileChooser FFileCh;
    //
    private boolean bServerStarted;
    private boolean bXmlGenerated;
    private boolean bTableExist;
    private DataCreator FDataCreator;
    private DataBaseProcess FDbProcess;
    private Map<String, PassengerInfo> FPassengerInfo;
    //
}
