/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;

/**
 *
 * @author lya
 */
import java.util.Set;
import java.util.Iterator;
import java.util.StringTokenizer;
//
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
//
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
//

public class ContentPane extends JPanel {
    //
    ContentPane( ){
        //
        BorderLayout m_layout = new BorderLayout();
        
        setLayout(m_layout);
        //
        FReqBarTitle = new JLabel("Passengers");
        FRequestBar = new JPanel();
        FRequestScroll = createPassengerScrollList();
        BoxLayout rb_layout = new BoxLayout(FRequestBar, BoxLayout.Y_AXIS);
        //
        FRequestBar.setLayout(rb_layout);
        FReqBarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        FRequestBar.add(FReqBarTitle);
        FRequestBar.add(FRequestScroll);
        //
        FResponseBar = new JPanel( );
        FName = new JTextArea();
        FName.setColumns(20);
        FSirname = new JTextArea();
        FSirname.setColumns(20);
        JPanel lugg_panel = new JPanel();
        BoxLayout lp_layout = new BoxLayout(lugg_panel, BoxLayout.Y_AXIS);
        lugg_panel.setLayout(lp_layout);
        FCheckWeight = new JTextArea();
        FCheckWeight.setColumns(20);
        FLuggOutputChBox = new JCheckBox();
        FLuggOutputChBox.setText("Багаж ушёл");
        FLuggOutputChBox.setEnabled(false);
        FLuggOutputChBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lugg_panel.add(FCheckWeight);
        FCheckWeight.setEditable(false);
        lugg_panel.add(FLuggOutputChBox);
        //
        FLuggOutputChBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent __ev){
                if(null != FClickPass){
                    FOwner.putoutLuggageTo(FClickPass);
                    FLuggOutputChBox.setEnabled(false);
                }
            }
        });
        //
        BoxLayout respb_layout = new BoxLayout(FResponseBar, BoxLayout.Y_AXIS);
        //
        FResponseBar.setLayout(respb_layout);
        FName.setBorder(createTextFieldBorder("Name", Color.BLACK));
        FName.setEditable(false);
        FName.setAlignmentX(Component.RIGHT_ALIGNMENT);
        FSirname.setBorder(createTextFieldBorder("Sirname", Color.BLACK));
        FSirname.setEditable(false);
        FSirname.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lugg_panel.setBorder(createTextFieldBorder("Luggage Checkweight kg.", Color.BLACK));
        lugg_panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        //
        FResponseBar.add(FName);
        FResponseBar.add(FSirname);
        FResponseBar.add(lugg_panel);     
        //
        //FRequestBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(FRequestBar, BorderLayout.WEST);
        //FResponseBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(FResponseBar, BorderLayout.EAST);
    }
    //
    
    void fillList(Set<String > __pass_set){
        Iterator<String > it = __pass_set.iterator();
        if(!FRequestList.isEmpty())
            FRequestList.clear();
        while(it.hasNext()){
            FRequestList.addElement(it.next());
        }
    }
    //
    
    void updateList( ){
        FRequestScroll.validate();
        FRequestScroll.repaint();
    }
    //
    
    void clearAll(){
        FRequestList.clear();
        FName.setText("");
        FSirname.setText("");
        FCheckWeight.setText("");
        FLuggOutputChBox.setEnabled(false);
        FLuggOutputChBox.setSelected(false);
        //
        updateList( );
        
    }
    
    private void updateRespBar() {
        FResponseBar.validate();
        FResponseBar.repaint();
    }
    
    private JScrollPane createPassengerScrollList( ){
        FRequestList = new DefaultListModel( );
        JList temp_list = new JList(FRequestList);
        JScrollPane req_scroll = new JScrollPane(temp_list);
        //
        temp_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        temp_list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent __ev) {
                if (__ev.getSource() instanceof JList) {
                    //
                    try {
                        //
                        JList ev_src = (JList) (__ev.getSource());
                        int idx = ev_src.getSelectedIndex();
                        if (0x0 <= idx) {
                            //
                            String temp = (String) FRequestList.get(idx);
                            FClickPass = temp;
                            StringTokenizer str_tokenz = new StringTokenizer(temp);
                            int i = 0;
                            while (str_tokenz.hasMoreTokens()) {
                                String word = str_tokenz.nextToken(" ");
                                if (i == 0) {
                                    FName.setText(word);
                                }
                                if (i == 1) {
                                    FSirname.setText(word);
                                }
                                i++;
                            }
                            //
                            boolean ch_temp=false;
                            if(FOwner.isLuggPutOut(FClickPass)) {
                                ch_temp=true;
                            }
                            //
                            FLuggOutputChBox.setEnabled(!ch_temp);
                            FLuggOutputChBox.setSelected(ch_temp);
                            //
                            int ch_w = FOwner.getCheckweightOfLuggage(FClickPass);
                            FCheckWeight.setText(Integer.toString(ch_w));
                            //
                            updateRespBar();
                            //
                        }
                        //
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                                     ex.getLocalizedMessage(),
                                                     "Error",
                                                     JOptionPane.ERROR_MESSAGE);
                    }
                    //
                }
            }
                    
        });
        temp_list.setAlignmentX(Component.LEFT_ALIGNMENT);
        return(req_scroll);
    }
    //
    
    private static Border createTextFieldBorder(String __title, Color __color){
        Border color_border = BorderFactory.createLineBorder(__color);
        TitledBorder tf_border = BorderFactory.createTitledBorder(__title);
        //
        tf_border.setTitleJustification(TitledBorder.LEFT);
        tf_border.setTitlePosition(TitledBorder.DEFAULT_POSITION);
        tf_border.setBorder(color_border);
        //
        return(tf_border);        
    }
    //
    
    private static Border createColorBorder(Color __color) {
        Border color_border = BorderFactory.createLineBorder(__color);
        //
        return (color_border);
    }
    //
    
    void setOwner(DialogFrame __owner){
        if(null == FOwner){
            FOwner = __owner;
        }
    }
    //
    
    private JPanel FRequestBar;
    private JLabel FReqBarTitle;
    private JScrollPane FRequestScroll;
    private DefaultListModel FRequestList;
    
    private JPanel FResponseBar;
    private JTextArea FName;
    private JTextArea FSirname;
    private JTextArea FCheckWeight;
    private JCheckBox FLuggOutputChBox;
    //
    private String FClickPass;
    private DialogFrame FOwner;
    //
}