package backpropagation_network;

import java.util.List;
import java.util.Random;

public class NetworkBuilder {
    double epsilon = 0.00000000001;
    double learningRate = 0.9f;
    double momentum = 0.7f;
    double minErrorCondition = 0.001;

    private double randomWeightMultiplier = 1;
    List<Neuron> inputLayer;
    List<Neuron> hiddenLayer;
    List<Neuron> outputLayer;

    private Random random = new Random();

    public Network build(List<Neuron> inputLayer,
                         List<Neuron> hiddenLayers,
                         List<Neuron> outputLayer) {
        this.inputLayer = inputLayer;
        this.hiddenLayer = hiddenLayers;
        this.outputLayer = outputLayer;

        this.hiddenLayer.forEach(this::putRandomWeight);
        this.outputLayer.forEach(this::putRandomWeight);

        return new Network(this);
    }

    private void putRandomWeight(Neuron neuron) {
        neuron.getConnections().forEach(connection -> {
            connection.setConnectionWeight(randomWeightMultiplier * (random.nextDouble() * 2 - 1)); // [-1; 1]
        });
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    public void setRandomWeightMultiplier(double randomWeightMultiplier) {
        this.randomWeightMultiplier = randomWeightMultiplier;
    }

    public void setMinErrorCondition(double minErrorCondition) {
        this.minErrorCondition = minErrorCondition;
    }
}
