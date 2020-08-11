import com.google.common.collect.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Brain implements Comparable<Brain>{
    private Integer lp;
    private Integer score;
    private Integer fitness;
    private Float pc;
    private Multimap<Integer, Multimap<Integer, Perceptron>> perceptronMap;

    public Brain() {
        lp = 0;
        score = 0;
        fitness = 0;
        pc = 0f;
    }

    public Brain(Integer lp, Integer score, Integer fitness, Float pc, Multimap<Integer, Multimap<Integer, Perceptron>> perceptronMap) {
        this.lp = lp;
        this.score = score;
        this.fitness = fitness;
        this.pc = pc;
        this.perceptronMap = perceptronMap;
    }

    public Multimap<Integer, Perceptron> getGivenLayer(int layer) {
        return Iterables.get(getPerceptronMap().values(), layer);
    }

    public Multimap<Integer, Float> getOutputLayer() {
        Multimap<Integer, Float> out = Multimaps.transformValues(getGivenLayer(getPerceptronMap().size() - 1), Perceptron::activation);
        return out;
    }

    public Integer getPerceptronCount() {
        Integer count = 0;
        for (Multimap<Integer, Perceptron> m : getPerceptronMap().values()) {
            for (Perceptron p : m.values()) {
                count++;
            }
        }
        return count;
    }

    public Perceptron getGivenPerceptron(int index) {
        Perceptron p = null;
        int temp = 0;
        int tempIndex = 0;
        for (Multimap<Integer, Perceptron> m : getPerceptronMap().values()) {
            if (temp + m.size() >= index) {
                break;
            }
            temp += m.size();
            tempIndex++;
        }

        if (tempIndex != 0) {
            for (Perceptron per : getGivenLayer(tempIndex).get(Math.abs(temp - index) - 1)) {
                p = per;
            }
        } else {
            for (Perceptron per : getGivenLayer(tempIndex).get(index)) {
                p = per;
            }
        }
        return p;
    }

    //TODO: Podaje index a on sprawdza w ktorej warstwie i ktory perceptron ma byc zamieniony
    public void replaceGivenPerceptron(Integer index, Perceptron p) {
        int temp = 0;
        int tempIndex = 0;
        for (Multimap<Integer, Perceptron> m : getPerceptronMap().values()) {
            if (temp + m.size() >= index) {
                break;
            }
            temp += m.size();
            tempIndex++;
        }
        //getGivenLayer(0)
        /*for (Multimap<Integer,Perceptron> m : getPerceptronMap().values()){
            for (Perceptron pp : m.values()){
                System.out.println(pp);
            }
        }*/
        //System.out.println("--------------------");
        //System.out.println(tempIndex);
        if (tempIndex != 0) {
            //System.out.println("Do podmiany: "+getGivenLayer(tempIndex).get(Math.abs(temp-index)-1));
            getGivenLayer(tempIndex).get(Math.abs(temp - index) - 1).stream().forEach(e -> {
                e.setInputs(p.getInputs());
            });
        } else {
            getGivenLayer(tempIndex).get(index).stream().forEach(e -> {
                e.setInputs(p.getInputs());
            });
        }
        //System.out.println("--------------------");
        //System.out.println("Po zamianie: ");
        /*for (Multimap<Integer,Perceptron> m : getPerceptronMap().values()){
            for (Perceptron pp : m.values()){
                System.out.println(pp);
            }
        }*/
    }

    public void createPerceptronMap(Integer layers, List<Integer> rows) {
        perceptronMap = ArrayListMultimap.create();
        for (int k = 0; k < layers; k++) {//3
            Multimap<Integer, Perceptron> temp = ArrayListMultimap.create();
            for (int l = 0; l < rows.get(k); l++) {
                Perceptron p = new Perceptron();
                if (k == 0) {
                    Float weight = ThreadLocalRandom.current().nextFloat();
                    Float value = ThreadLocalRandom.current().nextFloat();
                    p.getInputs().put(weight, value);
                } else {
                    for (Perceptron pp : getGivenLayer(k - 1).values()) {
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        p.getInputs().put(weight, pp.getOutput());
                    }
                }
                temp.put(l, p);
            }
            perceptronMap.put(k, temp);
        }
        /*for (int j = 0; j < i; j++) {
            perceptronMap.put(j,new Perceptron());
        }*/
    }

    public void createDefaultPerceptronMap() {
        List<Integer> rows = new ArrayList<>();
        rows.add(16);
        rows.add(8);
        rows.add(4);
        rows.add(4);
        perceptronMap = ArrayListMultimap.create();
        for (int k = 0; k < 4; k++) {
            Multimap<Integer, Perceptron> temp = ArrayListMultimap.create();
            for (int l = 0; l < rows.get(k); l++) {
                Perceptron p = new Perceptron();
                if (k == 0) {
                    Float weight = ThreadLocalRandom.current().nextFloat();
                    Float value = ThreadLocalRandom.current().nextFloat();
                    p.getInputs().put(weight, value);
                } else {
                    for (Perceptron pp : getGivenLayer(k - 1).values()) {
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        p.getInputs().put(weight, pp.getOutput());
                    }
                }
                temp.put(l, p);
            }
            perceptronMap.put(k, temp);
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

    @Override
    public int compareTo(Brain o) {
        if(getScore()==null){
            return 0;
        }else {
            return getScore().compareTo(o.getScore());
        }
    }
}
