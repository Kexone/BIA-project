package cz.bia;


import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {

    private Coord3d[] population;
    private Random rand = new Random();
    private final double F = 0.5;
    private final double CR = 0.9;
    private double min;
    private double max;
    public void randomPopulation(double min, double max, int countPopulation, IFunction function) {
        this.min = min;
        this.max = max;
        this.population = new Coord3d[countPopulation];
            for(int i = 0; i < countPopulation; i++) {

                double x = (max - min) * rand.nextDouble() + min ;
                double y = (max - min) * rand.nextDouble() + min ;
                double z = function.calculate(new double[]{x, y});
                population[i] = new Coord3d(x, y, z);
            }
        }

    public Coord3d[] getPopulation() {

        return population;
    }
    public Coord3d getBest(){
        return Arrays.stream(this.population).min(Comparator.comparing(c -> c.z)).orElseThrow(() -> new RuntimeException("there is no best"));
    }

    public Coord3d[] getFor(IFunction function) {
        Coord3d[] newPopulation = new Coord3d[population.length];
        Coord3d noise = new Coord3d();
        for (int i = 0; i < population.length; i++) {
            newPopulation[i] = new Coord3d();
            Coord3d[] solution =  {population[rand.nextInt(population.length-1)], population[rand.nextInt(population.length-1)], population[rand.nextInt(population.length-1)]};
            noise.x = (float) (solution[0].x +  F * (solution[1].x + solution[2].x));
            noise.y = (float) (solution[0].y +  F * (solution[1].y + solution[2].y));

                double random = rand.nextDouble();
                if(random < CR) {
                    newPopulation[i].x = noise.x;
                }
                else if( random >= CR) {
                    newPopulation[i].x = population[i].x;
                }
                random = rand.nextDouble();
                if(random < CR) {
                    newPopulation[i].y = noise.y;
                }
                else if( random >= CR) {
                    newPopulation[i].y = population[i].y;
                }

            newPopulation[i].z = (float) function.calculate(new double[]{newPopulation[i].x, newPopulation[i].y});
            if(newPopulation[i].x < min || newPopulation[i].x > max || newPopulation[i].y < min || newPopulation[i].y > max ) {
                i--;
                continue;
            }
            System.out.println("Old X:" + population[i].x +" Y:" +population[i].y +" Z:" +population[i].z );
            System.out.println("New X:" + newPopulation[i].x +" Y:" +newPopulation[i].y +" Z:" +newPopulation[i].z );
        }
    return newPopulation;
    }
}
