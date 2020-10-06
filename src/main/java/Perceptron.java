import java.util.*;
import java.util.stream.Collectors;

public class Perceptron {
    private List<Dendrite> inputs;
    private Dendrite output;
    private Float sum;
    private int layer;
    private final Float lambda = 1.0507f;
    private final Float alpha = 1.6732f;

    public Perceptron() {
        inputs = new ArrayList<>();
        output = new Dendrite();
        sum = 0f;
    }

    public void activation() {
        sum = 0f;
        if (layer == 0) {
            inputs.forEach(entry-> sum += entry.getWeight() * (log2(entry.getValue())));
        } else {
            inputs.forEach(entry-> sum += entry.getWeight() * (normalize(entry.getValue())));
        }
        if (sum < 0) {
            Double a = (alpha * Math.exp(sum) - alpha) * lambda;
            output.setValue(a.floatValue());
        } else {
            output.setValue(sum * lambda);
        }
    }

    public int log2(Float value) {
        if (value == 0f) {
            return 0;
        }
        Double a = (Math.log(value) / Math.log(2) + 1e-10);
        return a.intValue();
    }

    public Float calculateSum() {
        sum = 0f;
        if (layer == 0) {
            inputs.forEach(input-> sum += input.getWeight() * log2(input.getValue()));
        } else {
            inputs.forEach(input-> sum += input.getWeight() * normalize(input.getValue()));
        }
        return sum;
    }

    public Float normalize(Float value) {
        return ((value - 1) / (200 - 1)) * (1 - 0) + 0;
    }

    public void replacePerceptronValues(List<Float> values) {
        for (int i = 0; i < values.size(); i++) {
            inputs.get(i).setValue(values.get(i));
        }
    }

    public void replacePerceptronValue(int index, Float value) {
        inputs.get(index).setValue(value);
    }

    public void replacePerceptronWeight(int index, float weight) {
        inputs.get(index).setWeight(weight);
    }

    public void replacePerceptronWeights(List<Float> weights) {
        for (int i = 0; i < weights.size(); i++) {
            inputs.get(i).setWeight(weights.get(i));
        }
    }

    public Float getOutput() {
        activation();
        return output.getValue();
    }

    public Float getInput(Integer i) {
        return inputs.get(i).getValue();
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


    public Float getWeight(int index) {
        return inputs.get(index).getWeight();
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
        return inputs.stream().map(Dendrite::getWeight).collect(Collectors.toList());
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

    public List<Dendrite> getInputs() {
        return inputs;
    }

    public void setInputs(List<Dendrite> inputs) {
        this.inputs = inputs;
    }

    public void setOutput(Dendrite output) {
        this.output = output;
    }
}