package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class SeventeenthYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        return pow(params[1] - (5.1f / 4 * pow(PI,2) * pow(params[0],2) + (5 / PI) * params[0] - 6),2) +
                10 * (1 - ( 1 / (8 * PI))) * cos(params[0]) + 10;
    }

    @Override
    public float getMinRange() {
        return -1.5f;
    }

    @Override
    public float getMaxRange() {
        return 1.5f;
    }
}
