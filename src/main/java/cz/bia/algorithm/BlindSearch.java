package cz.bia.algorithm;

import cz.bia.Population;
import org.jzy3d.maths.Coord3d;

/**
 * Created by Jakub Sevcik on 23.11.2016.
 */
public class BlindSearch extends Algorithm {

    @Override
 public Coord3d start(Population pop) {
    return super.getBest(pop.getPopulation());
 }

}
