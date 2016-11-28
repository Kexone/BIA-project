package cz.bia.algorithm;

import cz.bia.Population;
import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Jakub Sevcik on 23.11.2016.
 */
public abstract class Algorithm {

    private Random rand = new Random();
    protected boolean discrete;
    protected IFunction function;
    protected double min;
    protected double max;
    protected Coord3d individual;

    public abstract Coord3d start(Population pop);

    public Coord3d getBest(Coord3d[] pop){
        return Arrays.stream(pop).min(Comparator.comparing(c -> c.z)).orElseThrow(() -> new RuntimeException("there is no best"));
    }

    public Coord3d getWorst(Coord3d[] pop){
        return Arrays.stream(pop).max(Comparator.comparing(c -> c.z)).orElseThrow(() -> new RuntimeException("there is no worst"));
    }

    protected double getDoubleRandom() {
        return rand.nextDouble();
    }

    protected int getIntegerRandom(int from) {
        return rand.nextInt(from);
    }

    protected double getGaussRandom() {
        return rand.nextGaussian();
    }

    public boolean isDiscrete() {
        return discrete;
    }

    public Coord3d generateIndividual() {
        double x = (max - min) * getDoubleRandom() + min ;
        double y = (max - min) * getDoubleRandom() + min ;
        double z = function.calculate(new double[]{x, y});
        if(isDiscrete()) {
            return new Coord3d((int)x, (int)y, (int)z);
        }
        else
            return new Coord3d(x, y, z);

    }

    public void setIndividual(Coord3d individual) {
        this.individual = individual;
    }
}
