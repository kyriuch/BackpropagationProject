package network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Neuron {

    private int id; // identyfikator neuronu służący do mapowania
    private static int idCounter = 0;

    private double output; // wyjście pojedynczego neuronu
    private Map<Integer, Neuron> leftNeurons = new HashMap<>(); // połączenia neuronu
    private Map<Integer, Double> weights = new HashMap<>(); // wagi połączeń
    private Map<Integer, Double> weightsDiffs = new HashMap<>(); // zapamiętane różnice wag

    private double biasWeight; // bias neuron
    private double biasDiff; // róznica wagi bias neuronu

    private double signalError; // błąd pojedynczego neuronu

    private static Random random = new Random();

    Neuron(List<Neuron> leftNeurons) { // utworzenie neuronu wraz z losową wagą dla każdego z połączeń
        id = idCounter++;

        System.out.println("Created Neuron with " + id + " id");

        leftNeurons.forEach(this::fillMaps);
        biasWeight = random.nextDouble() * 2 - 1;
        biasDiff = 0.0;
    }

    Neuron() { // utworzenie neuronu warstwy wejściowej
        id = idCounter++;
        System.out.println("Created Neuron with " + id + " id");
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    void sumWeights() { // suma iloczynu wag połączeń z ich wyjściami + bias neuron
        output = weights.entrySet()
                .stream()
                .mapToDouble(value -> value.getValue() * leftNeurons.get(value.getKey()).getOutput())
                .sum();

        output += biasWeight;
    }

    void activate() {
        output = 1.0 / (1 + Math.exp(-output));
    } // funkcja aktywacji (funkcja sigmoidalna)

    public int getId() {
        return id;
    }

    private void fillMaps(Neuron neuron) { // uzupełnienie mapy połączeń wraz z losowymi wagami
        leftNeurons.put(neuron.getId(), neuron);
        weights.put(neuron.getId(), random.nextDouble() * 2 - 1); // wagi od -1.0 do 1.0
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
