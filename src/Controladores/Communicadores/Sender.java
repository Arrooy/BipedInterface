package Controladores.Communicadores;

import Controladores.ExitManager;
import com.fazecast.jSerialComm.SerialPort;

import java.util.LinkedList;

public class Sender extends Thread{

    private SerialPort sp;
    private boolean ableToSend;
    private LinkedList<byte[]> espera;
    private byte[] buffer;

    public Sender(SerialPort mySerial) {
        sp = mySerial;
        ableToSend = false;
        ExitManager.addThread(this);
        espera = new LinkedList<>();
        this.start();
    }

    public void send(byte[] newData){
        espera.add(newData);
    }
    public void sendALLCOST(byte[] newData) throws InterruptedException {
        while(sp.bytesAwaitingWrite() != 0){
            sleep(100);
        }
        sp.writeBytes(newData,newData.length);
    }

    @Override
    public void run() {
        try{

            while(true){
                if(ableToSend){

                    System.out.print("S ");
                    int sent =  sp.writeBytes(buffer,buffer.length);
                    System.out.println(buffer.length == sent ? " .>Success" : " .>Failed");
                    ableToSend = false;
                }else{
                    if(espera.size() != 0){
                        buffer = espera.getFirst();
                        espera.removeFirst();
                        ableToSend = true;
                    }else{
                        sleep(25);
                    }
                }
            }
        }catch (InterruptedException e){
            System.out.println("Controladores.Communicadores.Sender Interrupted...");
        }
    }
}
