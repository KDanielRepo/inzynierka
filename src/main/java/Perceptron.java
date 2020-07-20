import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Perceptron {
    private Multimap<Float,Float> inputs;
    private Float output;
    private Float sum;

    public Perceptron(){
        inputs = HashMultimap.create();
        sum = 0f;
    }

    public Float activation(){
        Double out = (1-Math.exp(-calculateSum()))/(1+Math.exp(-calculateSum()));
        System.out.println("out to: "+out);
        if(out<0){
            output = -1f;
            return -1f;
        }else {
            output = 1f;
            return 1f;
        }
    }

    public Float calculateSum(){
        sum = 0f;
        inputs.entries().forEach(entry -> sum += entry.getKey()*entry.getValue());
        return sum;
    }

    public Float getOutput() {
        if(output == null){
            output = Iterables.get(inputs.values(),0);
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
    public Float getInput(Integer i){
        return Iterables.get(inputs.values(),i);
    }

    public Multimap<Float, Float> getInputs() {
        return inputs;
    }

    public void setInputs(Multimap<Float, Float> inputs) {
        this.inputs = inputs;
    }

    /*public Multimap<Float, Float> getOutputs() {
        if(outputs.values().isEmpty()){
            outputs = inputs;
        }
        return outputs;
    }*/

    /*public void setOutputs(Multimap<Float, Float> outputs) {
        this.outputs = outputs;
    }*/

    public Float getSum() {
        return sum;
    }

    public void setSum(Float sum) {
        this.sum = sum;
    }

    public List<Float> getWeights(){
        return inputs.keys().stream().map(Float::floatValue).collect(Collectors.toList());
    }

    public void replacePerceptronValue(int index,float value){
        Multimap<Float,Float> tempMap = HashMultimap.create();
        for (int i = 0; i < inputs.size(); i++) {
            if(i!=index){
                tempMap.put(Iterables.get(inputs.keys(),i),Iterables.get(inputs.values(),i));
            }else{
                tempMap.put(Iterables.get(inputs.keys(),i),value);
            }

        }
        if(output==Iterables.get(inputs.values(),0)){
            output=Iterables.get(tempMap.values(),0);
        }
        inputs = tempMap;
    }
    public void replacePerceptronWeight(int index,float weight){
        Multimap<Float,Float> tempMap = HashMultimap.create();
        for (int i = 0; i < inputs.size(); i++) {
            if(i!=index){
                tempMap.put(Iterables.get(inputs.keys(),i),Iterables.get(inputs.values(),i));
            }else{
                tempMap.put(weight,Iterables.get(inputs.values(),i));
            }

        }
        if(output==Iterables.get(inputs.values(),0)){
            output=Iterables.get(tempMap.values(),0);
        }
        inputs = tempMap;
    }
}

/*

    public void setInputs(Float...floats){
        inputs.addAll(Arrays.asList(floats));
    }

    public void setOutputs(Float...floats){
        outputs.addAll(Arrays.asList(floats));
    }

    public void setInputWeights(Float...floats){
        inputWeights.addAll(Arrays.asList(floats));
    }

    public void setOutputWeights(Float...floats){
        outputWeights.addAll(Arrays.asList(floats));
    }*/
