package cz.bia;


import cz.bia.model.IFunction;
import org.jzy3d.maths.Coord3d;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {

    private Coord3d[] population;
    private Random rand = new Random();

    public void randomPopulation(double min, double max, int countPopulation, IFunction functinon) {

        this.population = new Coord3d[countPopulation];
            for(int i = 0; i < countPopulation; i++) {

                double x = (max - min) * rand.nextDouble() + min ;
                double y = (max - min) * rand.nextDouble() + min ;
                double z = functinon.calculate(new double[]{x, y});
                population[i] = new Coord3d(x, y, z);
            }
            //paintPoint(population, countPopulation);

        }

    public Coord3d[] getPopulation() {
        return population;
    }
    public Coord3d getBest(){
        return Arrays.stream(this.population).min(Comparator.comparing(c -> c.z)).orElseThrow(() -> new RuntimeException("there is no best"));
    }
}
