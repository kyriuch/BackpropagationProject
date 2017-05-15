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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main extends Application {

    Neuron biasNeuron;
    List<Neuron> inputLayer;
    List<Neuron> hiddenLayer;
    List<Neuron> outputLayer;

    public static Network network;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();


        biasNeuron = NeuronFactory.getNeuronWithoutConnections();
        inputLayer = buildInputLayer();
        hiddenLayer = buildHiddenLayer();
        outputLayer = buildOutputLayer();


        network = new NetworkBuilder().setLearningRate(0.2f).
                build(inputLayer, hiddenLayer, outputLayer);

        List<List<Double>> inputs = new ArrayList<>();
        File actualDir = new File(".\\images");

        Arrays.asList(actualDir.listFiles()).forEach(file -> {
            try {
                System.out.println(file.getName());

                BufferedImage bufferedImage = ImageIO.read(file);

                int[][] result = Network.convertTo2DWithoutUsingGetRGB(bufferedImage);

                List<Double> oneInput = new ArrayList<>();

                for(int i = 0; i < 36; i += 3) {
                    int counter = 0;
                    for(int j = 0; j < 36; j += 3) {
                        if(result[i][j] != -1) {
                            counter++;
                        }
                    }

                    oneInput.add((double) counter);
                }

                inputs.add(oneInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        List<List<Double>> expectedOutputs = new ArrayList<>();

        int currentOne = 0;

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 4; j++) {
                List<Double> expectedOutput = new ArrayList<>();

                for(int k = 0; k < 10; k++) {
                    if(k == currentOne) {
                        expectedOutput.add(1.0);
                    } else {
                        expectedOutput.add(0.0);
                    }
                }

                expectedOutputs.add(expectedOutput);
            }

            currentOne++;
        }

        network.learn(inputs, expectedOutputs, 5000);
    }

    private List<Neuron> buildInputLayer() {
        List<Neuron> inputLayer = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            inputLayer.add(NeuronFactory.getNeuronWithoutConnections());
        }

        return inputLayer;
    }

    private List<Neuron> buildHiddenLayer() {
        List<Neuron> firstHiddenLayer = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            firstHiddenLayer.add(NeuronFactory.getNeuronWithConnections(inputLayer, biasNeuron));
        }

        return firstHiddenLayer;
    }

    private List<Neuron> buildOutputLayer() {
        List<Neuron> outputLayer = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            outputLayer.add(NeuronFactory.getNeuronWithConnections(hiddenLayer, biasNeuron));
        }


        return outputLayer;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

