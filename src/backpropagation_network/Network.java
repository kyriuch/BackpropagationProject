package backpropagation_network;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
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
        this.minErrorCondition = networkBuilder.minErrorCondition;

        this.inputLayer = networkBuilder.inputLayer;
        this.hiddenLayer = networkBuilder.hiddenLayer;
        this.outputLayer = networkBuilder.outputLayer;
    }

    public void check(List<Double> input) {
        setInputLayerOutputs(input);

        activate();

        System.out.print("ACTUAL: ");
        for (int x = 0; x < outputLayer.size(); x++) {
            System.out.println(outputLayer.get(x).getOutput());
        }
    }

    public void learn(List<List<Double>> inputs, List<List<Double>> expectedOutputs, int maxEras) {
        double error = 1;

        for (int i = 0; i < maxEras && error > minErrorCondition; i++) {
            error = 0;

            for (int j = 0; j < inputs.size(); j++) { // for each data to learn
                setInputLayerOutputs(inputs.get(j));

                activate();

                if(outputs.size() < j + 1) {
                    outputs.add(j, outputLayer.stream().mapToDouble(Neuron::getOutput).boxed().collect(Collectors.toList()));
                } else {
                    outputs.set(j, outputLayer.stream().mapToDouble(Neuron::getOutput).boxed().collect(Collectors.toList()));
                }

                for (int k = 0; k < outputs.get(j).size(); k++) {
                    error += Math.pow(outputs.get(j).get(k) - expectedOutputs.get(j).get(k), 2);
                }

                applyBackpropagation(expectedOutputs.get(j));
            }
        }

        System.out.println("NN example with xor training");
        for (int p = 0; p < inputs.size(); p++) {
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
        final List<Double> expectedList = new ArrayList<>();

        expectedOutput.forEach(output -> {
            if(output < 0) {
                expectedList.add(0 + epsilon);
            } else if(output > 1) {
                expectedList.add(1 - epsilon);
            } else {
                expectedList.add(output);
            }
        });

        int i = 0;

        for (Neuron neuron : outputLayer) {
            List<Connection> connections = neuron.getConnections();

            for (Connection connection : connections) {
                double ak = neuron.getOutput();
                double ai = connection.getLeftNeuron().getOutput();
                double desiredOutput = expectedList.get(i);

                double partialDerivative = -ak * (1 - ak) * ai * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = connection.getConnectionWeight() + deltaWeight;

                connection.setDeltaWeight(deltaWeight);
                connection.setConnectionWeight(newWeight + momentum * connection.getPreviousDeltaWeight());
            }

            i++;
        }

        // update weights for the hidden layer
        hiddenLayer.forEach(neuron -> neuron.getConnections().forEach(connection -> {
            double aj = neuron.getOutput();
            double ai = connection.getLeftNeuron().getOutput();
            double sumKoutputs = 0;

            int j = 0;

            for(Neuron outNeuron : outputLayer) {
                double wjk = outNeuron.getConnection(neuron.getId()).getConnectionWeight();
                double ak = outNeuron.getOutput();

                sumKoutputs += (-(expectedList.get(j) - ak) * ak * (1 - ak) * wjk);

                j++;
            }

            double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
            double deltaWeight = -learningRate * partialDerivative;
            double newWeight = connection.getConnectionWeight() + deltaWeight;
            connection.setDeltaWeight(deltaWeight);
            connection.setConnectionWeight(newWeight + momentum * connection.getPreviousDeltaWeight());
        }));
    }

    public static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }
}
