import com.google.common.collect.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/*@XmlRootElement(name = "Brain")
@XmlAccessorType(XmlAccessType.FIELD)*/
public class Brain implements Comparable<Brain> {
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
        Multimap<Integer, Perceptron> a = getPerceptronMap().get(layer).stream().findFirst().get();
        return a;
        //return Iterables.get(getPerceptronMap().values(), layer);
    }

    public Multimap<Integer, Float> getOutputLayer() {
        /*Multimap<Integer, Float> out = HashMultimap.create();
        for (int i = 0; i < getGivenLayer(getPerceptronMap().size() - 1).values().size(); i++) {
            Perceptron p = Iterables.get(getGivenLayer(getPerceptronMap().size() - 1).values(), i);
            out.put(i, p.activation());
        }*/
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
        int tempIndex = index;  //8,8,8,4; 23
        if (index != 0 && index != getPerceptronCount()) {
            for (int i = 0; i < getPerceptronMap().values().size(); i++) {
                for (int j = 0; j < getGivenLayer(i).values().size(); j++) {
                    if (tempIndex == 0) {
                        return getGivenLayer(i).get(j).stream().findFirst().get();
                    }
                    tempIndex--;
                }
            }
        } else if (index == 0) {
            return getGivenLayer(0).get(0).stream().findFirst().get();
        } else {
            return getGivenLayer(getPerceptronMap().size()).get(getGivenLayer(getPerceptronMap().size()).size()).stream().findFirst().get();
        }
        return null;
    }

    //TODO: napisac lepsza implementacje tego
    public void replaceGivenWeightByIndex(int index, Float weightValue) {
        AtomicInteger tempIndex = new AtomicInteger(index);
        getPerceptronMap().values().stream().forEach(l->{
            l.values().stream().forEach(p->{
                AtomicInteger indexx = new AtomicInteger(0);
                p.getInputs().keys().forEach(k->{
                    if (tempIndex.get() == 0) {
                        p.replacePerceptronWeight(indexx.get(),weightValue);
                        return;
                    }
                    tempIndex.getAndDecrement();
                    indexx.getAndIncrement();
                });
            });
        });
        /*int tempIndex = index;
        int tempMult = 0;
        if (index != 0 && index > getGivenLayer(0).values().size()) {
            for (int i = 0; i < getPerceptronMap().values().size(); i++) {
                for (int j = 0; j < getGivenLayer(i).values().size(); j++) {
                    for (int k = 0; k < getGivenPerceptron(j + tempMult).getInputs().values().size(); k++) {
                        if (tempIndex == 0) {
                            getGivenPerceptron(j + tempMult).replacePerceptronWeight(k, weightValue);
                        }
                        tempIndex--;
                    }
                }
                tempMult += getGivenLayer(i).values().size();
            }
        } else if (index != 0 && index < getGivenLayer(0).values().size()) {
            for (int i = 0; i < getGivenLayer(0).values().size(); i++) {
                if (tempIndex == 0) {
                    getGivenPerceptron(0).replacePerceptronWeight(i, weightValue);
                }
                tempIndex--;
            }
        } else {
            getGivenPerceptron(0).replacePerceptronWeight(0, weightValue);
        }*/
    }

    //TODO: napisac lepsza implementacje tego
    public Float getGivenWeightByIndex(int index) {
        AtomicInteger tempIndex = new AtomicInteger(index);
        //int tempMult = 0;
        AtomicReference<Float> a = new AtomicReference<>(0f);
        getPerceptronMap().values().stream().forEach(l->{
            l.values().stream().forEach(p->{
                p.getInputs().keys().forEach(k->{
                    if (tempIndex.get() == 0) {
                        a.set(k);
                        return;
                    }
                    tempIndex.getAndDecrement();
                });
            });
        });
        return a.get();
        /*if (index != 0 && index > getGivenLayer(0).values().size()) {
            for (int i = 0; i < getPerceptronMap().values().size(); i++) { //dla kazdej z 5 warstw
                for (int j = 0; j < getGivenLayer(i).values().size(); j++) { //
                    for (int k = 0; k < getGivenPerceptron(j + tempMult).getInputs().values().size(); k++) {
                        if (tempIndex.get() == 0) {
                            return getGivenPerceptron(j + tempMult).getWeight(k);
                        }
                        tempIndex.getAndDecrement();
                    }
                }
                tempMult += getGivenLayer(i).values().size();
            }
        } else if (index != 0 && index < getGivenLayer(0).values().size()) {
            for (int k = 0; k < getGivenPerceptron(0).getInputs().values().size(); k++) {
                if (tempIndex.get() == 0) {
                    return getGivenPerceptron(0).getWeight(k);
                }
                tempIndex.getAndDecrement();
            }
        } else {
            return getGivenPerceptron(0).getWeight(0);
        }*/
        //return 0f;
    }


    public void replaceGivenPerceptron(Integer index, Perceptron p) {
        int temp = 0;
        int tempLayer = 0;
        int tempIndex = 0;
        for (Multimap<Integer, Perceptron> m : getPerceptronMap().values()) {
            if (temp + m.size() - 1 >= index) {
                if (index == temp + m.size() - 1) {
                    temp += m.size() - 1;
                } else {
                    temp += index;
                }
                break;
            } else {
                tempLayer++;
                temp += m.size();
                tempIndex += index - (m.size()) * tempLayer;
            }
        }

        if (tempLayer != 0) {
            for (Perceptron per : getGivenLayer(tempLayer).get(tempIndex)) {
                per.setInputs(p.getInputs());
            }
        } else {
            for (Perceptron per : getGivenLayer(tempLayer).get(temp)) {
                per.setInputs(p.getInputs());
            }
        }
    }

    public void createPerceptronMap(Integer layers, List<Integer> rows) {
        perceptronMap = ArrayListMultimap.create();
        for (int k = 0; k < layers; k++) {//3
            Multimap<Integer, Perceptron> temp = ArrayListMultimap.create();
            for (int l = 0; l < rows.get(k); l++) {
                Perceptron p = new Perceptron();
                if (k == 0) {
                    Float weight = ThreadLocalRandom.current().nextFloat();
                    Float value = 0f;
                    p.getInputs().put(weight, value);
                    p.setLayer(k);
                } else {
                    for (Perceptron pp : getGivenLayer(k - 1).values()) {
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        p.getInputs().put(weight, pp.getOutput());
                        p.setLayer(k);
                    }
                }
                temp.put(l, p);
            }
            perceptronMap.put(k, temp);
        }
    }

    public void updatePerceptronValues() {
        List<Float> outputs = new ArrayList<>();
        int size = 0;
        for (int i = 0; i < getPerceptronMap().size(); i++) {
            if (i > 0) {
                for (int j = 0; j < getGivenLayer(i).size();j++) {
                    outputs.add(getGivenPerceptron(j+size).getOutput());
                }
            }
            size+=getGivenLayer(i).size();
        }
        int iter = 0;
        for (int i = 0; i < getPerceptronMap().values().size(); i++) {
            if (i > 0) {
                int index = 0;
                for (Perceptron p : getGivenLayer(i).values()) {
                    p.replacePerceptronValue(index, outputs.get(iter));
                    index++;
                    iter++;
                }
            }
        }

    }

    public void createDefaultPerceptronMap() {
        List<Integer> rows = Arrays.asList(8, 8, 8, 8, 4);
        perceptronMap = HashMultimap.create();
        for (int k = 0; k < rows.size(); k++) {
            Multimap<Integer, Perceptron> temp = HashMultimap.create();
            for (int l = 0; l < rows.get(k); l++) {
                Perceptron p = new Perceptron();
                if (k == 0) {
                    for (int i = 0; i < 16; i++) {
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        Float value = 0f;
                        p.getInputs().put(weight, value);
                        p.setLayer(k);
                    }
                } else {
                    for (Perceptron pp : getGivenLayer(k - 1).values()) {
                        Float weight = ThreadLocalRandom.current().nextFloat();
                        p.getInputs().put(weight, 0f);
                        p.setLayer(k);
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
        if (getScore() == null) {
            return 0;
        } else {
            return getScore().compareTo(o.getScore());
        }
    }

}
