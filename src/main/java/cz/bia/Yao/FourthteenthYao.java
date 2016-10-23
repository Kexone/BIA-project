package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class FourthteenthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < 2; i++) {
            sum2 += pow(params[i],6);
            for (int j = 0; j < 25; j++) {
                    sum1 += (1 / (j + sum1));
            }
        }
        return pow((1/500) + sum1,-1);
    }

    @Override
    public float getMinRange() {
        return -5;
    }

    @Override
    public float getMaxRange() {
        return 5;
    }
}
