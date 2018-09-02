package Vistas;

import Controladores.ExitManager;
import Controladores.GyroViewerController;
import Controladores.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GyroViewer extends JFrame {

    private JPanel graphPanel;

    private JTextField pitchIndicator;
    private JTextField rollIndicator;
    private JButton calibGyro;

    public GyroViewer(int locationX,int locationY){

        setTitle("Bot_GyroViewer " + Main.VERSION);
       // setSize(366,409);
        setResizable(false);

        setLocation(locationX,locationY);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addExitManagement();

        JPanel c = new JPanel(new BorderLayout());
        graphPanel = new JPanel();
        graphPanel.setPreferredSize(new Dimension(360,360));
        c.add(graphPanel,BorderLayout.CENTER);
        pitchIndicator = new JTextField();
        pitchIndicator.setEditable(false);
        rollIndicator = new JTextField();
        rollIndicator.setEditable(false);

        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap,BoxLayout.Y_AXIS));

        JPanel aux = new JPanel();
        aux.setLayout(new BoxLayout(aux,BoxLayout.X_AXIS));
        aux.add(pitchIndicator);
        aux.add(rollIndicator);

        JPanel f = new JPanel(new BorderLayout());
        calibGyro = new JButton("Calibrate Gyro");
        f.add(calibGyro,BorderLayout.CENTER);

        wrap.add(aux);
        wrap.add(f);

        c.add(wrap,BorderLayout.SOUTH);

        getContentPane().add(c);
        pack();
    }
    public Graphics getGraphGraphics() {
        return graphPanel.getGraphics();
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

    public Image genImage(int i, int i1) {
        return graphPanel.createImage(i, i1);
    }

    public void updateValues(double pitch, double roll) {
        pitchIndicator.setText(String.valueOf(pitch));
        rollIndicator.setText(String.valueOf(roll));
    }
    public void setController(GyroViewerController gvc){
        calibGyro.setActionCommand("calibrateGyro");
        calibGyro.addActionListener(gvc);
    }

}
