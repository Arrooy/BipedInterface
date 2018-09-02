package Helpers;
public class GyroValues{
    private float roll;
    private float pitch;
    public GyroValues(float r,float p){
        this.roll = r;
        this.pitch = p;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }
}