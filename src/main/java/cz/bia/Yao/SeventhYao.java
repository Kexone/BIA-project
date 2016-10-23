package cz.bia.Yao;

import cz.bia.model.IFunction;

import java.util.Random;

import static java.lang.Math.pow;

/**
 * Created by Jakub Sevcik on 24.09.2016.
 */
public class SeventhYao implements IFunction {
    @Override
    public double calculate(double[] params) {
        Random rand = new Random();
        double sum = 0;
        for (int i = 0; i < params.length; i++) {
            sum =+ (i * pow(params[i],4) + rand.nextInt(2));
        }
        return sum;
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
