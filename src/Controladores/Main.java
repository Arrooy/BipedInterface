package Controladores;

import Vistas.*;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Scanner;

public class Main {
    public static final String VERSION = "0.0";

    //TODO: HACER QUE LOS THREADS DE LA BATERIA, EL GYRO I EL ANIMATOR SE ENCIENDAN AL ABRIR EL PUERTO!
    //TODO: JSLIDER EN GYRO PARA ELEGIR EL UPDATE RATE.

    //TODO: ENVIANDO VALORES 1 i 255 VA PERFECTO CODIGO ARDUINO, VA AL MAX I AL MIN.
    //TODO: FALTA REGULAR LOS MAPS DEL ANIMATOR PARA CREAR LOS ANGULOS BIEN.


    public static void main(String[] args) {

        ExitManager.init();

        SerialSettings ss = new SerialSettings(0,0);

        Robot robot = new Robot();

        SettingsController sc = new SettingsController(ss,robot);
        ss.addController(sc);

        GyroViewer gv = new GyroViewer(ss.getWidth(),0);
        GyroViewerController gvc = new GyroViewerController(gv,robot);
        gv.setController(gvc);

        FinderWindow fw = new FinderWindow();


        Animator animator = new Animator(717, robot);
        BateriaView bateriaView = new BateriaView(717  + animator.getWidth(),73,100,robot);
        AnimatorController ac = new AnimatorController(animator,fw,robot);
        animator.addController(ac);
        fw.configController(ac);

        addListeners(ss,gv,bateriaView);

        bateriaView.setVisible(true);
        gv.setVisible(true);
        animator.setVisible(true);
        ss.setVisible(true);

        Thread a = new Thread(animator);a.start();ExitManager.addThread(a);
    }

    private static void addListeners(JFrame a,JFrame b, JFrame c) {
        a.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {
                b.setExtendedState(JFrame.ICONIFIED);
                c.setExtendedState(JFrame.ICONIFIED);
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                b.setExtendedState(JFrame.NORMAL);
                c.setExtendedState(JFrame.NORMAL);
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

    }
}
