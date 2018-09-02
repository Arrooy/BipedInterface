package Controladores;

import Controladores.Communicadores.Reciever;
import Controladores.Communicadores.Sender;
import Helpers.BatLevel;
import Helpers.CMD;
import Helpers.GyroValues;
import Vistas.BateriaView;
import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;

public class Robot {

    private static Sender sender;
    private static Reciever reciever;
    private boolean portIsOpen;
    private boolean servosUp;

    public Robot() {
        portIsOpen = false;
        servosUp = false;
    }

    public static void exit() {

        System.out.println("Sending exit CMD...");
        try {
            sender.sendALLCOST(CMD.stopAll());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void openPort(SerialPort mySerial) {
        portIsOpen = true;

        if(sender != null) sender.interrupt();
        sender = new Sender(mySerial);

        if(reciever != null)reciever.interrupt();
        reciever = new Reciever(mySerial);

    }

    private synchronized void send(byte[] finalBuffer) {
        if(portIsOpen)
        sender.send(finalBuffer);
    }

    public void initGyro(){
        send(CMD.initGyro());
        send(CMD.startGyro());
    }

    public boolean isReady() {
        return portIsOpen;
    }

    public void sendRAW(byte[] finalBuffer) {
        System.out.println("SENDING AT REAL " + Arrays.toString(finalBuffer));
        if(portIsOpen) sender.send(finalBuffer);
    }

    public GyroValues readGyro() {
        return ready() ? reciever.getLastGyroVal() : null;
    }

    public BatLevel readBattery() {
        return ready() ? reciever.getBatLevel() : null;
    }

    private boolean ready(){
        return reciever != null && portIsOpen && reciever.hasMessages();
    }

    public void requestBattery() {
        sender.send(CMD.getBatteryLevel());
    }

    public void requestGyroCalibration() {
        sender.send(CMD.calibrateGyro());
    }

    public void upateServos(byte[] values) {
        if(!servosUp){
            sender.send(CMD.initServos());
            sender.send(CMD.startServos());
            servosUp = true;
        }
        sender.send(CMD.newPos(values));

    }
}
