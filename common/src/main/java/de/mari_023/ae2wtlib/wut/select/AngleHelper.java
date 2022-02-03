package de.mari_023.ae2wtlib.wut.select;

public class AngleHelper {

    public static float getAngle(double x, double y) {
        return correctAngle((float) (Math.toDegrees(Math.atan2(y, x)) + 90));
    }

    public static float correctAngle(float angle) {
        if (angle < 0) {
            angle += 360;
        } else if (angle > 360) {
            angle -= 360;
        }
        return angle;
    }

    public static float getX(int angle, int radius) {
        return (float) (Math.sin(Math.toRadians(angle)) * radius);
    }

    public static float getY(int angle, int radius) {
        return (float) (Math.cos(Math.toRadians(angle)) * radius);
    }
}
