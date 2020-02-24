package quasar.lib;

public class MoreMath {
    /**
     * Linear interpolation
     * @param a Point 1
     * @param b Point 2
     * @param alpha Range [0,1], how far between each point should be
     * @return the result of linear interpolation
     */
    public static double lerp(double a, double b, double alpha) {
        return a + alpha * (b - a);
    }

    public static double clip(double num, double min, double max) {
        return Math.min( Math.max( min, num ), max );
    }

    public static boolean isClose(double a, double b, double err) {
        return Math.abs( a - b ) < err;
    }

    /**
     * This will return the tapeloop increment of a number & max/min.  i.e., if you give it max=2, min=0, index = 2,
     * it will loop back and return 0, not 3.
     */
    public static int tapeInc(int min, int max, int index) {
        if(index + 1 > max) return min;
        return index + 1;
    }
    public static int tapeDec(int min, int max, int index) {
        if(index - 1 < min) return max;
        return index - 1;
    }
}
