package Helpers;

import java.util.Arrays;

public class CMD {

    public final static int PACKET_SIZE = 7;

    public final static int CMD_INIT_GYRO = 1;
    public final static int CMD_INIT_SERVOS = 2;
    public final static int CMD_INIT_PID = 3;

    public final static int CMD_START_GYRO = 4;
    public final static int CMD_START_SERVOS = 5;
    public final static int CMD_START_PID = 6;

    public final static int CMD_STOP_GYRO = 7;
    public final static int CMD_STOP_SERVOS = 8;
    public final static int CMD_STOP_PID = 9;

    public final static int CMD_SERVOS_NEWPOS = 10;
    public final static int CMD_GYRO_CALIBRATE = 11;

    public final static int CMD_R_GYRO_VAL = 12;
    public final static int CMD_R_loopTime = 13;

    public final static int CMD_BAT = 14;
    public final static int CMD_R_BAT = 15;

    public final static int CMD_START_TIME = 16;
    public final static int CMD_STOP_TIME = 17;

    public final static int CMD_EXIT = 18;

    public static byte[] initGyro(){
        return basicCreator(CMD_INIT_GYRO);
    }
    public static byte[] initServos(){
        return basicCreator(CMD_INIT_SERVOS);
    }
    public static byte[] initPID(){
        return basicCreator(CMD_INIT_PID);
    }
    public static byte[] startGyro(){
        return basicCreator(CMD_START_GYRO);
    }
    public static byte[] startServos(){
        return basicCreator(CMD_START_SERVOS);
    }
    public static byte[] startPID(){
        return basicCreator(CMD_START_PID);
    }
    public static byte[] stopGyro(){
        return basicCreator(CMD_STOP_GYRO);
    }
    public static byte[] stopServos(){
        return basicCreator(CMD_STOP_SERVOS);
    }
    public static byte[] stopPID(){
        return basicCreator(CMD_STOP_PID);
    }
    public static byte[] calibrateGyro(){
        return basicCreator(CMD_GYRO_CALIBRATE);
    }
    public static byte[] getBatteryLevel(){
        return basicCreator(CMD_BAT);
    }
    public static byte[] startLoopTime(){
        return basicCreator(CMD_START_TIME);
    }
    public static byte[] stopLoopTime(){
        return basicCreator(CMD_STOP_TIME);
    }
    public static byte[] stopAll(){
        return basicCreator(CMD_EXIT);
    }


    public static byte[] newPos(byte ... positions){
        byte[] aux = new byte[PACKET_SIZE];
        aux[0] = (byte) CMD_SERVOS_NEWPOS;

        for(int i = 0; i < 6; i++){
            aux[i+1] = i < positions.length ? positions[i] : 0;
        }

        return aux;
    }

    private static byte[] basicCreator(int command){
        byte[] aux = new byte[PACKET_SIZE];
        aux[0] = (byte) command;
        for(byte a : aux)a = 0;
        return aux;
    }

}
