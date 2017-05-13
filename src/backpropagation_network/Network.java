package backpropagation_network;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Network {
    private double epsilon;
    private double learningRate;
    private double momentum;
    private double minErrorCondition;

    private List<Neuron> inputLayer;
    private List<Neuron> hiddenLayer;
    private List<Neuron> outputLayer;

    private List<List<Double>> outputs = new ArrayList<>();

    Network(NetworkBuilder networkBuilder) {
        this.epsilon = networkBuilder.epsilon;
        this.learningRate = networkBuilder.learningRate;
        this.momentum = networkBuilder.momentum;
        this.minErrorCondition = networkBuilder.randomWeightMultiplier;

        this.inputLayer = networkBuilder.inputLayer;
        this.hiddenLayer = networkBuilder.hiddenLayer;
        this.outputLayer = networkBuilder.outputLayer;
    }

    public void learn(List<List<Double>> inputs, List<List<Double>> expectedOutputs, int maxEras) {
        double error = 1;

        for (int i = 0; i < maxEras || error > minErrorCondition; i++) {
            error = 0;

            for (int j = 0; j < inputs.size(); j++) { // for each data to learn
                setInputLayerOutputs(inputs.get(j));

                activate();

                if(outputs.size() <= j) {
                    outputs.add(j, outputLayer.stream().mapToDouble(Neuron::getOutput).boxed().collect(Collectors.toList()));
                } else {
                    outputs.set(j, outputLayer.stream().mapToDouble(Neuron::getOutput).boxed().collect(Collectors.toList()));
                }

                for (int k = 0; k < outputs.get(j).size(); k++) {
                    error += Math.pow(outputs.get(j).get(k) - expectedOutputs.get(j).get(k), 2);
                }

                applyBackpropagation(outputs.get(j));
            }
        }

        System.out.println("NN example with xor training");
        for (int p = 0; p < inputs.size(); p++) {
            System.out.print("INPUTS: ");
            for (int x = 0; x < inputs.get(p).size(); x++) {
                System.out.print(inputs.get(p).get(x) + " ");
            }

            System.out.print("EXPECTED: ");
            for (int x = 0; x < outputLayer.size(); x++) {
                System.out.print(expectedOutputs.get(p).get(x) + " ");
            }

            System.out.print("ACTUAL: ");
            for (int x = 0; x < outputLayer.size(); x++) {
                System.out.print(outputs.get(p).get(x) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void setInputLayerOutputs(List<Double> inputs) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setOutput(inputs.get(i));
        }
    }

    private void activate() {
        hiddenLayer.forEach(Neuron::calculateOutput);
        outputLayer.forEach(Neuron::calculateOutput);
    }

    private void applyBackpropagation(List<Double> expectedOutput) {

        // normalization
        expectedOutput = expectedOutput.stream().map(outputs -> {
            if (outputs < 0) {
                return 0 + epsilon;
            } else if (outputs > 1) {
                return 0 - epsilon;
            } else {
                return outputs;
            }
        }).collect(Collectors.toList());

        int i = 0;

        for (Neuron neuron : outputLayer) {
            List<Connection> connections = neuron.getConnections();

            for (Connection connection : connections) {
                double ak = neuron.getOutput();
                double ai = connection.getLeftNeuron().getOutput();
                double desiredOutput = expectedOutput.get(i);

                double partialDerivative = -ak * (1 - ak) * ai * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = connection.getConnectionWeight() + deltaWeight;

                connection.setDeltaWeight(deltaWeight);
                connection.setConnectionWeight(newWeight + momentum * connection.getPreviousDeltaWeight());
            }

            i++;
        }

        final List<Double> expectedOutputs = expectedOutput;

        // update weights for the hidden layer
        hiddenLayer.forEach(neuron -> neuron.getConnections().forEach(connection -> {
            double aj = neuron.getOutput();
            double ai = connection.getLeftNeuron().getOutput();
            double sumKoutputs = 0;

            int j = 0;

            for(Neuron outNeuron : outputLayer) {
                double wjk = outNeuron.getConnection(neuron.getId()).getConnectionWeight();
                double ak = outNeuron.getOutput();

                sumKoutputs += (-(expectedOutputs.get(j) - ak) * ak * (1 - ak) * wjk);
            }

            double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
            double deltaWeight = -learningRate * partialDerivative;
            double newWeight = connection.getConnectionWeight() - deltaWeight;
            connection.setDeltaWeight(deltaWeight);
            connection.setConnectionWeight(newWeight + momentum + connection.getPreviousDeltaWeight());
        }));
    }
}
