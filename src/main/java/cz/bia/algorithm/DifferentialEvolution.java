package cz.bia.algorithm;

import cz.bia.Population;
import org.jzy3d.maths.Coord3d;

import cz.bia.model.IFunction;

/**
 * Created by Jakub Sevcik on 23.11.2016.
 */
public class DifferentialEvolution extends Algorithm {

    private final double F;
    private final double CR;

    public DifferentialEvolution(IFunction function,double max, double min, boolean isDiscrete, double freq, double CR){
        this.function = function;
        this.F = freq;
        this.CR = CR;
        this.min = min;
        this.max = max;
        this.discrete = isDiscrete;
    }

    @Override
    public Coord3d start(Population pop) {
        Coord3d[] population = pop.getPopulation();
        Coord3d[] newPopulation = new Coord3d[population.length];
        Coord3d noise = new Coord3d();
        for (int i = 0; i < population.length; i++) {
            newPopulation[i] = new Coord3d();
            Coord3d[] solution =  {population[this.getIntegerRandom(population.length)], population[this.getIntegerRandom(population.length)], population[this.getIntegerRandom(population.length)]};
            noise.x = (float) (solution[0].x +  F * (solution[1].x - solution[2].x));
            noise.y = (float) (solution[0].y +  F * (solution[1].y - solution[2].y));

            double random = this.getDoubleRandom();
            if(random < CR) {
                newPopulation[i].x = this.isDiscrete() ? (int) noise.x : noise.x;
            }
            else if( random >= CR) {
                newPopulation[i].x = this.isDiscrete() ? (int) population[i].x : population[i].x;
            }
            random = this.getDoubleRandom();
            if(random < CR) {
                newPopulation[i].y = this.isDiscrete() ? (int) noise.y : noise.y;
            }
            else if( random >= CR) {
                newPopulation[i].y = this.isDiscrete() ? (int) population[i].y : population[i].y;
            }
            if(this.isDiscrete()) {
                newPopulation[i].z = (int) function.calculate(new double[]{newPopulation[i].x, newPopulation[i].y});
            }
            else {
                newPopulation[i].z = (float) function.calculate(new double[]{newPopulation[i].x, newPopulation[i].y});
            }
            if(newPopulation[i].x < min || newPopulation[i].x > max || newPopulation[i].y < min || newPopulation[i].y > max ) {
                i--;
                continue;
            }
            if(population[i].z < newPopulation[i].z) {
                newPopulation[i] = population[i];
            }
            //  System.out.println("Old X:" + population[i].x +" Y:" +population[i].y +" Z:" +population[i].z );
            //  System.out.println("New X:" + newPopulation[i].x +" Y:" + newPopulation[i].y +" Z:" + newPopulation[i].z );
        }
        pop.setPopulation(newPopulation);
        return this.getBest(population);
    }
}
