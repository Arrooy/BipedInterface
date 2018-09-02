package Vistas;

import Controladores.AnimatorController;
import Controladores.ExitManager;
import Controladores.Main;
import Controladores.Robot;
import Model.RobotFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Thread.sleep;

public class Animator extends JFrame implements Runnable{

    private final static int UPDATE_RATE = 50;
    private final static int height = 300;
    private final static int knobSize = 10;

    private Robot r;

    private JButton startAnimation;
    private JButton restartAnimation;
    private JButton stopAnimation;
    private JButton nextFrame;

    private JPanel representacio;
    private JTextField duracioTotal;
    private JSlider posicioEnElTemps;
    private JButton saveFrame;
    private JButton saveAnimation;
    private JButton loadAnimaton;
    private JTextField animationName;
    private JButton newAnimation;

    private JLabel actualFrameNumber;

    private int majorTickHelper;

    private RobotFrame actualFrame;

    private Arm upperLegL;
    private Arm lowerLegL;
    private Arm upperLegR;
    private Arm lowerLegR;
    private Arm footR;
    private Arm footL;

    private boolean editing;
    private boolean live;
    private boolean updateMeh;


//    short[] absMin = {100,90,210,230,115,100};
//    short[] absMax = {400,400,460,480,400,400};
//    short[] center = {250,210,380,300,285,250};

    public Animator(int posX, Robot robot) {
        r = robot;
        majorTickHelper = 1;
        live = false;

        setTitle("Bot_Animator " + Main.VERSION);
        setSize(389,564);
        setResizable(false);

        setLocation(posX,0);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addExitManagement();
        getContentPane().add(createVisual());

        double w = (double)getWidth() / 10.0, h = 2;
        double x = 75, y = 125;

        lowerLegR = new Arm(getWidth() - x - w - 10 + w / 2 + w * Math.cos(Math.toRadians(0)),y + w * Math.sin(Math.toRadians(0)),w,h, Color.YELLOW,174,0,66);
        upperLegR = new Arm(getWidth() - x - w - 10 + w / 2,y,w,h, Color.red,208,66,163);
        upperLegL = new Arm(x + w / 2,y,w,h, Color.red,220,77,117);
        lowerLegL = new Arm(x + w / 2 + w * Math.cos(Math.toRadians(0)),y + w * Math.sin(Math.toRadians(0)),w,h, Color.YELLOW,174,12,108);

        footL = new Arm(x + w / 2,height - 50,35,h + 5,Color.cyan,174,3,88);
        footR = new Arm(getWidth() - x - w - 10 + w / 2,height - 50,35,h + 5,Color.cyan,174,3,88);

        editing = false;
        updateMeh = false;
    }

