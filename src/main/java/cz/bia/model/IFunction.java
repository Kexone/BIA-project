package cz.bia.model;

/**
 * Created by Jakub Sevcik on 22.09.2016.
 */
public interface IFunction {

    double calculate(double[] params);
    float getMinRange();
    float getMaxRange();
}
