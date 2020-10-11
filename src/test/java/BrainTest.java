import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class BrainTest {

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
    public void getOutputLayerTest(){
        Brain brain = createBrain();
        Assert.assertTrue(brain.getOutputLayer().size() > 0);
    }

    @Test
    public void replaceGivenPerceptronTest(){
        /*Brain brain = createBrain();
        Perceptron p1 = new Perceptron();
        p1.getInputs().put(1f,2f);

        brain.replaceGivenPerceptron(0,p1);
        Assert.assertEquals(brain.getGivenPerceptron(0).getInputs(),p1.getInputs());*/
    }

    @Test
    public void replaceGivenWeightByIndexTest(){
        Brain brain = createBrain();
        Float weightBefore = brain.getGivenWeightByIndex(300);
        brain.replaceGivenWeightByIndex(300,3f);
        Float weightAfter = brain.getGivenWeightByIndex(300);
        Assert.assertNotEquals(weightAfter,weightBefore);

    }

    @Test
    public void getGivenWeightByIndex(){
        Brain brain = createBrain();
        Float a = brain.getGivenWeightByIndex(17);
        System.out.println(a);
    }

    @Test
    public void updatePerceptronValuesTest(){
        Brain brain = createBrain();
        System.out.println(brain.getOutputLayer());
        brain.getGivenPerceptron(0).replacePerceptronValue(0,15f);
        brain.updatePerceptronValues();
        System.out.println(brain.getOutputLayer());
    }

    @Test
    public void dlaSuleczka(){
        String a = "Test, to jest tylko test";
        String b = "Test to też";
        String c = "Nie ma tu testu";
        String d = "Cholipka ile tych testów";

        a += " "+b+" "+c+" "+d;
        System.out.println(a);
        List<String> lista = Arrays.asList(a.split("[\\s,]"));
        List<String> bezPowtorek = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            if(!bezPowtorek.contains(lista.get(i)) && !lista.get(i).isEmpty()){
                bezPowtorek.add(lista.get(i));
            }
        }
        for (int i = 0; i < bezPowtorek.size(); i++) {
            System.out.println(bezPowtorek.get(i));
        }
    }

    private Brain createBrain(){
        Brain brain = new Brain();
        brain.createDefaultPerceptronMap();
        return brain;
    }
}
