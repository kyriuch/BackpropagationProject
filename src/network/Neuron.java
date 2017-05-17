package network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Neuron {

    private int id;
    private static int idCounter = 0;

    private double output;
    private Map<Integer, Neuron> leftNeurons = new HashMap<>();
    private Map<Integer, Double> weights = new HashMap<>();
    private Map<Integer, Double> weightsDiffs = new HashMap<>();

    private double biasWeight;
    private double biasDiff;

    private double signalError;

    private static Random random = new Random();

    Neuron(List<Neuron> leftNeurons) {
        id = idCounter++;

        System.out.println("Created Neuron with " + id + " id");

        leftNeurons.forEach(this::fillMaps);
        biasWeight = random.nextDouble() * 2 - 1;
        biasDiff = 0.0;
    }

    Neuron() {
        id = idCounter++;
        System.out.println("Created Neuron with " + id + " id");
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    void sumWeights() {
        output = weights.entrySet()
                .stream()
                .mapToDouble(value -> value.getValue() * leftNeurons.get(value.getKey()).getOutput())
                .sum();

        output += biasWeight;
    }

    void activate() {
        output = 1.0 / (1 + Math.exp(-output));
    }

    public int getId() {
        return id;
    }

    private void fillMaps(Neuron neuron) {
        leftNeurons.put(neuron.getId(), neuron);
        weights.put(neuron.getId(), random.nextDouble() * 2 - 1);
        weightsDiffs.put(neuron.getId(), 0.0);
    }

    public double getSignalError() {
        return signalError;
    }

    public void setSignalError(double signalError) {
        this.signalError = signalError;
    }

    public double getWeight(Neuron leftNeuron) {
        return weights.get(leftNeuron.getId());
    }

    public void setWeight(Neuron leftNeuron, double weight) {
        weights.put(leftNeuron.getId(), weight);
    }

    public double getWeightDiff(Neuron leftNeuron) {
        return weightsDiffs.get(leftNeuron.getId());
    }

    public void setWeightDiff(Neuron leftNeuron, double diff) {
        weightsDiffs.put(leftNeuron.getId(), diff);
    }

    public double getBiasWeight() {
        return biasWeight;
    }

    public void setBiasWeight(double biasWeight) {
        this.biasWeight = biasWeight;
    }

    public double getBiasDiff() {
        return biasDiff;
    }

    public void setBiasDiff(double biasDiff) {
        this.biasDiff = biasDiff;
    }
}