    private JPanel createVisual() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));

        startAnimation = new JButton("➤");
        stopAnimation = new JButton("✕");
        restartAnimation = new JButton("◀◀");
        nextFrame = new JButton("⇉");

        JPanel le = new JPanel();
        le.setLayout(new BoxLayout(le,BoxLayout.X_AXIS));

        le.add(Box.createHorizontalStrut(2));
        le.add(fullScreen(startAnimation));
        le.add(fullScreen(stopAnimation));
        le.add(fullScreen(restartAnimation));
        le.add(fullScreen(nextFrame));

        main.add(Box.createVerticalStrut(2));

        main.add(le);

        representacio = new JPanel();
        representacio.setPreferredSize(new Dimension(representacio.getPreferredSize().width,height));

        main.add(Box.createVerticalStrut(2));
        main.add(representacio);
        main.add(Box.createVerticalStrut(10));

        duracioTotal = new JTextField();
        duracioTotal.setPreferredSize(new Dimension(35,duracioTotal.getPreferredSize().height));
        duracioTotal.setHorizontalAlignment(SwingConstants.CENTER);
        duracioTotal.setAlignmentY(Component.CENTER_ALIGNMENT);

        newAnimation = new JButton("New animation");

        actualFrameNumber = new JLabel("Frame " + 0);
        actualFrameNumber.setHorizontalAlignment(SwingConstants.CENTER);

        posicioEnElTemps = new JSlider(0,9,0);
        posicioEnElTemps.setMinorTickSpacing(1);
        posicioEnElTemps.setMajorTickSpacing(1);
        posicioEnElTemps.setAlignmentY(Component.CENTER_ALIGNMENT);
        posicioEnElTemps.setPaintTicks(true);
        posicioEnElTemps.setPaintLabels(true);

        main.add(fullScreen(actualFrameNumber));
        main.add(fullScreen(posicioEnElTemps));

        JPanel div = new JPanel();
        div.setLayout(new BoxLayout(div,BoxLayout.X_AXIS));


        div.add(newAnimation);
        div.add(Box.createHorizontalStrut(2));
        div.add(new JLabel("Nº Frames:"));
        div.add(Box.createHorizontalStrut(5));
        div.add(duracioTotal);
        div.add(Box.createHorizontalStrut(2));

        main.add(Box.createVerticalStrut(2));
        main.add(div);
        main.add(Box.createVerticalStrut(2));

        saveFrame = new JButton("Save frame");

        main.add(fullScreen(saveFrame));

        main.add(Box.createVerticalStrut(2));
        loadAnimaton = new JButton("Load animation");
        main.add(fullScreen(loadAnimaton));
        main.add(Box.createVerticalStrut(2));

        saveAnimation = new JButton("Save animation");
        animationName = new JTextField();

        JLabel aux = new JLabel("Animation name");
        aux.setHorizontalAlignment(SwingConstants.LEFT);


        JPanel div2 = new JPanel();
        div2.setLayout(new BoxLayout(div2,BoxLayout.X_AXIS));
        div2.add(Box.createHorizontalStrut(2));div2.add(aux);div2.add(Box.createHorizontalStrut(5));div2.add(fullScreen(animationName));

        main.add(Box.createVerticalStrut(2));
        main.add(div2);
        main.add(Box.createVerticalStrut(2));
        main.add(fullScreen(saveAnimation));
        return main;
    }

    private JPanel fullScreen(Component c){
        JPanel a = new JPanel(new BorderLayout());
        a.add(c,BorderLayout.CENTER);
        return a;
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

    @Override
    public void run() {
        while(true){
            try{

                Image img = representacio.createImage(getWidth(),height);
                if(img != null) {
                    Graphics2D g = (Graphics2D) img.getGraphics();

                    render(g);

                    Graphics2D g2 = (Graphics2D) representacio.getGraphics();
                    g2.drawImage(img, 0, 0, null);
                }
                sleep(UPDATE_RATE);
            }catch (Exception e){
                System.out.println("Closing animator thread...");
            }
        }
    }

    private void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),height);

        if(actualFrame != null){
            String angles = footR.getAngle()+ " " + lowerLegR.getAngle() +" "+ upperLegR.getAngle() + " "+
                    upperLegL.getAngle() + " " + lowerLegL.getAngle() + " " + footL.getAngle();

            String sentAngles =  footR.getConvertedAngle() +" " + lowerLegR.getConvertedAngle() +" "+
                    upperLegR.getConvertedAngle() + " "+ upperLegL.getConvertedAngle() + " " + lowerLegL.getConvertedAngle() + " " + footL.getConvertedAngle();

            g.setColor(Color.white);
            text("Frame.> " + actualFrame.toString(),2,0,Color.white,g);
            text("Real Angles.> " + angles,2,20,Color.white,g);
            text("Sent Angles.> " + sentAngles,2,40,Color.white,g);

            text("Live:",getWidth() - 50,0,Color.white,CENTER_ALIGNMENT,g);
            g.setColor(live ? Color.green : Color.red);
            g.fillOval(getWidth() - 55 + g.getFontMetrics().stringWidth("Live:"),4,14,14);

            renderFrame(g);
        }else{
            text("No animation loaded",getWidth() / 2,height / 2,Color.red,CENTER_ALIGNMENT,g);
        }

    }

    private void text(String data, int x, int y, Color color,Graphics2D g) {
        text(data,x,y,color,LEFT_ALIGNMENT,g);
    }

    private void text(String data, int x, int y, Color color, float alignment,Graphics2D g) {
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        if (alignment == LEFT_ALIGNMENT){
            g.drawString(data,x,y + fm.getHeight());
        }else{


            g.drawString(data,x - fm.stringWidth(data) / 2,y + fm.getHeight());
        }
    }

    public void addController(AnimatorController ac) {

        representacio.addKeyListener(ac);

        saveFrame.setActionCommand("saveFrame");
        saveAnimation.setActionCommand("saveAnimation");

        saveFrame.addActionListener(ac);
        saveAnimation.addActionListener(ac);

        newAnimation.setActionCommand("duracioTotal");
        newAnimation.addActionListener(ac);

        posicioEnElTemps.addChangeListener(ac);

        representacio.addMouseListener(ac);
        representacio.addMouseMotionListener(ac);

        loadAnimaton.addActionListener(ac);
        loadAnimaton.setActionCommand("loadAnimation");

        startAnimation.addActionListener(ac);
        stopAnimation.addActionListener(ac);
        restartAnimation.addActionListener(ac);
        nextFrame.addActionListener(ac);

        startAnimation.setActionCommand("startAnimation");
        stopAnimation.setActionCommand("stopAnimation");
        restartAnimation.setActionCommand("restartAnimation");
        nextFrame.setActionCommand("nextFrame");
    }

    public int updateDuracioTotal() {
        return updateDuracioTotal(Integer.valueOf(duracioTotal.getText()));
    }


    public int updateDuracioTotal(int valor) {
        posicioEnElTemps.setMaximum(valor - 1);

        majorTickHelper = valor / 10 + 1;
        posicioEnElTemps.setLabelTable(null);
        posicioEnElTemps.setMajorTickSpacing(majorTickHelper);
        return valor;
    }

    public byte[] getValues() {
        return actualFrame.getValues();
    }

    public void representaFrame(RobotFrame frame) {
        actualFrame = frame;
        byte[] positions = actualFrame.getValues();
        int[] angles = translateValues(positions);

        upperLegR.updateAngle(angles[2]);
        lowerLegR.update(upperLegR.getEndX(),upperLegR.getEndY());

        upperLegL.updateAngle(angles[3]);
        lowerLegL.update(upperLegL.getEndX(),upperLegL.getEndY());

        lowerLegR.updateAngle(angles[1]);
        lowerLegL.updateAngle(angles[4]);

        footL.updateAngle(angles[5]);
        footR.updateAngle(angles[0]);
    }

    public void updateSlider(int nex) {
        actualFrameNumber.setText("Frame " + nex);
        posicioEnElTemps.setValue(nex);
    }

    private void renderFrame(Graphics2D g) {

        double w = (double)getWidth() / 10.0, h = 2;
        double x = 75, y = 125;

        text("Left",(int)(x + w / 2),(int)y - 20, Color.white,CENTER_ALIGNMENT,g);

        g.setColor(Color.green);
        g.fill(new Rectangle2D.Double(x,y,w,h));

        text("Right",(int)(getWidth() - x - w - 10 + w / 2),(int)y - 20, Color.white,CENTER_ALIGNMENT,g);

        g.setColor(Color.green);
        g.fill(new Rectangle2D.Double(getWidth() - x - w - 10,y,w,h));

        if(!editing){
            byte[] positions = actualFrame.getValues();
            int[] angles = translateValues(positions);

            upperLegR.updateAngle(angles[2]);
            lowerLegR.update(upperLegR.getEndX(),upperLegR.getEndY());

            upperLegL.updateAngle(angles[3]);
            lowerLegL.update(upperLegL.getEndX(),upperLegL.getEndY());

            lowerLegR.updateAngle(angles[1]);
            lowerLegL.updateAngle(angles[4]);

            footL.updateAngle(angles[5]);
            footR.updateAngle(angles[0]);
        }


        upperLegL.render(g);
        lowerLegL.render(g);
        upperLegR.render(g);
        lowerLegR.render(g);
        footL.render(g);
        footR.render(g);

        g.setColor(upperLegL.getBallColor());
        g.fill(new Ellipse2D.Double(upperLegL.getBallX(10),upperLegL.getBallY(10),10,10));

        g.setColor(upperLegR.getBallColor());
        g.fill(new Ellipse2D.Double(upperLegR.getBallX(10),upperLegR.getBallY(10),10,10));

        g.setColor(lowerLegL.getBallColor());
        g.fill(new Ellipse2D.Double(lowerLegL.getBallX(10),lowerLegL.getBallY(10),10,10));

        g.setColor(lowerLegR.getBallColor());
        g.fill(new Ellipse2D.Double(lowerLegR.getBallX(10),lowerLegR.getBallY(10),10,10));

        g.setColor(footR.getBallColor());
        g.fill(new Ellipse2D.Double(footR.getBallX(10),footR.getBallY(10),10,10));

        g.setColor(footL.getBallColor());
        g.fill(new Ellipse2D.Double(footL.getBallX(10),footL.getBallY(10),10,10));
    }


    public void mouseMoved(int x, int y) {
        if(upperLegL.hover(x,y,knobSize)) {
            upperLegL.move(x, y);
            lowerLegL.update(upperLegL.getEndX(),upperLegL.getEndY());
        }

        if(upperLegR.hover(x,y,knobSize)){
            upperLegR.move(x,y);
            lowerLegR.update(upperLegR.getEndX(),upperLegR.getEndY());
        }

        if(lowerLegL.hover(x,y,knobSize)){
            lowerLegL.move(x,y);
        }

        if(lowerLegR.hover(x,y,knobSize)){
            lowerLegR.move(x,y);
        }

        if(footR.hover(x,y,knobSize)){
            footR.move(x,y);
        }

        if(footL.hover(x,y,knobSize)){
            footL.move(x,y);
        }
    }


    public void mousePress(boolean state) {
        representacio.requestFocus();

        if(footR.mousePress(state)){actualFrame.updateValue(footR.getConvertedAngle(),0);}
        if(lowerLegR.mousePress(state)){actualFrame.updateValue(lowerLegR.getConvertedAngle(),1);}
        if(upperLegR.mousePress(state)){actualFrame.updateValue(upperLegR.getConvertedAngle(),2);}
        if(upperLegL.mousePress(state)){actualFrame.updateValue(upperLegL.getConvertedAngle(),3);}
        if(lowerLegL.mousePress(state)){actualFrame.updateValue(lowerLegL.getConvertedAngle(),4);}
        if(footL.mousePress(state)){actualFrame.updateValue(footL.getConvertedAngle(),5);}

        editing = state;
    }

    private int[] translateValues(byte[] p) {
        int[] aux = new int[p.length];
        for(int i = 0; i < p.length; i++){
            aux[i] = (int) map(p[i] < 0 ? p[i] + 255 : p[i],0,255,0,180);
        }
        return aux;
    }

    private double map(double value, double istart, double istop, double ostart, double ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public String getFileName() {
        return animationName.getText();
    }

    public void toogleLive() {
        live = !live;
    }
    public boolean isLive(){
        return live;
    }

    public RobotFrame getActualFrame() {
        return actualFrame;
    }
}

