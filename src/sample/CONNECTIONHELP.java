package sample;

/**
 * Created by Tomek on 09.05.2017.
 */
public class CONNECTIONHELP {
    double weight = 0;
    double prevDeltaWeight = 0; // for momentum
    double deltaWeight = 0;

    final NEURONHELP leftNeuron;
    final NEURONHELP rightNeuron;
    static int counter = 0;
    final public int id; // auto increment, starts at 0

    public CONNECTIONHELP(NEURONHELP fromN, NEURONHELP toN) {
        leftNeuron = fromN;
        rightNeuron = toN;
        id = counter;
        counter++;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double w) {
        weight = w;
    }

    public void setDeltaWeight(double w) {
        prevDeltaWeight = deltaWeight;
        deltaWeight = w;
    }

    public double getPrevDeltaWeight() {
        return prevDeltaWeight;
    }

    public NEURONHELP getFromNeuron() {
        return leftNeuron;
    }

    public NEURONHELP getToNeuron() {
        return rightNeuron;
    }
}
