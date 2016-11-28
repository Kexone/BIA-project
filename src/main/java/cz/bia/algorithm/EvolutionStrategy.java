package cz.bia.algorithm;

import cz.bia.Population;
import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Created by Jakub on 27.11.2016.
 */
public class EvolutionStrategy extends Algorithm {

    private final double fitnessValue;
    private final double deviation;
    private final boolean typeMode;
    public static boolean isCompleted;

    public EvolutionStrategy(IFunction function, double max, double min, boolean isDiscrete, double fitnessValue, double deviation, boolean typeMode) {
        this.function = function;
        this.min = min;
        this.max = max;
        this.discrete = isDiscrete;
        this.fitnessValue = fitnessValue;
        this.deviation = deviation;
        this.typeMode = typeMode;
    }

    @Override
    public Coord3d start(Population pop) {
        Coord3d[] descendants = pop.getPopulation().clone();
        for(int i = 0; i < descendants.length; i++) {
            float[] descendant  = descendants[i].toArray();
            for(int j = 0; j < descendant.length-1; j++) {
                descendant[j] = (float) (descendant[j] + this.getGaussRandom() * this.deviation);
                if(isInRange(descendant[j])) {
                    changeRange(descendant[j]);
                    j--;
                    continue;
                }
            }
            descendants[i] = setDescendants(descendant);
        }
        if(!typeMode) {
            descendants = chooseBests(descendants, pop.getPopulation());
        }
        pop.setPopulation(descendants);
        isCompleted = isCompleted(pop);
        return getBest(pop.getPopulation());
    }

    private Coord3d[] chooseBests(Coord3d[] descendants, Coord3d[] populations) {
        return Stream.of(descendants, populations)
                .flatMap(Arrays::stream)
                .sorted(Comparator.comparing(c -> c.z))
                .limit(descendants.length)
                .toArray(Coord3d[]::new);
    }

    private Coord3d setDescendants(float[] descendant) {
        Coord3d temp = new Coord3d();
    if (isDiscrete()) {
        temp.x = (int) descendant[0];
        temp.y = (int) descendant[1];
        temp.z = (int) function.calculate(new double[]{temp.x, temp.y});
    }
    else {
        temp.x = descendant[0];
        temp.y = descendant[1];
        temp.z = (float) function.calculate(new double[]{temp.x, temp.y});
    }
        return temp;
    }

    public void changeRange(float descendant) {
        while (descendant > max) {
            descendant = (float) (descendant - (max - min));
        }
        while (descendant < min) {
            descendant = (float) (descendant + (max - min));
        }
    }

    public boolean isInRange(float descendant) {
        return descendant > max || descendant < min;
    }

    private boolean isCompleted(Population pop) {
       return this.getBest(pop.getPopulation()).z <= this.fitnessValue;
    }
}
