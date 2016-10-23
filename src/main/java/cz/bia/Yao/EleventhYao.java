package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class EleventhYao implements IFunction {

    @Override
    public double calculate(double[] params) {
        double sum = 0;
        double pi = 1;
        for (int i = 0; i < params.length; i++) {
            sum += pow(params[i], 2);
            double pom =params[i] / sqrt(i+1);
            pi *= cos(pom) + 1;
        }
        return ( 0.00025f * sum - pi);
    }

    @Override
    public float getMinRange() {
        return -600;
    }

    @Override
    public float getMaxRange() {
        return 600;
    }
}
