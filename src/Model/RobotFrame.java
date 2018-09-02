package Model;

import java.util.Arrays;

public class RobotFrame {

    //CONVERTED AS BYTES!
    byte[] centerAngles = {124,93,-26,-91,-103,124};

    private byte[] values;

    public RobotFrame(byte ... frames){
        values = new byte[6];

        for(int i = 0 ;i < 6; i++){
            if(i < frames.length)values[i] = frames[i];
            else values[i] = centerAngles[i];
        }
    }

    public static RobotFrame copy(RobotFrame frame) {
        return new RobotFrame(frame.getValues());
    }

    public byte[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    public String toJSON(boolean last) {
        String aux = "{";
        for(int i= 0; i < 6; i++){
            aux += "\"" + i + "\":" + "\"" + values[i];
            if(i != 5)aux+= "\",";
            else aux+= "\"";
        }
        aux += "}";
        if(!last) aux += ",";
        return aux;
    }
    public void updateValue(byte newVal,int index){
        values[index] = newVal;
    }
}