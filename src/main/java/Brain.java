import com.google.common.collect.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Brain {
    private Integer lp;
    private Integer score;
    private Integer fitness;
    private Float pc;
    private Multimap<Integer,Multimap<Integer,Perceptron>> perceptronMap;

    public Brain(){}

    public Brain(Integer lp, Integer score, Integer fitness, Float pc, Multimap<Integer, Multimap<Integer,Perceptron>> perceptronMap) {
        this.lp = lp;
        this.score = score;
        this.fitness = fitness;
        this.pc = pc;
        this.perceptronMap = perceptronMap;
    }

    public Multimap<Integer,Perceptron> getGivenLayer(int layer){
        return Iterables.get(getPerceptronMap().values(),layer);
    }
    public Multimap<Integer,Float> getOutputLayer(){
        Multimap<Integer,Float> out = Multimaps.transformValues(getGivenLayer(getPerceptronMap().size()-1),Perceptron::activation);
        return out;
    }

    public void createPerceptronMap(Integer layers, List<Integer> rows){
        perceptronMap = ArrayListMultimap.create();
        for (int k = 0; k < layers; k++) {//3
            Multimap<Integer,Perceptron> temp = ArrayListMultimap.create();
            for (int l = 0; l < rows.get(k); l++) {
                Perceptron p = new Perceptron();
                if(k==0){
                    Float weight = ThreadLocalRandom.current().nextFloat();
                    Float value = ThreadLocalRandom.current().nextFloat();
                    p.getInputs().put(weight,value);
                }else{
                    for(Perceptron pp : getGivenLayer(k-1).values()){
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        p.getInputs().put(weight,pp.getOutput());
                    }
                }
                temp.put(l,p);
            }
            perceptronMap.put(k,temp);
        }
        /*for (int j = 0; j < i; j++) {
            perceptronMap.put(j,new Perceptron());
        }*/
    }

    public void createDefaultPerceptronMap(){
        List<Integer> rows = new ArrayList<>();
        rows.add(16);
        rows.add(4);
        perceptronMap = ArrayListMultimap.create();
        for (int k = 0; k < 2; k++) {
            Multimap<Integer,Perceptron> temp = ArrayListMultimap.create();
            for (int l = 0; l < rows.get(k); l++) {
                Perceptron p = new Perceptron();
                if(k==0){
                    Float weight = ThreadLocalRandom.current().nextFloat();
                    Float value = ThreadLocalRandom.current().nextFloat();
                    p.getInputs().put(weight,value);
                }else{
                    for(Perceptron pp : getGivenLayer(k-1).values()){
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        p.getInputs().put(weight,pp.getOutput());
                    }
                }
                temp.put(l,p);
            }
            perceptronMap.put(k,temp);
        }
    }


    public Integer getLp() {
        return lp;
    }

    public void setLp(Integer lp) {
        this.lp = lp;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getFitness() {
        return fitness;
    }

    public void setFitness(Integer fitness) {
        this.fitness = fitness;
    }

    public Float getPc() {
        return pc;
    }

    public void setPc(Float pc) {
        this.pc = pc;
    }

    public Multimap<Integer, Multimap<Integer, Perceptron>> getPerceptronMap() {
        return perceptronMap;
    }

    public void setPerceptronMap(Multimap<Integer, Multimap<Integer, Perceptron>> perceptronMap) {
        this.perceptronMap = perceptronMap;
    }
}
