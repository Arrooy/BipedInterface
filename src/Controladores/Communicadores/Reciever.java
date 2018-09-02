package Controladores.Communicadores;

import Controladores.ExitManager;
import Controladores.Robot;
import Helpers.BatLevel;
import Helpers.CMD;
import Helpers.GyroValues;
import Helpers.LoopTime;
import com.fazecast.jSerialComm.SerialPort;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.LinkedList;

public class Reciever extends Thread {

    private SerialPort sp;
    private int errorCount;
    private LinkedList<Object> messages;

    public Reciever(SerialPort sp){
        this.sp = sp;
        ExitManager.addThread(this);
        messages = new LinkedList<>();
        errorCount = 0;
        this.start();
    }

    public boolean hasMessages(){
        return !messages.isEmpty();
    }

    public BatLevel getBatLevel(){
        int listSize = messages.size();

        for(int i = 0; i < listSize; i++){
            Object aux = messages.get(i);
            if(aux instanceof BatLevel){
                messages.remove(aux);
                return (BatLevel) aux;
            }
        }
        return null;
    }

    public LoopTime getLoopTime(){
        int listSize = messages.size();

        for(int i = 0; i < listSize; i++){
            Object aux = messages.get(i);
            if(aux instanceof LoopTime){
                messages.remove(aux);
                return (LoopTime) aux;
            }
        }
        return null;
    }

    public synchronized GyroValues getLastGyroVal(){
        int listSize = messages.size();

        for(int i = 0; i < listSize; i++){
            Object aux = messages.get(i);
            if(aux instanceof GyroValues){
                messages.remove(aux);
                return (GyroValues) aux;
            }
        }
        return null;
    }

    @Override
    public void run() {
        while (true){
            try {

                while (sp.bytesAvailable() <  9) sleep((10 / (sp.bytesAvailable() == 0 ? 1 : sp.bytesAvailable())) < 0 ? 10 : sp.bytesAvailable());
                byte[] buffer = new byte[9];
                if(sp.readBytes(buffer,9) != 9) System.out.println("R .>Failed");

                switch (buffer[0]){
                    case CMD.CMD_R_GYRO_VAL:
                        gyroSave(buffer);
                        break;
                    case CMD.CMD_R_loopTime:
                        timeSave(takeHalfPacket(buffer));
                        break;
                    case CMD.CMD_R_BAT:
                        batSave(takeHalfPacket(buffer));
                        break;
                        default:
                            System.out.println("UNKNOWN COMMAND FROM ARDUINO " + buffer[0]);
                            errorCount++;
                            if(errorCount > 10){
                                System.out.println("Stopping the robot");
                                ExitManager.exit();
                                errorCount = 0;
                            }
                }
            }catch (InterruptedException e){
                System.out.println("Closing reciever thread...");
            }catch (Exception e2){
                System.out.println("Timeout mal construit en RECIEVER");
            }
        }
    }

    private byte[] takeHalfPacket(byte[] arr){
        byte [] aux = new byte[4];
        for(int i = 0; i < 4; i++) aux[i] = arr[i + 1];
        return aux;
    }

    private void batSave(byte[] buffer) {
        messages.add(new BatLevel(generateFloat(buffer)));
    }

    private void timeSave(byte[] buffer) {
        messages.add(new LoopTime(generateLong(buffer)));
    }

    private void gyroSave(byte[] buffer) {
        byte [] dataInR = new byte[4];
        byte [] dataInP = new byte[4];

        //TODO:POC OPTIM EL MODUL
        for(int i = 0; i < 8; i++){
            if(i < 4)dataInR[i] = buffer[i+1];
            else dataInP[i%4] = buffer[i+1];
        }
        messages.add(new GyroValues(generateFloat(dataInR),generateFloat(dataInP)));
    }

    private float generateFloat(byte[] inData) {
        int intbit = (inData[3] << 24) | ((inData[2] & 0xff) << 16) | ((inData[1] & 0xff) << 8) | (inData[0] & 0xff);
        return Float.intBitsToFloat(intbit);
    }

    private long generateLong(byte[] inData) {
        return ((inData[3] << 24) | ((inData[2] & 0xff) << 16) | ((inData[1] & 0xff) << 8) | (inData[0] & 0xff));
    }
}
