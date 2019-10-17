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
}