class Arm{
    private double x,y,w,h;
    private int angle,maxAngle,minAngle,centerAngle;
    private Color ball,me;
    private boolean isHover;
    private boolean selected;

    public Arm(double x,double y,double w, double h,Color me,int ma,int mi,int center){
        maxAngle = ma;
        minAngle = mi;
        centerAngle = center;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.me = me;
        ball = new Color(200,50,50);
        isHover = false;
        angle = centerAngle;
        selected = false;
    }

    public void render(Graphics2D g){

        AffineTransform old = g.getTransform();
        g.rotate(Math.toRadians(angle),x,y);
        g.setColor(me);
        g.fill(new Rectangle2D.Double(x ,y,w,h));
        g.setTransform(old);
    }

    public boolean hover(int x, int y, int i){
        isHover = onZone(x,y, (int) getBallX(i),(int)getBallY(i),i);
        if(isHover){
            ball = new Color(150,50,50);
        }else{
            ball = new Color(200,50,50);
        }
        return isHover || selected;
    }

    public Color getBallColor(){
        return ball;
    }

    public double getBallX(int size){
        return x + w * Math.cos(Math.toRadians(angle)) - size / 2.0;
    }

    public double getBallY(int size){
        return y + w * Math.sin(Math.toRadians(angle)) - size / 2.0;
    }

    public void update(int x,int y) {
        this.x = x;
        this.y = y;
    }

    private boolean onZone(int x, int y, int x1, int y1, int size){
        return Math.sqrt(Math.pow(x - x1,2) + Math.pow(y - y1,2)) <= size;
    }

    public void move(int x1, int y1) {
        int newAngle = (int) Math.toDegrees(Math.atan2((y1 - y), (x1 - x)));
        if(selected) this.angle = newAngle >= minAngle ? (newAngle <= maxAngle ? newAngle : angle) : angle;
    }

    public boolean mousePress(boolean state) {
        selected = state & isHover;
        return isHover;
    }

    public int getEndY() {
        return (int) (y + w * Math.sin(Math.toRadians(angle)));
    }

    public int getEndX() {
        return (int) (x + w * Math.cos(Math.toRadians(angle)));
    }

    public byte getConvertedAngle(){
            return (byte)map(angle,0,180,0,255);
    }

    private double map(double value, double istart, double istop, double ostart, double ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public void updateAngle(int angle) {
        this.angle = angle;
    }

    public String getAngle() {
        return String.valueOf(angle);
    }
}