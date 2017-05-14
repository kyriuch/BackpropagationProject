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


        network = new NetworkBuilder().build(inputLayer,
                hiddenLayer,
                outputLayer);

        List<List<Double>> inputs = new ArrayList<>();
        File actualDir = new File(".\\images");

        Arrays.asList(actualDir.listFiles()).forEach(file -> {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                BufferedImage resizedImage = new BufferedImage(30, 30, bufferedImage.getType());
                Graphics2D graphics2D = resizedImage.createGraphics();
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics2D.drawImage(bufferedImage, 0, 0, 30, 30, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
                graphics2D.dispose();

                int[][] result = Network.convertTo2DWithoutUsingGetRGB(resizedImage);

                List<Double> oneInput = new ArrayList<>();

                for (int[] aResult : result) {
                    for (int anAResult : aResult) {
                        oneInput.add((double) anAResult);
                    }
                }

                inputs.add(oneInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        List<List<Double>> expectedOutputs = new ArrayList<>();

        /**
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 1.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 1.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 1.0, 1.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 1.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 1.0, 0.0, 1.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 1.0, 1.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 1.0, 1.0, 1.0));
        expectedOutputs.add(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 1.0, 0.0, 0.0, 1.0));
        expectedOutputs.add(Arrays.asList(0.0, 1.0, 0.0, 1.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 1.0, 0.0, 1.0, 1.0));
        expectedOutputs.add(Arrays.asList(0.0, 1.0, 1.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 1.0, 1.0, 0.0, 1.0));
        expectedOutputs.add(Arrays.asList(0.0, 1.0, 1.0, 1.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 1.0, 1.0, 1.0, 1.0));
        expectedOutputs.add(Arrays.asList(1.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(1.0, 0.0, 0.0, 0.0, 1.0));
        expectedOutputs.add(Arrays.asList(1.0, 0.0, 0.0, 1.0, 0.0));
        expectedOutputs.add(Arrays.asList(1.0, 0.0, 0.0, 1.0, 1.0));
        expectedOutputs.add(Arrays.asList(1.0, 0.0, 1.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(1.0, 0.0, 1.0, 0.0, 1.0));
        expectedOutputs.add(Arrays.asList(1.0, 0.0, 1.0, 1.0, 0.0));
        expectedOutputs.add(Arrays.asList(1.0, 0.0, 1.0, 1.0, 1.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        expectedOutputs.add(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
         **/

        for (int i = 0; i < 9; i++) {
            expectedOutputs.add(Collections.singletonList(1.0));
        }

        for (int i = 0; i < 23; i++) {
            expectedOutputs.add(Collections.singletonList(0.0));
        }



        network.learn(inputs, expectedOutputs, 5000);
    }

    private List<Neuron> buildInputLayer() {
        List<Neuron> inputLayer = new ArrayList<>();

        for(int i = 0; i < 900; i++) {
            inputLayer.add(NeuronFactory.getNeuronWithoutConnections());
        }

        return inputLayer;
    }

    private List<Neuron> buildHiddenLayer() {
        List<Neuron> firstHiddenLayer = new ArrayList<>();

        for(int i = 0; i < 100; i ++) {
            firstHiddenLayer.add(NeuronFactory.getNeuronWithConnections(inputLayer, biasNeuron));
        }

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

