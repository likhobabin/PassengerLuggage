package test;
//
import airport.flights.DialogFrame;
import airport.flights.DataBaseProcess;
//
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import javax.swing.JOptionPane;
//
public class Test {

    /**
     * @param args the command line arguments
     */
   public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            //
            public void run() {
                //
                DialogFrame dlgFrame = null;
                //
                Throwable thr = null;
                String msg = null;
                String msg_descr = "Error";
                int msg_type = JOptionPane.ERROR_MESSAGE;
                //
                try {
                    //
                    dlgFrame = new DialogFrame("Airport");
                    //
                }
                catch (SQLException ex) {
                    for (Throwable t : ex) {
                        t.printStackTrace();
                    }
                    thr = ex;
                    msg = ex.getLocalizedMessage();
                } 
                catch (IOException ex) {
                    ex.printStackTrace();
                    thr=ex;
                    msg = ex.getLocalizedMessage();
                }  
                catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                    thr=ex;
                    msg = ex.getLocalizedMessage();
                }  
                catch (URISyntaxException ex) {
                    ex.printStackTrace();
                    thr=ex;
                    msg = ex.getLocalizedMessage();
                }  
                catch(Exception ex){
                    ex.printStackTrace();
                    thr=ex;
                    msg = ex.getLocalizedMessage();                    
                }
                //
                DialogFrame.processException(thr, msg, msg_descr, msg_type);
                //
                }
            //
        });
        //
    }
    //
}