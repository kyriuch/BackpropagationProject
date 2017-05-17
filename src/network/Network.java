package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Network {

    private Layer inputLayer;
    private HiddenLayer hiddenLayer;
    private OutputLayer outputLayer;

    private List<List<Double>> inputs = new ArrayList<>();
    private List<List<Double>> expectedOutputs = new ArrayList<>();
    private Map<Integer, List<Double>> acutalOutputs = new HashMap<>();


    //------ CONFIGURATION --------\\

    private int maxEras = 5000;
    private double minError = 0.0001;
    private double learningRate = 0.2f;
    private double momentum = 0.4f;

    public Network(Layer inputLayer, Layer hiddenLayer, Layer outputLayer) {
        this.inputLayer = inputLayer;
        this.hiddenLayer = (HiddenLayer) hiddenLayer;
        this.outputLayer = (OutputLayer) outputLayer;
    }

    private void sumWeightsAndActivate() {
        hiddenLayer.sumWeights();
        hiddenLayer.activate();
        outputLayer.sumWeights();
        outputLayer.activate();
    }

    private void calculateSignalErrors(List<Double> expectedOutput) {
        int k = 0;

        for(Neuron neuron:outputLayer.list) {
            double singleOutput = expectedOutput.get(k++);
            neuron.setSignalError((singleOutput - neuron.getOutput()) * neuron.getOutput() * (1 - neuron.getOutput())); // error * derivative
        }

        for(Neuron neuron : hiddenLayer.list) {
            double sum = outputLayer.list.stream().
                    mapToDouble(outputNeuron -> outputNeuron.getWeight(neuron) * outputNeuron.getSignalError()).sum();

            neuron.setSignalError(neuron.getOutput() * (1 - neuron.getOutput()) * sum);
        }
    }

    private void updateWeights() {
        outputLayer.list.forEach(neuron -> {
            neuron.setBiasDiff(learningRate * neuron.getSignalError() + momentum * neuron.getBiasDiff());
            neuron.setBiasWeight(neuron.getBiasWeight() + neuron.getBiasDiff());

            hiddenLayer.list.forEach(hiddenNeuron -> {
                neuron.setWeightDiff(hiddenNeuron,
                        learningRate * neuron.getSignalError() * hiddenNeuron.getOutput() + momentum * neuron.getWeightDiff(hiddenNeuron));

                neuron.setWeight(hiddenNeuron,
                        neuron.getWeight(hiddenNeuron) + neuron.getWeightDiff(hiddenNeuron));
            });
        });

        hiddenLayer.list.forEach(neuron -> {
            neuron.setBiasDiff(learningRate * neuron.getSignalError() + momentum * neuron.getBiasDiff());
            neuron.setBiasWeight(neuron.getBiasWeight() + neuron.getBiasDiff());

            inputLayer.list.forEach(inputNeuron -> {
                neuron.setWeightDiff(inputNeuron,
                        learningRate * neuron.getSignalError() * inputNeuron.getOutput() + momentum * neuron.getWeightDiff(inputNeuron));

                neuron.setWeight(inputNeuron,
                        neuron.getWeight(inputNeuron) + neuron.getWeightDiff(inputNeuron));
            });
        });
    }

    private double calculateOverallError() {
        double error = 0;

        int i = 0;

        for(List<Double> expectedOutput : expectedOutputs) {
            for(int j = 0; j < acutalOutputs.get(i).size(); j++) {
                error += 0.5 * (Math.pow(expectedOutput.get(j) - acutalOutputs.get(i).get(j), 2));
            }

            i++;
        }

        return error;
    }


    public void addOneDataSet(List<Double> inputs, List<Double> expectedOutputs) {
        this.inputs.add(inputs);
        this.expectedOutputs.add(expectedOutputs);
    }

    public void learn() {
        int i = 0;
        double overallError;

        do {
            List<Integer> usedInputs = new ArrayList<>();
            for (int j = 0; j < inputs.size(); j++) {
                int currentInput;

                do {
                    currentInput = ThreadLocalRandom.current().nextInt(0, inputs.size());
                } while(usedInputs.contains(currentInput));

                usedInputs.add(currentInput);

                int k = 0;

                for(Neuron neuron:inputLayer.list) {
                    neuron.setOutput(inputs.get(currentInput).get(k++));
                }

                sumWeightsAndActivate();
                calculateSignalErrors(expectedOutputs.get(currentInput));
                updateWeights();

                List<Double> thisOutputs = new ArrayList<>();

                for(Neuron neuron : outputLayer.list) {
                    thisOutputs.add(neuron.getOutput());
                }

                acutalOutputs.put(currentInput, thisOutputs);
            }

            overallError = calculateOverallError();
            System.out.println(overallError);
            i++;
        } while (i < 50000 && overallError > minError);

        System.out.println(i);

        System.out.println("ended learning");
    }

    public List<Double> checkDataSet(List<Double> inputs) {
        List<Double> outputs = new ArrayList<>();
        int k = 0;

        for(Neuron neuron : inputLayer.list) {
            neuron.setOutput(inputs.get(k++));
        }

        sumWeightsAndActivate();

        outputLayer.list.forEach(neuron -> outputs.add(neuron.getOutput()));

        return outputs;
    }
}
