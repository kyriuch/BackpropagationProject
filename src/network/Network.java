package network;

import sample.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Network {

    // ------ WARSTWY SIECI ------- \\

    private Layer inputLayer;
    private HiddenLayer hiddenLayer;
    private OutputLayer outputLayer;

    // ------ DANE UCZĄCE ORAZ WYJSCIA SIECI ------ \\

    private List<List<Double>> inputs = new ArrayList<>();
    private List<List<Double>> expectedOutputs = new ArrayList<>();
    private Map<Integer, List<Double>> acutalOutputs = new HashMap<>();

    // ------ ZMIENNE WLASNE ------ \\

    private int iterator = 0;
    private boolean isEnded = false;
    private double overallError;
    private Controller controller;

    //------ CONFIGURATION --------\\

    private int maxEras = 500;
    private double minError = 0.0001;
    private double learningRate = 0.2f;
    private double momentum = 0.4f;


    public Network(Layer inputLayer, Layer hiddenLayer, Layer outputLayer) { // zbudowanie sieci
        this.inputLayer = inputLayer;
        this.hiddenLayer = (HiddenLayer) hiddenLayer;
        this.outputLayer = (OutputLayer) outputLayer;
    }

    private void sumWeightsAndActivate() { // suma iloczynu wag i wyjść + funkcja sigmoidalna
        hiddenLayer.sumWeights();
        hiddenLayer.activate();
        outputLayer.sumWeights();
        outputLayer.activate();
    }

    private void calculateSignalErrors(List<Double> expectedOutput) { // obliczenie błędu dla pojedynczych neuronów warstwy ukrytej oraz wyjściowej
        int k = 0;

        for(Neuron neuron:outputLayer.list) {
            double singleOutput = expectedOutput.get(k++);
            neuron.setSignalError((singleOutput - neuron.getOutput()) * neuron.getOutput() * (1 - neuron.getOutput())); // błąd * pochodna
        }

        for(Neuron neuron : hiddenLayer.list) {
            double sum = outputLayer.list.stream().
                    mapToDouble(outputNeuron -> outputNeuron.getWeight(neuron) * outputNeuron.getSignalError()).sum(); // suma wag * błędy warstwy wyjsciowej

            neuron.setSignalError(neuron.getOutput() * (1 - neuron.getOutput()) * sum); // błąd neuronu warstwy ukrytej
        }
    }

    private void updateWeights() { // aktualizacja wag na podstawie obliczonych błędów
        outputLayer.list.forEach(neuron -> {
            neuron.setBiasDiff(learningRate * neuron.getSignalError() + momentum * neuron.getBiasDiff());
            neuron.setBiasWeight(neuron.getBiasWeight() + neuron.getBiasDiff());

            hiddenLayer.list.forEach(hiddenNeuron -> {
                neuron.setWeightDiff(hiddenNeuron,
                        learningRate * neuron.getSignalError() * hiddenNeuron.getOutput() + momentum * neuron.getWeightDiff(hiddenNeuron)); // obliczenie różnicy wag z uwzględnieniem błędu

                neuron.setWeight(hiddenNeuron,
                        neuron.getWeight(hiddenNeuron) + neuron.getWeightDiff(hiddenNeuron)); // aktualizacja wagi
            });
        });

        hiddenLayer.list.forEach(neuron -> {
            neuron.setBiasDiff(learningRate * neuron.getSignalError() + momentum * neuron.getBiasDiff());
            neuron.setBiasWeight(neuron.getBiasWeight() + neuron.getBiasDiff());

            inputLayer.list.forEach(inputNeuron -> {
                neuron.setWeightDiff(inputNeuron,
                        learningRate * neuron.getSignalError() * inputNeuron.getOutput() + momentum * neuron.getWeightDiff(inputNeuron)); // obliczenie różnicy wag z uwzględnieniem błędu

                neuron.setWeight(inputNeuron,
                        neuron.getWeight(inputNeuron) + neuron.getWeightDiff(inputNeuron)); // aktualizacja wagi
            });
        });
    }

    private double calculateOverallError() { // obliczenie błędu ogólnego
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


    public void addOneDataSet(List<Double> inputs, List<Double> expectedOutputs) { // dodanie jednej danej uczącej
        this.inputs.add(inputs);
        this.expectedOutputs.add(expectedOutputs);
    }

    public void learn() { // uczenie sieci
        isEnded = false;

        do {

            List<Integer> usedInputs = new ArrayList<>();
            for (int j = 0; j < inputs.size(); j++) { // dla każdej epoki
                int currentInput;

                do {
                    currentInput = ThreadLocalRandom.current().nextInt(0, inputs.size()); // znajdź nieużyty zestaw danych uczących
                } while(usedInputs.contains(currentInput));

                usedInputs.add(currentInput); // zapisz znaleziony zestaw

                int k = 0;

                for(Neuron neuron:inputLayer.list) { // ustaw wyjścia warstwy wejściowej
                    neuron.setOutput(inputs.get(currentInput).get(k++));
                }

                sumWeightsAndActivate(); // suma iloczynu wag i wyjść + funkcja sigmoidalna
                calculateSignalErrors(expectedOutputs.get(currentInput)); // obliczenie błędu wyniku
                updateWeights(); // aktualizacja wag

                List<Double> thisOutputs = new ArrayList<>();

                for(Neuron neuron : outputLayer.list) {
                    thisOutputs.add(neuron.getOutput()); // lista tymczasowa z obecnymi wyjściamy
                }

                acutalOutputs.put(currentInput, thisOutputs); // zapamiętanie wyjść sieci
            }

            overallError = calculateOverallError(); // obliczenie błędu ogólnego

            if(iterator % (maxEras / 10) == 0 || iterator == (maxEras - 1)) { // zapisanie informacji
                controller.appendLine("Era " + iterator + ".");
                controller.appendLine("Błąd ogólny: " + overallError);
                controller.appendLine("Osiągnięty % błędu: " + (minError / overallError * 100) + "%");
            }

            iterator++;
        } while (iterator < maxEras && overallError > minError); // dopóki nie miną wszystkie epoki lub nie osiągniemy minimalnego błędu

        isEnded = true; // nauczanie zakończone
    }

    public List<Double> checkDataSet(List<Double> inputs) { // wyprodukowanie wyniku z otrzymanych danych wejściowych
        List<Double> outputs = new ArrayList<>();
        int k = 0;

        for(Neuron neuron : inputLayer.list) {
            neuron.setOutput(inputs.get(k++));
        }

        sumWeightsAndActivate();

        outputLayer.list.forEach(neuron -> outputs.add(neuron.getOutput()));

        return outputs;
    }

    public int getMaxEras() {
        return maxEras;
    }

    public int getIterator() {
        return iterator;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public Layer getInputLayer() {
        return inputLayer;
    }

    public void setMaxEras(int maxEras) {
        this.maxEras = maxEras;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public void setMomentum(double momentum) {
        this.momentum = momentum;
    }

    public void setMinError(double minError) {
        this.minError = minError;
    }

    public double getOverallError() {
        return overallError;
    }

    public double getMinError() {
        return minError;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
