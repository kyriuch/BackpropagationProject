package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;
import network.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main extends Application {

    public static Network network;

    private List<Double> getExpectedOutput(String digit) {
        switch (digit) {
            case "0":
                return Arrays.asList(1.0, 0.0, 0.0, 0.0);
            case "1":
                return Arrays.asList(0.0, 1.0, 0.0, 0.0);
            case "2":
                return Arrays.asList(0.0, 0.0, 1.0, 0.0);
            case "3":
                return Arrays.asList(0.0, 0.0, 0.0, 1.0);
        }

        return null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        Layer inputLayer = new InputLayer(100);
        Layer hiddenLayer = new HiddenLayer(4, inputLayer);
        Layer outputLayer = new OutputLayer(4, hiddenLayer);

        network = new Network(inputLayer, hiddenLayer, outputLayer);

        File actualDir = new File(".\\images");

        Arrays.asList(actualDir.listFiles()).forEach(file -> {
            try {

                System.out.println(file.getName());

                BufferedImage bufferedImage = ImageIO.read(file);

                BufferedImage resizedImage = Thumbnails.of(bufferedImage).forceSize(100, 100).asBufferedImage();

                int[][] result = convertTo2DWithoutUsingGetRGB(resizedImage);

                List<Double> horizontalLine = new ArrayList<>();
                List<Double> verticalLine = new ArrayList<>();

                for (int i = 0; i < 100; i += 2) {
                    int counterX = 0;
                    int counterY = 0;

                    for (int j = 0; j < 100; j += 2) {
                        if (result[i][j] != -1) {
                            counterX++;
                        }

                        if (result[j][i] != -1) {
                            counterY++;
                        }
                    }

                    horizontalLine.add((double) counterX);
                    verticalLine.add((double) counterY);
                }

                List<Double> summaryList = new ArrayList<>();

                final Optional<Double> max = horizontalLine.stream().max(Comparator.naturalOrder());
                final Optional<Double> min = horizontalLine.stream().min(Comparator.naturalOrder());

                horizontalLine.forEach(digit -> summaryList.add((digit - min.get()) / (max.get() - min.get())));

                final Optional<Double> max2 = verticalLine.stream().max(Comparator.naturalOrder());
                final Optional<Double> min2 = verticalLine.stream().min(Comparator.naturalOrder());

                verticalLine.forEach(digit -> summaryList.add((digit - min2.get()) / (max2.get() - min2.get())));

                System.out.println(summaryList);
                network.addOneDataSet(summaryList, getExpectedOutput(file.getName().split("_")[0]));
                System.out.println(summaryList.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        network.learn();

        /*network.addOneDataSet(Arrays.asList(0.0, 0.0, 0.0, 0.0), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.0, 0.0, 0.0, 0.1), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.0, 0.0, 0.1, 0.0), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.0, 0.0, 0.1, 0.1), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.0, 0.1, 0.0, 0.0), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.0, 0.1, 0.0, 0.1), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.0, 0.1, 0.1, 0.0), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.0, 0.1, 0.1, 0.1), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.0, 0.1, 0.1, 0.0), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.1, 0.0, 0.0, 0.1), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.1, 0.0, 0.1, 0.0), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.1, 0.0, 0.1, 0.1), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.1, 0.1, 0.0, 0.0), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.1, 0.1, 0.0, 0.1), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.1, 0.1, 0.1, 0.0), Collections.singletonList(0.0));
        network.addOneDataSet(Arrays.asList(0.1, 0.1, 0.1, 0.1), Collections.singletonList(1.0));
        network.learn();

        System.out.println(network.checkDataSet(Arrays.asList(0.0, 0.0, 0.0, 0.0)));
        System.out.println(network.checkDataSet(Arrays.asList(0.0, 0.1, 0.0, 0.1)));
        System.out.println(network.checkDataSet(Arrays.asList(0.1, 0.0, 0.0, 0.0)));
        System.out.println(network.checkDataSet(Arrays.asList(0.0, 0.1, 0.1, 0.0)));
        System.out.println(network.checkDataSet(Arrays.asList(0.1, 0.1, 0.1, 0.1)));*/
    }

    /*private List<Neuron> buildInputLayer() {
        List<Neuron> inputLayer = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            inputLayer.add(NeuronFactory.getNeuronWithoutConnections());
        }

        return inputLayer;
    }

    private List<Neuron> buildHiddenLayer() {
        List<Neuron> firstHiddenLayer = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            firstHiddenLayer.add(NeuronFactory.getNeuronWithConnections(inputLayer, biasNeuron));
        }

        return firstHiddenLayer;
    }

    private List<Neuron> buildOutputLayer() {
        List<Neuron> outputLayer = new ArrayList<>();

        for(int i = 0; i < 4; i++) {
            outputLayer.add(NeuronFactory.getNeuronWithConnections(hiddenLayer, biasNeuron));
        }


        return outputLayer;
    }*/

    public static void main(String[] args) {
        launch(args);
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

/*    private void lastStart() {


        network.learn(inputs, expectedOutputs, 5000);
    }*/
    }
}

