import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/*@XmlRootElement(name = "Perceptron")
@XmlAccessorType(XmlAccessType.FIELD)*/
public class Perceptron {
    private Multimap<Float, Float> inputs;
    private Float output;
    private Float sum;
    private int layer;
    private final Float lambda = 1.0507f;
    private final Float alpha = 1.6732f;
    //private Mutex mutex;

    public Perceptron() {
        inputs = HashMultimap.create();
        //mutex = new Mutex();
        sum = 0f;
    }

    public Float activation() {
        sum = 0f;
        if (layer == 0) {
            inputs.entries().forEach(entry -> sum += (entry.getKey() * (log2(entry.getValue()))));
        } else {
            inputs.entries().forEach(entry -> sum += (entry.getKey() * (normalize(entry.getValue()))));
        }
        if (sum < 0) {
            Double a = (alpha * Math.exp(sum) - alpha) * lambda;
            return output = a.floatValue();
        } else {
            return output = sum * lambda;
        }
    }

    public int log2(Float value) {
        if (value == 0f) {
            return 0;
        }
        Double a = (Math.log(value) / Math.log(2) + 1e-10);
        return a.intValue();
    }

    //TODO: Ale zaraz przeca cos rozwale, dlaczego tutaj rzuca ten ConcurrentModificationException...

    public Float calculateSum() {
        sum = 0f;
        if (layer == 0) {
            inputs.entries().forEach(entry -> sum += (entry.getKey() * (log2(entry.getValue()))));
        } else {
            inputs.entries().forEach(entry -> sum += (entry.getKey() * (normalize(entry.getValue()))));
        }
        return sum;
    }

    public Float normalize(Float value) {
        return ((value - 1) / (200 - 1)) * (1 - 0) + 0;
    }

    public void replacePerceptronValue(int index, float value) {
        float key = Iterables.get(inputs.keys(), index);
        inputs.replaceValues(key, Arrays.asList(value));
        Multimap<Float, Float> tempMap = HashMultimap.create();
        inputs.entries().forEach(e -> tempMap.put(e.getKey(), e.getValue()));
        tempMap.replaceValues(key, Arrays.asList(value));
        setInputs(tempMap);
        //getInputs().replaceValues(Iterables.get(getInputs().keys(), index),Arrays.asList(value));
        /*Multimap<Float, Float> tempMap = HashMultimap.create();
        for (int i = 0; i < inputs.size(); i++) {
            if (i != index) {
                tempMap.put(Iterables.get(inputs.keys(), i), Iterables.get(inputs.values(), i));
            } else {
                tempMap.put(Iterables.get(inputs.keys(), i), value);
            }
        }
        if (output == Iterables.get(inputs.values(), 0)) {
            output = Iterables.get(tempMap.values(), 0);
        }
        setInputs(tempMap);*/
    }

    public void replacePerceptronWeight(int index, float weight) {
        Multimap<Float, Float> tempMap = HashMultimap.create();
        for (int i = 0; i < inputs.size(); i++) {
            if (i != index) {
                tempMap.put(Iterables.get(inputs.keys(), i), Iterables.get(inputs.values(), i));
            } else {
                tempMap.put(weight, Iterables.get(inputs.values(), i));
            }

        }
        if (output == Iterables.get(inputs.values(), 0)) {
            output = Iterables.get(tempMap.values(), 0);
        }
        setInputs(tempMap);
    }

    public Float getOutput() {
        sum = 0f;
        if (layer == 0) {
            inputs.entries().forEach(entry -> sum += (entry.getKey() * (log2(entry.getValue()))));
        } else {
            inputs.entries().forEach(entry -> sum += (entry.getKey() * (normalize(entry.getValue()))));
        }
        if (sum < 0) {
            Double a = (alpha * Math.exp(sum) - alpha) * lambda;
            output = a.floatValue();
        } else {
            output = sum * lambda;
        }
        return output;
    }

    public void setOutput(Float output) {
        this.output = output;
    }


    public Float getInput(Integer i) {

        return Iterables.get(inputs.values(), i);

    }

    /*public Float getInput(Integer i) {
        try {
            mutex.lock();
            return Iterables.get(inputs.values(), i);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return null;
    }*/


    public Multimap<Float, Float> getInputs() {
        return inputs;

    }

    /*public Multimap<Float, Float> getInputs() {
        try {
            mutex.lock();
            return inputs;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return null;
    }*/


    public void setInputs(Multimap<Float, Float> inputs) {

        this.inputs = inputs;

    }

    /*public void setInputs(Multimap<Float, Float> inputs) {
        try {
            mutex.lock();
            this.inputs = inputs;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }*/

    public void createInputs(List<Float> weights, List<Float> values) {
        for (int i = 0; i < weights.size(); i++) {
            inputs.put(weights.get(i), values.get(i));
        }
    }

    public Float getSum() {
        return sum;
    }

    public void setSum(Float sum) {
        this.sum = sum;
    }

    public Float getWeight(int index) {
        return Iterables.get(inputs.keys(), index);

    }

    /*public Float getWeight(int index) {
        //System.out.println(getInputs().keys().size());
        try {
            mutex.lock();
            return Iterables.get(inputs.keys(), index);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return null;
    }*/


    public List<Float> getWeights() {
        return inputs.keys().stream().map(Float::floatValue).collect(Collectors.toList());
    }

    /*public List<Float> getWeights() {
        try {
            mutex.lock();
            return inputs.keys().stream().map(Float::floatValue).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return null;
    }*/

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

}