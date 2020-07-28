import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PerceptronTest {
    private Perceptron perceptron;
    @Test
    public void perceptronTests(){
        perceptron = new Perceptron();
        perceptron.getInputs().put(0.4f,13f);
        perceptron.replacePerceptronValue(0,1f);
        perceptron.getInputs().values().stream().forEach(e->{
            System.out.println(e);
        });
    }
    public Perceptron createPerceptron(){
        Perceptron p = new Perceptron();
        return p;
    }
}
