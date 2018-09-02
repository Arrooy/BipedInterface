package Vistas;

import Controladores.ExitManager;
import Controladores.Main;
import Controladores.SettingsController;
import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

public class SerialSettings extends JFrame {

    private final Color DISCONNECTED = new Color(200, 51, 78);
    private final Color CONNECTED = new Color(54, 255, 102);

    private LinkedList<JTextField> fields;
    private LinkedList<JComboBox> comboBoxes;

    private JButton update;
    private JButton submit;
    private JButton test;

    private JTextField testData;
    private JLabel connectionIndicator;

    public SerialSettings(int locationX, int locationY){

        setTitle("Bot_SerialSettings " + Main.VERSION);
        setResizable(false);

        setLocation(locationX,locationY);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addExitManagement();

        fields = new LinkedList<>();
        comboBoxes = new LinkedList<>();

        update = new JButton("Update ports");
        update.setActionCommand("Update");

        submit = new JButton("Connect");
        submit.setActionCommand("Submit");

        test = new JButton("Send Raw text");
        test.setActionCommand("Test");

        testData = new JTextField();

        getContentPane().add(createContent());
        setSize(new Dimension(351,258));
    }

    private JPanel createContent() {

        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base,BoxLayout.Y_AXIS));

        JPanel fs = new JPanel(new BorderLayout());
        connectionIndicator = new JLabel(" ");
        connectionIndicator.setOpaque(true);
        connectionIndicator.setBackground(DISCONNECTED);

        fs.add(connectionIndicator,BorderLayout.CENTER);
        base.add(fs);


        String[] options = {"COM Port","BaudRate","TimeOuts","ReadTimeOut","WriteTimeOut"};
        for(String opt : options){
            JPanel aux = new JPanel();
            aux.setLayout(new BoxLayout(aux,BoxLayout.X_AXIS));

            JLabel lab = new JLabel(opt,JLabel.TRAILING);
            lab.setAlignmentX(Component.CENTER_ALIGNMENT);
            aux.add(lab);
            aux.add(Box.createHorizontalStrut(12));

            if(opt.equals("COM Port")){
                aux.add(Box.createHorizontalStrut(1));
                addCOMOption(aux);
            }else if(opt.equals("BaudRate")){
                addBAUDOption(aux);
            }else if(opt.equals("TimeOuts")){
                addTIMEOption(aux);
            }else{

                if(opt.equals("ReadTimeOut")){
                    aux.add(Box.createHorizontalStrut(2));
                }

                JTextField tf = new JTextField();
                tf.setName(opt);
                fields.add(tf);

                aux.add(tf);
            }

            base.add(aux);
        }

        JPanel fullScreen = new JPanel(new BorderLayout());
        fullScreen.add(update,BorderLayout.CENTER);
        base.add(fullScreen);

        JPanel fullScreen0 = new JPanel(new BorderLayout());
        fullScreen0.add(submit,BorderLayout.CENTER);
        base.add(fullScreen0);

        JPanel fullScreen2 = new JPanel(new BorderLayout());
        fullScreen2.add(testData,BorderLayout.CENTER);
        base.add(fullScreen2);

        JPanel fullScreen1 = new JPanel(new BorderLayout());
        fullScreen1.add(test,BorderLayout.CENTER);
        base.add(fullScreen1);

        return base;
    }

    private void addTIMEOption(JPanel aux) {
        String[] options = {"TIMEOUT_NONBLOCKING","TIMEOUT_READ_SEMI_BLOCKING","TIMEOUT_READ_BLOCKING","TIMEOUT_SCANNER"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setName("TimeOuts");

        comboBoxes.add(comboBox);
        aux.add(comboBox);
    }

    private void addBAUDOption(JPanel aux) {
        String[] options = {"300", "600", "1200", "2400", "4800", "9600", "14400", "19200", "28800", "38400", "57600", "115200"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setSelectedItem("9600");
        comboBox.setName("BaudRate");

        comboBoxes.add(comboBox);
        aux.add(comboBox);
    }

    private void addCOMOption(JPanel aux) {
        JComboBox<String> comboBox = new JComboBox<>(generateCOMList());

        comboBox.setName("COM Port");

        comboBoxes.add(comboBox);
        aux.add(comboBox);
    }

    private String[] generateCOMList(){
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] generatedData = new String[ports.length];

        int i = 0;
        for(SerialPort p : ports){
            generatedData[i++] = p.getDescriptivePortName() + " - " + p.getPortDescription();
        }

        return generatedData;
    }

    private void addExitManagement() {
        ExitManager.addJFrame(this);
        addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                ExitManager.exit();
            }
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosed(WindowEvent e) {

            }
            @Override
            public void windowIconified(WindowEvent e) {

            }
            @Override
            public void windowDeiconified(WindowEvent e) {

            }
            @Override
            public void windowActivated(WindowEvent e) {

            }
            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    public String getTestData(){
        return testData.getText();
    }

    public void addController(SettingsController sc) {
        submit.addActionListener(sc);
        update.addActionListener(sc);
        test.addActionListener(sc);

        for(JComboBox jc : comboBoxes){
            if(jc.getName().equals("TimeOuts")){
                jc.addPopupMenuListener(sc);
                break;
            }
        }
    }

    public void updateCOMComboBox(){
        JComboBox<String> elected = null;

        for(JComboBox c : comboBoxes){
            if(c.getName().equals("COM Port")){
                elected = c;
                break;
            }
        }
        if(elected != null) {
            elected.removeAllItems();
            for (String s : generateCOMList()) {
                elected.addItem(s);
            }
        }
    }

    public void updateConnectionState(boolean newState){
        if(newState){
            connectionIndicator.setBackground(CONNECTED);
        }else{
            connectionIndicator.setBackground(DISCONNECTED);
        }
    }

    public SerialPort getSerialPort() {
        return SerialPort.getCommPorts()[getComboIndex("COM Port")];
    }

    public int getBaudRate() {
        return Integer.valueOf((String)getComboData("BaudRate"));
    }

    public String getTimeOut () {
        return (String)getComboData("TimeOuts");
    }

    public boolean usingTimeOut(){
        return !(getComboData("TimeOuts")).equals("TIMEOUT_NONBLOCKING");
    }

    public int getReadTimeOut() {
        return getFieldData("ReadTimeOut");
    }

    public int getWriteTimeOut() {
        return getFieldData("WriteTimeOut");
    }

    private Object getComboData(String name){
        for (JComboBox c : comboBoxes) {
            if(c.getName().equals(name)){
                return c.getSelectedItem();
            }
        }
        return null;
    }
    private int getComboIndex(String name){
        for (JComboBox c : comboBoxes) {
            if(c.getName().equals(name)){
                return c.getSelectedIndex();
            }
        }
        return -1;
    }

    private int getFieldData(String name){
        for (JTextField f : fields) {
            if(f.getName().equals(name)){
                return Integer.valueOf(f.getText());
            }
        }
        return 0;
    }

    public void clearFields() {
        for(JTextField tf : fields){
            tf.setText("");
        }
    }
}
