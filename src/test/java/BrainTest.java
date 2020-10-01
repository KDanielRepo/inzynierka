import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BrainTest {

    @Test
    public void getGivenPerceptronTest(){
        Brain brain = createBrain();
        //System.out.println(brain.getPerceptronCount());
        Perceptron p1 = brain.getGivenPerceptron(7);
        Perceptron p2 = Iterables.get(brain.getPerceptronMap().get(0).stream().findFirst().get().values(),7);
        Assert.assertEquals(p1,p2);
    }

    @Test
    public void getOutputLayerTest(){
        Brain brain = createBrain();
        brain.createDefaultPerceptronMap();

        Multimap<Integer,Float> a = brain.getOutputLayer();
        System.out.println(a);
    }

    @Test
    public void replacePerceptronValue(){
        Brain brain = createBrain();
        brain.createDefaultPerceptronMap();
        int size = 0;
        for (int i = 0; i < brain.getPerceptronMap().size(); i++) {
            for (int j = 0; j <brain.getGivenLayer(i).size() ; j++) {
                brain.getGivenPerceptron(j+size).replacePerceptronValue(5,4f);
            }
            size+=brain.getGivenLayer(i).size();
        }
        size = 0;
        for (int i = 0; i < brain.getPerceptronMap().size(); i++) {
            for (int j = 0; j <brain.getGivenLayer(i).size() ; j++) {
                System.out.println(brain.getGivenPerceptron(j+size).getInput(5));
            }
            size+=brain.getGivenLayer(i).size();
        }
        System.out.println(brain);

    }

    @Test
    public void replaceGivenPerceptronTest(){
        Brain brain = createBrain();
        Perceptron p1 = new Perceptron();
        p1.getInputs().put(1f,2f);

        brain.replaceGivenPerceptron(0,p1);
        Assert.assertEquals(brain.getGivenPerceptron(0).getInputs(),p1.getInputs());
    }

    @Test
    public void replaceGivenWeightByIndexTest(){
        Brain brain = new Brain();
        brain.createDefaultPerceptronMap();
        int size = 0;
        for (int i = 0; i < brain.getPerceptronMap().values().size(); i++) {
            //System.out.println(brain.getPerceptronMap().values().size());
            //System.out.println(brain.getGivenLayer(i).values().size());
            System.out.println("-----");
            for (int j = 0; j < brain.getGivenLayer(i).values().size(); j++) {
                System.out.println(j + size);
                System.out.println(brain.getGivenPerceptron(j + (i * brain.getPerceptronMap().values().size())).getInputs().size());
            }
            size += brain.getGivenLayer(i).values().size();
        }
    }

    @Test
    public void updatePerceptronValuesTest(){
        Brain brain = createBrain();
        System.out.println(brain.getOutputLayer().values());
        brain.getGivenPerceptron(0).replacePerceptronValue(0,15f);
        long start = System.nanoTime();
        brain.updatePerceptronValues();
        long end = System.nanoTime();
        System.out.println("wynik: "+(end-start)*0.000000001);
        System.out.println(brain.getOutputLayer().values());
    }

    private Brain createBrain(){
        Brain brain = new Brain();
        brain.createDefaultPerceptronMap();
        return brain;
    }
}
