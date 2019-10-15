package quasar.lib;

import quasar.lib.fileTools.Trace;

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
            Trace.log("Misuse of listAdd with different lengths");
            return null;
        }

        double[] prod = a.clone();
        for (int i = 0; i < a.length; i++) {
            prod[i] = a[i] + b[i];
        }
        return prod;
    }
}
