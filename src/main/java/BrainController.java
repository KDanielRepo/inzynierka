import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BrainController {
    private Brain brain;

    public BrainController(){
        brain = new Brain();
        List<Integer> list = new ArrayList<>();
        list.add(16);
        list.add(4);
        brain.createPerceptronMap(2,list);
    }
    public Integer generateMove(){
        Float value = Float.parseFloat(brain.getOutputLayer().values()
                .stream()
                .max(Comparator.naturalOrder())
                .toString()
                .replaceAll("[\\[\\]a-z,A-Z]",""));
        Integer move = Integer.parseInt(brain.getOutputLayer()
                .entries()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .toString()
                .replaceAll("[\\[\\]a-z,A-Z]",""));
        return move;
    }
    public void setCurrentInputs(Integer[][] matrix){
        List<Integer> list = new ArrayList<>();
        for(Integer[] i : matrix){
            list.addAll(Arrays.asList(i));
        }
        int i = 0;
        for(Perceptron p : brain.getGivenLayer(0).values()){
            p.replacePerceptronValue(0,list.get(i).floatValue());
            i++;
        }
    }

    public Brain getBrain() {
        return brain;
    }
    public void setBrain(Brain brain) {
        this.brain = brain;
    }

}
