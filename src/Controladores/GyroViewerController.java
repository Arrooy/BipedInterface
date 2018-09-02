package Controladores;

import Helpers.CMD;
import Helpers.GyroValues;
import Vistas.GyroViewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

public class GyroViewerController extends Thread implements ActionListener {

    private GyroViewer gv;
    private Image graphics;
    private Robot robot;
    private double pitch,roll;

    private boolean needsInitProtocol;

    public GyroViewerController(GyroViewer g, Robot robot) {
        gv = g;
        this.robot = robot;
        needsInitProtocol = true;
        ExitManager.addThread(this);
        this.start();
    }

    private void render(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0,0,360,360);
        graphics.setColor(Color.red);
        graphics.fill(new Ellipse2D.Double(-pitch - 3 + 180,roll - 3 + 180,6,6));
    }

    private void update(){
        if(robot.isReady()){

            if(needsInitProtocol){
                robot.initGyro();
                needsInitProtocol = false;
            }

            GyroValues val = robot.readGyro();
            if(val != null) {
                pitch = val.getPitch();
                roll = val.getRoll();
                gv.updateValues(pitch,roll);
            }
        }
    }

    @Override
    public void run() {

        // Aquestes variables haurien de sumar 17 sempre
        long updateDurationMillis = 0; // Mesura el temps d'execució d'updte() i render()
        long sleepDurationMillis = 0; // Mesura el temps que hade dormir la iteració per ajustar-se

        //Bucle principal d'actualització i renderització del panell
        try {

            //Aquest joc es super obert, podriem fer coses molt guapas amb IA i demes...
            //De moment falta optimitzar algunes coses, pro funcioiona bastant follat.
            //Quan acabem controls li fas un ojaso i ens enxixem eh
            while (true) {
                    if(graphics == null){
                        graphics = gv.genImage(360,360);
                        if(graphics == null)
                        Thread.sleep(250);
                    }else{

                    //S'inicien els temps de la iteració
                    long beforeUpdateRender = System.nanoTime();

                    update();

                    render((Graphics2D) graphics.getGraphics());

                    Graphics gameGraphics = gv.getGraphGraphics();

                    gameGraphics.drawImage(graphics, 0, 0, null);

                    Toolkit.getDefaultToolkit().sync();

                    gameGraphics.dispose();

                    //Es calcula la durada de U&R i el temps de repos
                    updateDurationMillis = (System.nanoTime() - beforeUpdateRender) / 1000000L;
                    sleepDurationMillis = Math.max(2, 17 - updateDurationMillis);

                    //Es reposa el thread
                    Thread.sleep(sleepDurationMillis);
                    }
            }
        } catch (Exception e) {
            System.out.println("Closing gyroViewerController thread...");
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "calibrateGyro":
                robot.requestGyroCalibration();
                break;
        }
    }
}
