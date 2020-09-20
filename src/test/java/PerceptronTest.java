import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PerceptronTest {

    @Test
    public void replacePerceptronValueTest(){
        Perceptron p1 = createPerceptron();
        p1.replacePerceptronValue(0,4f);

        Assert.assertEquals(p1.getInput(0),new Float(4));
    }

    @Test
    public void replacePerceptronWeightTest(){
        Perceptron p1 = createPerceptron();
        p1.replacePerceptronWeight(0,5f);

        Assert.assertEquals(p1.getWeight(0),new Float(5));
    }

    private Perceptron createPerceptron(){
        Perceptron p = new Perceptron();
        p.getInputs().put(1f,1f);
        p.getInputs().put(2f,2f);
        p.getInputs().put(3f,3f);
        return p;
    }
}
