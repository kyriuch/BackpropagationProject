package sample;

import backpropagation_network.Network;
import backpropagation_network.NetworkBuilder;
import backpropagation_network.Neuron;
import backpropagation_network.NeuronFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main extends Application {

    Neuron biasNeuron;
    List<Neuron> inputLayer;
    List<Neuron> hiddenLayer;
    List<Neuron> outputLayer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.show();

        biasNeuron = NeuronFactory.getBiasNeuron(-1);
        inputLayer = buildInputLayer();

        hiddenLayer = buildHiddenLayer();
        primaryStage.setScene(new Scene(root, 300, 275));
        outputLayer = buildOutputLayer();


        Network network = new NetworkBuilder().build(inputLayer,
                hiddenLayer,
                outputLayer);

        List<List<Double>> inputs = new ArrayList<>();
        inputs.add(Arrays.asList(0.0, 0.0));
        inputs.add(Arrays.asList(0.0, 0.1));
        inputs.add(Arrays.asList(1.0, 0.0));
        inputs.add(Arrays.asList(1.0, 1.0));

        List<List<Double>> expectedOutputs = new ArrayList<>();
        expectedOutputs.add(Collections.singletonList(0.0));
        expectedOutputs.add(Collections.singletonList(0.0));
        expectedOutputs.add(Collections.singletonList(0.0));
        expectedOutputs.add(Collections.singletonList(1.0));


        network.learn(inputs, expectedOutputs, 5000);

    }

    private List<Neuron> buildInputLayer() {
        List<Neuron> inputLayer = new ArrayList<>();

        inputLayer.add(NeuronFactory.getNeuronWithoutConnections());
        inputLayer.add(NeuronFactory.getNeuronWithoutConnections());

        return inputLayer;
    }

    private List<Neuron> buildHiddenLayer() {
        List<Neuron> firstHiddenLayer = new ArrayList<>();

        firstHiddenLayer.add(NeuronFactory.getNeuronWithConnections(inputLayer, biasNeuron));
        firstHiddenLayer.add(NeuronFactory.getNeuronWithConnections(inputLayer, biasNeuron));
        firstHiddenLayer.add(NeuronFactory.getNeuronWithConnections(inputLayer, biasNeuron));
        firstHiddenLayer.add(NeuronFactory.getNeuronWithConnections(inputLayer, biasNeuron));

        return firstHiddenLayer;
    }

    private List<Neuron> buildOutputLayer() {
        List<Neuron> outputLayer = new ArrayList<>();

        outputLayer.add(NeuronFactory.getNeuronWithConnections(hiddenLayer, biasNeuron));

        return outputLayer;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

