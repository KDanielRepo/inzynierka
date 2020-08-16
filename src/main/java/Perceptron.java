import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Perceptron {
    private Multimap<Float, Float> inputs;
    private Float output;
    private Float sum;

    public Perceptron() {
        inputs = HashMultimap.create();
        sum = 0f;
    }

    public Float activation() {
        //Float out = Math.max(0, calculateSum());
        Float lambda = 1.0507f;
        Float alpha = 1.6732f;
        calculateSum();
        if(sum<0){
            Double a = (alpha*Math.exp(sum)-alpha)*lambda;
            return output = a.floatValue();
        }else{
            return output = sum * lambda;
        }
    }

    public int log2(float value){
        return (int) (Math.log(value)/Math.log(2)+1e-10);
    }

    public Float calculateSum() {
        sum = 0f;
        inputs.entries().forEach(entry -> sum += entry.getKey() * (log2(entry.getValue())*0.01f));
        System.out.println(sum);
        return sum;
    }

    public Float getOutput() {
        if (output == null) {
            activation();
        }
        return output;
    }

    public void setOutput(Float output) {
        this.output = output;
    }

    //To musi zwracac mape <F,F>
    /*public Map<Float,Float> getOutput(Integer i){
        Map<Float,Float> out = new HashMap<>();
        out.put(Iterables.get(outputs.keys(),i),Iterables.get(outputs.values(),i));
        return out;
    }*/
    public Float getInput(Integer i) {
        return Iterables.get(inputs.values(), i);
    }

    public Multimap<Float, Float> getInputs() {
        return inputs;
    }

    public void setInputs(Multimap<Float, Float> inputs) {
        this.inputs = inputs;
    }

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

    public List<Float> getWeights() {
        return inputs.keys().stream().map(Float::floatValue).collect(Collectors.toList());
    }

    public void replacePerceptronValue(int index, float value) {
        Multimap<Float, Float> tempMap = HashMultimap.create();
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
        inputs = tempMap;
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
        inputs = tempMap;
    }
}