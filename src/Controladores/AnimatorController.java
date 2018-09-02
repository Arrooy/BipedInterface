package Controladores;

import Controladores.FilesManager.Arxiu;
import Controladores.FilesManager.ArxiuManager;
import Controladores.FilesManager.MalformedJsonFileException;
import Model.RobotFrame;
import Vistas.Animator;
import Vistas.FinderWindow;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import java.awt.event.*;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class AnimatorController implements ActionListener, ChangeListener, MouseInputListener,Runnable,KeyListener {

    private Robot r;

    private int animationSpeed = 800;
    private Animator animator;
    private ArxiuManager manager;

    private FinderWindow finder;

    private RobotFrame[] frames;
    private int actualFrame;

    private boolean animacioCarregada;

    private boolean animationRunning;

    public AnimatorController(Animator animator, FinderWindow fw,Robot r) {
        this.animator = animator;
        finder = fw;
        this.r = r;

        frames = new RobotFrame[10];
        for(int i = 0; i < 10;i ++)frames[i] = new RobotFrame();

        manager = new ArxiuManager();
        Thread a = new Thread(this);
        a.start();
        ExitManager.addThread(a);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()){
            case "startAnimation":
                animationRunning = true;
                break;
            case "stopAnimation":
                animationRunning = false;
                break;
            case "restartAnimation":
                animationRunning = false;
                actualFrame = 0;
                sendLifeData();
                animator.representaFrame(frames[actualFrame]);
                animator.updateSlider(actualFrame);
                break;
            case "nextFrame":
                nextFrame();
                break;
            case "saveFrame":
               saveFrame();
                break;
            case "saveAnimation":
                saveAnimation();
                break;
            case "loadAnimation":
                animationRunning = false;
                finder.setVisible(true);
                updateJSONList();
                break;
            case "duracioTotal":
                int numOfFrames = animator.updateDuracioTotal();
                actualFrame = 0;
                frames = new RobotFrame[numOfFrames];
                for(int i = 0; i < frames.length; i++) frames[i] = new RobotFrame();
                animator.representaFrame(frames[actualFrame]);
                sendLifeData();
                break;
            //El boto actualitzar s'ha premut
            case "Actualitzar":

                //Es refresca la llista del LSFinder
                updateJSONList();
                break;

            //El boto carregar s'ha premut
            case "Carregar":
                //Es carrega l'arxiu selecionat a LSParser
                carregar();
                break;
        }
    }

    private void saveFrame() {
        frames[actualFrame] = animator.getActualFrame();
        System.out.println("Saved frame: " + frames[actualFrame].toString());
    }

    private void nextFrame() {

        actualFrame ++;
        if(actualFrame >= frames.length)actualFrame = 0;
        sendLifeData();
        animator.representaFrame(frames[actualFrame]);
        animator.updateSlider(actualFrame);
    }


    private void prevFrame() {
        actualFrame --;
        if(actualFrame < 0)actualFrame = frames.length - 1;
        sendLifeData();
        animator.representaFrame(frames[actualFrame]);
        animator.updateSlider(actualFrame);
    }

    private void sendLifeData(){
        if(animator.isLive()){
            if(r.isReady()){
                r.upateServos(frames[actualFrame].getValues());
            }
        }
    }

    //INIIT PO-s  126 94 231 167 154 126

    private double map(double value, double istart, double istop, double ostart, double ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    private short extractServoValue(byte val){
        short aux = val < 0 ? (short) (val + 255) : val;
        return (short)map(aux,0,255,93,409);
    }

    private String translate(RobotFrame frame) {
        String res = "";
        for(byte a : frame.getValues()){
            res += extractServoValue(a) + " ";
        }
        return res;
    }

    private void updateJSONList(){
        //Es refresca la llista del LSFinder
        manager.clearArxiuList();
        buscarArxiusJson(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        JSlider slider = (JSlider) e.getSource();
        if(slider.getValueIsAdjusting()) {
            actualFrame = slider.getValue();
            sendLifeData();
            animator.representaFrame(frames[actualFrame]);
            animator.updateSlider(actualFrame);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        animator.mousePress(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        animator.mousePress(false);
        if(animator.isLive()){
            saveFrame();
        }
        sendLifeData();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        animator.mouseMoved(e.getX(),e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        animator.mouseMoved(e.getX(),e.getY());
    }

    /**
     * buscarArxiusJson busca tots els arxius en format .json de la carpeta data.
     * Un cop troba un arxiu, el guarda. En cas de detectar que l'arxiu té un format json erroni,
     * guarda l'arxiu indicant que aquest es corrupte/erroni.
     */

    public void buscarArxiusJson(boolean indicarErrors){

        StringBuilder errors = new StringBuilder();
        boolean error = false;
        int numberErrors = 0;

        //Va buscant arxius fins a trobar-ne tots. En cas de trobar un d'incorrecte, salta l'excepcio MalformedJsonFileException
        do {
            try {
                manager.lookForJsonFiles();
            } catch (MalformedJsonFileException e) {
                error = true;
                errors.append(e.getArxiu().getNom());
                errors.append(System.lineSeparator());
                numberErrors++;
                manager.addArxiu(e.getArxiu());
            }

        }while(!manager.estanElsArxiusCarregats());

        if(error && indicarErrors){

            if(numberErrors > 1)
                //S'indica al usuari amb un error que el arxiu json trobat conté un error o varis errors
                finder.showDialog("Error en la recerca d'arxius .json",
                        "S'han trobat " + numberErrors + " fitxers amb errors:\n" + errors.toString(),
                        "error");
            else
                //S'indica al usuari amb un error que el arxiu json trobat conté un error o varis errors
                finder.showDialog("Error en la recerca d'arxius .json",
                        "S'ha trobat un fitxer amb errors:\n" + errors.toString(),
                        "error");
        }

        //Amb el seguent codi es refresca la llista d'arxius del LSFinder
        finder.resetList();
        for(Arxiu arxiu : manager.getArxius()){
            finder.addToList(arxiu);
        }
    }


    private void saveAnimation() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("\"totalFrames\":");
        stringBuilder.append("\"" + frames.length + "\",");

        stringBuilder.append("\"duracioTotal\":");
        stringBuilder.append("\"" + animationSpeed + "\",");

        stringBuilder.append("\"keyFrames\":[");
        for(int i = 0; i < frames.length; i++) stringBuilder.append(frames[i].toJSON(i == frames.length - 1));
        stringBuilder.append("]}");


        try {
            if (animator.getFileName().length() == 0){
               finder.showDialog("No name file","FileName has to be 1+ char long","error");
            }else{
                manager.createFile(animator.getFileName().contains(".json") ? animator.getFileName() : animator.getFileName() + ".json",stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedJsonFileException e) {
            e.printStackTrace();
        }

    }


    /**
     * Gestiona la funcio del boto carregar de LSFinder.
     */

    private void carregar(){

        //En el cas de haver selecionat un arxiu i que aquest sigui correcte, el carreguem a LSParser
        if (!finder.isSelectionEmpty() && finder.getSelecio().getJsonObject() != null) {

            manager.setArxiuSelecionat(finder.getSelecio());

            JSONObject novaAnimacio = manager.getArxiuSelecionat().getJsonObject();

            int tempsDeAnimacio = Integer.valueOf((String)novaAnimacio.get("duracioTotal"));
            int totalNumberOfFrames = Integer.valueOf((String)novaAnimacio.get("totalFrames"));

            JSONArray newFramesData = novaAnimacio.getJSONArray("keyFrames");
            frames = new RobotFrame[totalNumberOfFrames];

            byte[] buffer = new byte[6];

            for(int iFrame = 0; iFrame < totalNumberOfFrames; iFrame++){

                JSONObject newFrameJSON = ((JSONObject) newFramesData.get(iFrame));

                for (int motor = 0; motor < 6; motor++) {
                    buffer[motor] = Byte.valueOf((String) newFrameJSON.get(motor + ""));
                }

                frames[iFrame] = new RobotFrame(buffer);
            }

            actualFrame = 0;
            animator.updateDuracioTotal(totalNumberOfFrames);

            animationSpeed = tempsDeAnimacio;
            animator.representaFrame(frames[actualFrame]);

            finder.setVisible(false);

        }else if(finder.isSelectionEmpty()){
            //S'indica al usuari amb un error que el arxiu redactat a la textArea conté errors
            finder.showDialog("Informacio",
                    "Per a poder carregar un arxiu, has de selecionar-lo",
                    "info");
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                if (animationRunning) {

                    nextFrame();
                    sleep(animationSpeed);
                } else {
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Closing AnimatorController thread...");
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_SPACE:
                animator.toogleLive();
                break;
            case KeyEvent.VK_Q:
                frames[actualFrame] = new RobotFrame();
                animator.representaFrame(frames[actualFrame]);
                sendLifeData();
                break;
            case KeyEvent.VK_W:
                frames[actualFrame] =  RobotFrame.copy(frames[actualFrame - 1]);
                animator.representaFrame(frames[actualFrame]);
                sendLifeData();
                break;

            case KeyEvent.VK_LEFT:
                prevFrame();
                break;

            case KeyEvent.VK_RIGHT:
                nextFrame();
                break;
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

    }
}
