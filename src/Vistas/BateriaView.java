package Vistas;

import Controladores.ExitManager;
import Controladores.Main;
import Controladores.Robot;
import Helpers.BatLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import static java.lang.Thread.sleep;


public class BateriaView extends JFrame implements Runnable {

    private final int UPDATE_RATE = 10000;

    public static final float maxBat = 5.5f;
    private JPanel foto;
    private float batteryLevel = 5.0f;
    private Robot r;

    public BateriaView(int locationX, int width, int height, Robot robot) {
        r = robot;
        setTitle("Bot_Bateria " + Main.VERSION);

        setResizable(false);
        setUndecorated(true);
        setSize(width, height);
        setLocation(locationX, 0);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        foto = new JPanel();
        getContentPane().add(foto);

        addExitManagement();
        Thread a = new Thread(this);
        a.start();
        ExitManager.addThread(a);
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

    public void update(boolean connected){

        Image img = foto.createImage(getWidth(), getHeight());
        Graphics2D a = (Graphics2D) img.getGraphics();

        a.setColor(Color.white);
        a.fillRect(0,0,getWidth(),getHeight());

        a.setColor(new Color(0x000000));

        double redPos = (float)getHeight() - batteryLevel / maxBat * (float)getHeight();

        a.fillRect(0,0,getWidth(),(int) redPos);

        float val = 100 - (float) ((redPos / (float) getHeight()) * 100);

        a.setColor(val < 50 ? val < 20 ?  new Color(0xCD0000) : new Color(0xCDCD00) : new Color(0x009B00));
        a.fillRect(0,(int)redPos,getWidth(),getHeight());

        FontMetrics fm = a.getFontMetrics();

        if(connected) {
            a.setColor(Color.WHITE);
            a.drawString((int) val + "%",getWidth() / 2 - fm.stringWidth((int) val + "%") /2 ,getHeight() / 2);
        }else{
            a.setColor(Color.RED);
            a.drawString("OFFLINE",getWidth() / 2 - fm.stringWidth("OFFLINE") /2 ,getHeight() / 2);
        }
        Graphics2D g = (Graphics2D) foto.getGraphics();

        g.drawImage(img,0,0,null);
    }


    @Override
    public void run() {
        while(true){
            if(r.isReady()) {
                r.requestBattery();

                BatLevel bat = r.readBattery();

                if (bat == null) {

                    update(false);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Closing batteryView Thread");
                    }
                } else {
                    batteryLevel = bat.getVal();
                    update(true);
                    try {
                        sleep(UPDATE_RATE);
                    } catch (InterruptedException e) {
                        System.out.println("Closing batteryView Thread");
                    }
                }
            }else{

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Closing batteryView Thread");
                }

                update(false);
            }
        }
    }
}
