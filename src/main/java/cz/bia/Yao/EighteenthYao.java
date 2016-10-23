package cz.bia.Yao;

import cz.bia.model.IFunction;

import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class EighteenthYao implements IFunction {

    @Override
    public double calculate(double[] params) {
        double part1 = 1 + pow(params[0] + params[1] + 1,2) *
                ( 10 - 14 * params[0] + 3 * pow(params[0],2) - 14 * params[1] + 6 * params[0] * params[1] + 3 * pow(params[1],2));
        double part2 = 30 + pow(2* params[0] - 3 * params[1],2) * (18 - 32 * params[0] + 12 * pow(params[0],2) + 48 * params[1] -
         36 * params[0] * params[1] + 27 * pow(params[1],2));
        return (part1 * part2);
    }

    @Override
    public float getMinRange() {
        return -2.f;
    }

    @Override
    public float getMaxRange() {
        return 2.f;
    }
}
