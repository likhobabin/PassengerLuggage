package test;
//
import airport.flights.DialogFrame;
//
import javax.swing.JOptionPane;
//
public class Test {

    /**
     * @param args the command line arguments
     */
   public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            //
            String ex_msg=null;
            public void run() {
                DialogFrame dlgFrame = null;
                Throwable thr=null;
                //
                try {
                    //
                    dlgFrame = new DialogFrame("Airport");
                    //
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    thr=ex;
                    ex_msg = ex.getLocalizedMessage();
                }                
                //
                if (null != ex_msg) {
                    if (DialogFrame.MAXERRMSG >= ex_msg.length()) {
                        JOptionPane.showMessageDialog(null,
                                ex_msg,
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
                    if (null != thr) {
                        DialogFrame.doWriteStackTrace(thr, "debug.txt");
                    }
                    //
                }
            }
            //
        });
    }
    //
}
