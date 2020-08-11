import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PerceptronTest {
    private Perceptron perceptron;
    @Test
    public void perceptronReplaceTests(){
        perceptron = new Perceptron();
        perceptron.getInputs().put(0.4f,13f);
        perceptron.replacePerceptronValue(0,1f);
        perceptron.getInputs().values().stream().forEach(e->{
            System.out.println(e);
        });
    }
    @Test
    public void perceptronActivationTest(){
        perceptron = new Perceptron();
        perceptron.getInputs().put(0.64235f,128f);
        Perceptron perceptron2 = new Perceptron();
        perceptron2.getInputs().put(0.4021f,256f);
        System.out.println(perceptron.activation());
        System.out.println(perceptron2.activation());
    }
    public Perceptron createPerceptron(){
        Perceptron p = new Perceptron();
        return p;
    }
}
