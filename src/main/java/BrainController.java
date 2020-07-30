import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BrainController {
    private Brain brain;
    private List<Integer> blocks;
    private Integer currentMove;

    public BrainController(){
        brain = new Brain();
        List<Integer> list = new ArrayList<>();
        blocks = new ArrayList<>();
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
    public Integer generateMoveWithoutBlocks(){
        List<Float> floats = new ArrayList<>(brain.getOutputLayer().values());
        floats.sort(Comparator.naturalOrder());

        Integer move = -1;
        for (int j = floats.size()-1; j > 0; j--) {
            int finalJ = j;
            move = Integer.parseInt(brain.getOutputLayer()
                    .entries()
                    .stream()
                    .filter(entry -> floats.get(finalJ).equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .toString()
                    .replaceAll("[\\[\\]a-z,A-Z]",""));
            for (Integer anI : blocks) {
                if (move == anI) {
                    floats.remove(j);
                }
            }
        }
        move = Integer.parseInt(brain.getOutputLayer()
                .entries()
                .stream()
                .filter(entry -> floats.get(floats.size()-1).equals(entry.getValue()))
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

    public Integer getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Integer currentMove) {
        this.currentMove = currentMove;
    }

    public void addBlock(int block){
        blocks.add(block);
    }

    public boolean isNotBlocked(){
        return blocks.size()==0;
    }

    public List<Integer> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Integer> blocks) {
        this.blocks = blocks;
    }
}
