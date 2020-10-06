public class Dendrite {
    private Perceptron out;
    private Perceptron in;
    private Float weight;
    private Float value;

    public Perceptron getOut() {
        return out;
    }

    public void setOut(Perceptron out) {
        this.out = out;
    }

    public Perceptron getIn() {
        return in;
    }

    public void setIn(Perceptron in) {
        this.in = in;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
