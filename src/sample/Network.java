package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tomek on 09.05.2017.
 */
public class Network implements Runnable {

    private final List<SingleNeuron> inputLayer = new ArrayList<>();
    private final List<SingleNeuron> hiddenLayer = new ArrayList<>();
    private final List<SingleNeuron> outputLayer = new ArrayList<>();
    private final SingleNeuron biasNeuron = new SingleNeuron();

    private final int inputLayerNeuronsNumber = 2;
    private final int hiddenLayerNeuronsNumber = 4;
    private final int outputLayerNeuronsNumber = 1;

    private final int weightMultiplier = 1;
    private final double epsilon = 0.00000000001;
    private final double learningRate = 0.9f;
    private final double momentum = 0.7f;

    private final double[][] egInputs = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
    private final double[][] expectedOutputs = {{0}, {1}, {1}, {0}};
    private double[][] resultOutputs = {{-1}, {-1}, {-1}, {-1}};
    private double[] output;


    @Override
    public void run() {
        for (int i = 0; i < inputLayerNeuronsNumber; i++) { // fill input layer with neurons
            inputLayer.add(new SingleNeuron());
        }

        for (int i = 0; i < hiddenLayerNeuronsNumber; i++) { // fill hidden layer with neurons
            SingleNeuron singleNeuron = new SingleNeuron();
            singleNeuron.addLeftNeuronsConnections(inputLayer);
            singleNeuron.addBiasConnection(biasNeuron);
            hiddenLayer.add(singleNeuron);
        }

        for (int i = 0; i < outputLayerNeuronsNumber; i++) { // fill output layer with neurons
            SingleNeuron singleNeuron = new SingleNeuron();
            singleNeuron.addLeftNeuronsConnections(hiddenLayer);
            singleNeuron.addBiasConnection(biasNeuron);
            outputLayer.add(singleNeuron);
        }

        Random rand = new Random();

        // init random weights
        hiddenLayer.forEach(neuron -> {
            List<Connection> connections = neuron.getNeuronConnections();

            connections.forEach(connection -> {
                connection.setWeight(weightMultiplier * (rand.nextDouble() * 2 - 1));
                System.out.println("Waga ukryta: " + connection.getWeight());
            });
        });

        outputLayer.forEach(neuron -> {
            List<Connection> connections = neuron.getNeuronConnections();

            connections.forEach(connection -> {
                connection.setWeight(weightMultiplier * (rand.nextDouble() * 2 - 1));
                System.out.println("Waga wyjsciowa: " + connection.getWeight());
            });
        });

        startWorking(5000, 0.0001);
    }

    private void startWorking(int eras, double minError) {
        double currentError = 1;

        for (int i = 0; i < eras && currentError > minError; i++) {
            currentError = 0;

            for (int j = 0; j < egInputs.length; j++) {
                for (int k = 0; k < inputLayerNeuronsNumber; k++) {
                    inputLayer.get(k).setOutput(egInputs[j][k]);
                }

                hiddenLayer.forEach(SingleNeuron::calcOutput);
                outputLayer.forEach(SingleNeuron::calcOutput);

                double[] outputs = new double[outputLayerNeuronsNumber];

                for (int k = 0; k < outputLayerNeuronsNumber; k++) {
                    outputs[k] = outputLayer.get(k).getOutput();
                }

                output = outputs;
                resultOutputs[j] = output;

                for (int k = 0; k < expectedOutputs[j].length; k++) {
                    currentError += Math.pow(output[k] - expectedOutputs[j][k], 2);
                }

                backpropagation(expectedOutputs[j]);
            }
        }

        for (int i = 0; i < egInputs.length; i++) {
            System.out.println("INPUTS:");

            for (int j = 0; j < inputLayerNeuronsNumber; j++) {
                System.out.println(egInputs[i][j] + " ");
            }

            System.out.print("EXPECTED: ");
            for (int j = 0; j < outputLayerNeuronsNumber; j++) {
                System.out.print(expectedOutputs[i][j] + " ");
            }

            System.out.print("ACTUAL: ");
            for (int j = 0; j < outputLayerNeuronsNumber; j++) {
                System.out.print(resultOutputs[i][j] + " ");
            }
        }
    }

    private void backpropagation(double expectedOutput[]) {
        for (int i = 0; i < expectedOutput.length; i++) {
            double d = expectedOutput[i];

            if (d < 0 || d > 1) {
                System.out.println("tu dochodze lol");

                if (d < 0) {
                    expectedOutput[i] = 0 + epsilon;
                } else {
                    expectedOutput[i] = 1 - epsilon;
                }
            }
        }

        int i = 0;

        for (SingleNeuron singleNeuron : outputLayer) {
            for (Connection connection : singleNeuron.getNeuronConnections()) {
                double currentOutput = singleNeuron.getOutput();
                double leftOutput = connection.getLeftNeuron().getOutput();
                double desiredOutput = expectedOutput[i];

                double partialDerivative = -currentOutput * (1 - currentOutput) * leftOutput *
                        (desiredOutput - currentOutput);

                double deltaWeight = -learningRate * partialDerivative;

                double newWeight = connection.getWeight() + deltaWeight;
                connection.setDeltaWeight(deltaWeight);
                connection.setWeight(newWeight + momentum * connection.getPreviousDeltaWeight());
            }

            i++;
        }

        hiddenLayer.forEach(neuron -> {
            neuron.getNeuronConnections().forEach(connection -> {
                double currentOutput = neuron.getOutput();
                double leftOutput = connection.getLeftNeuron().getOutput();

                double sumKoutputs = 0;

                int j = 0;

                for (SingleNeuron outputNeuron : outputLayer) {
                    double outputNeuronWeight = outputNeuron.getConnection(neuron.getId()).getWeight();
                    double desiredOutput = expectedOutput[j++];
                    double outputNeuronOutput = outputNeuron.getOutput();

                    sumKoutputs = sumKoutputs + (-(desiredOutput - outputNeuronOutput) * outputNeuronOutput *
                            (1 - outputNeuronOutput) * outputNeuronWeight);
                }

                double partialDerivative = currentOutput * (1 - currentOutput) * leftOutput * sumKoutputs;
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = connection.getWeight() + deltaWeight;

                connection.setDeltaWeight(deltaWeight);
                connection.setWeight(newWeight + momentum * connection.getPreviousDeltaWeight());
            });
        });


    }
}
