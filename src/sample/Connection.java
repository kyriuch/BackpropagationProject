package sample;

/**
 * Created by Tomek on 09.05.2017.
 */
public class Connection {
    private double weight = 0;
    private double previousDeltaWeight = 0;
    private double deltaWeight = 0;

    private final SingleNeuron leftNeuron;
    private final SingleNeuron currentNeuron;

    private static int nextId = 0;
    private final int id;

    public Connection(SingleNeuron leftNeuron, SingleNeuron rightNeuron) {
        this.leftNeuron = leftNeuron;
        this.currentNeuron = rightNeuron;
        id = nextId++;
    }

    public void setNewAvgWeight(double newDeltaWeight) {
        previousDeltaWeight = deltaWeight;
        deltaWeight = newDeltaWeight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPreviousDeltaWeight() {
        return previousDeltaWeight;
    }

    public void setPreviousDeltaWeight(double previousDeltaWeight) {
        this.previousDeltaWeight = previousDeltaWeight;
    }

    public double getAvgWeight() {
        return deltaWeight;
    }

    public void setDeltaWeight(double DeltaWeight) {
        this.deltaWeight = deltaWeight;
    }

    public SingleNeuron getLeftNeuron() {
        return leftNeuron;
    }

    public SingleNeuron getCurrentNeuron() {
        return currentNeuron;
    }

    public int getId() {
        return id;
    }
}
