
public class Test {
    public static void main(String[] args) {
        Perceptron p = new Perceptron();
        p.getInputs().put(-0.5f,4f);

        Perceptron p1 = new Perceptron();
        p1.getInputs().put(-0.3f,8f);

        Perceptron p2 = new Perceptron();
        p2.getInputs().put(-0.5f,p.getOutput());
        p2.getInputs().put(0.5f,p1.getOutput());
        System.out.println(p2.activation());
    }
}
