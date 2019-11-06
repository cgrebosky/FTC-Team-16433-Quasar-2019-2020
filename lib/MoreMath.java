package quasar.lib;

public class MoreMath {
    //Represents scalar * vector multiplication
    public static double[] listMultiply(double a, double[] b) {
        double[] product = b.clone();

        for (int i = 0; i < b.length; i++) {
            product[i] *= a;
        }

        return product;
    }
    //Represents vector + vector addition
    public static double[] listAdd(double[] a, double[] b) {
        if(a.length != b.length) {
            throw new IllegalArgumentException("Array lengths must be the same!  Currently a is "
                    + a.length + " and b is " + b.length);
        }

        double[] prod = a.clone();
        for (int i = 0; i < a.length; i++) {
            prod[i] = a[i] + b[i];
        }
        return prod;
    }

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
