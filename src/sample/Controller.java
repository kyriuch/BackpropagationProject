package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import net.coobird.thumbnailator.Thumbnails;
import network.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Controller implements Initializable {

    private Network network;

    private int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) { // metoda konwertująca obraz na tablicę pikseli
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

    private final char[] actualLetters = {'1', '2', '3', '4'}; // aktualnie rozpatrywane znaki

    private List<Double> getExpectedOutput(String digit) { // podanie oczekiwanego wyniku sieci z nazwy pliku
        if (digit.charAt(0) == actualLetters[0]) {
            return Arrays.asList(1.0, 0.0, 0.0, 0.0);
        } else if (digit.charAt(0) == actualLetters[1]) {
            return Arrays.asList(0.0, 1.0, 0.0, 0.0);
        } else if (digit.charAt(0) == actualLetters[2]) {
            return Arrays.asList(0.0, 0.0, 1.0, 0.0);
        } else if (digit.charAt(0) == actualLetters[3]) {
            return Arrays.asList(0.0, 0.0, 0.0, 1.0);
        } else {
            return null;
        }

    }

    @FXML
    private Canvas canvas;

    @FXML
    private TextArea outputs;

    @FXML
    private Label networkAnswer;

    @FXML
    private ProgressBar learnProgressBar;

    @FXML
    private ProgressBar errorProgressBar;

    @FXML
    private TextField erasTextField;

    @FXML
    private TextField learningRateTextField;

    @FXML
    private TextField momentumTextField;

    @FXML
    private TextField hiddenNeuronsTextField;

    @FXML
    private TextField inputsTextField;

    @FXML
    private TextField minimumErrorTextField;

    @FXML
    private void check() throws IOException { // obliczenie wyniku dla obecnie wprowadzonego obrazu
        if(network == null) {
            return;
        }

        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage); // snapshot okna do rysowania

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g = imageRGB.createGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();

        ImageIO.write(imageRGB, "png", new File("example.png"));

        // reading

        bufferedImage = ImageIO.read(new File("example.png"));


        int[][] result = convertTo2DWithoutUsingGetRGB(bufferedImage);

        int firstX = -1;
        int firstY = -1;
        int lastX = -1;
        int lastY = -1;

        for(int y = 0; y < result.length; y++) { // wycięcie niepotrzebnego tła z narysowanego obrazu
            for(int x = 0; x < result.length; x++) {
                if(result[x][y] != -1) {
                    if(firstX == -1) {
                        firstX = y;
                        lastX = y;
                        firstY = x;
                        lastY = x;
                    }

                    if(x > lastY) {
                        lastY = x;
                    }

                    if(x < firstY) {
                        firstY = x;
                    }

                    if(y > lastX) {
                        lastX = y;
                    }

                    if(y < firstX) {
                        firstX = y;
                    }
                }
            }
        }

        firstX -= 2;
        firstY -= 2;
        lastX += 2;
        lastY += 2;

        BufferedImage subImage = bufferedImage.getSubimage(firstX, firstY, lastX - firstX, lastY - firstY); // przygotowanie obrazu z wyciętym tłem

        ImageIO.write(subImage, "png", new File("example.png"));

        BufferedImage resizedImage = Thumbnails.of(subImage).forceSize(100, 100).asBufferedImage();

        result = convertTo2DWithoutUsingGetRGB(resizedImage); // utworzenie tablicy pikseli

        List<Double> horizontalLine = new ArrayList<>();

        for (int i = 0; i < 100; i += 100 / network.getInputLayer().getSize()) { // podział obrazu na linie
            int counterX = 0;

            for (int j = 0; j < 100; j += 100 / network.getInputLayer().getSize()) {
                if (result[i][j] != -1) {
                    counterX++;
                }
            }

            horizontalLine.add((double) counterX); // zapamiętanie zliczonych pikseli w punktach przecięcia
        }

        List<Double> summaryList = new ArrayList<>();

        final Optional<Double> max = horizontalLine.stream().max(Comparator.naturalOrder());
        final Optional<Double> min = horizontalLine.stream().min(Comparator.naturalOrder());

        horizontalLine.forEach(digit -> summaryList.add((digit - min.get()) / (max.get() - min.get()))); // normalizacja

        List<Double> outputList = network.checkDataSet(summaryList);

        networkAnswer.setText("Wprowadzona liczba to najprawdopodobniej: " + actualLetters[outputList.indexOf(Collections.max(outputList))]);

        outputs.appendText(outputList.toString() + "\n");
    }

    @FXML
    private void clear() { // wyczyszczenie pola do rysowania
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getHeight(), canvas.getWidth());
    }



    @FXML
    private void learn() { // konfiguracja sieci i proces nauczania
        errorProgressBar.setProgress(0.0);
        learnProgressBar.setProgress(0.0);

        // ------- POBRANIE PÓL Z OKNA ---------- \\

        int eras = Integer.parseInt(erasTextField.getText());
        double learningRate = Double.parseDouble(learningRateTextField.getText());
        double momentum = Double.parseDouble(momentumTextField.getText());
        double minError = Double.parseDouble(minimumErrorTextField.getText());
        int hiddenNeurons = Integer.parseInt(hiddenNeuronsTextField.getText());
        int inputs = Integer.parseInt(inputsTextField.getText());

        Layer inputLayer;

        if(inputs >= 10 && inputs <= 100 && inputs % 5 == 0) { // utworzenie warstwy wejściowej
            inputLayer = new InputLayer(inputs);
        } else {
            inputLayer = new InputLayer(20);
        }

        Layer hiddenLayer;

        if(hiddenNeurons > 0 && hiddenNeurons < 20) { // utworzenie warstwy ukrytej
            hiddenLayer = new HiddenLayer(hiddenNeurons, inputLayer);
        } else {
            hiddenLayer = new HiddenLayer(4, inputLayer);
        }

        Layer outputLayer = new OutputLayer(4, hiddenLayer); // utworzenie warstwy wyjściowej

        network = new Network(inputLayer, hiddenLayer, outputLayer); // zbudowanie sieci
        network.setController(this);


        // ---------- KONFIGURACJA SIECI --------- \\

        if(minError < 1.0 && minError > 0.000001) {
            network.setMinError(minError);
        }

        if(learningRate < 1.0 && learningRate > 0.0) {
            network.setLearningRate(learningRate);
        }

        if(momentum < 1.0 && momentum > 0.0) {
            network.setMomentum(momentum);
        }

        if(eras > 0 && eras < 10000000) {
            network.setMaxEras(eras);
        }

        File actualDir = new File(".\\images");

        Arrays.asList(actualDir.listFiles()).forEach(file -> { // pobranie plików z folderu images i zapisanie ich jako dane uczące
            try {
                BufferedImage bufferedImage = ImageIO.read(file);


                BufferedImage scaledImage = Thumbnails.of(bufferedImage).forceSize(100, 100).asBufferedImage(); // skalowanie do 100x100


                int[][] result = convertTo2DWithoutUsingGetRGB(scaledImage); // utworzenie tablicy pikseli

                List<Double> horizontalLine = new ArrayList<>();

                for (int i = 0; i < 100; i += (100 / network.getInputLayer().getSize())) { // podział na linie
                    int counterX = 0;

                    for (int j = 0; j < 100; j += (100 / network.getInputLayer().getSize())) {
                        if (result[i][j] != -1) {
                            counterX++;
                        }

                    }

                    horizontalLine.add((double) counterX); // zapamiętanie zliczonych pikseli w punktach przecięcia
                }

                List<Double> summaryList = new ArrayList<>();

                final Optional<Double> max = horizontalLine.stream().max(Comparator.naturalOrder());
                final Optional<Double> min = horizontalLine.stream().min(Comparator.naturalOrder());

                horizontalLine.forEach(digit -> summaryList.add((digit - min.get()) / (max.get() - min.get()))); // normalizacja

                network.addOneDataSet(summaryList, getExpectedOutput(file.getName().split("_")[0])); // dodanie danych uczących


            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        new Thread(network::learn).start(); // rozpoczęciu wątku w którym odbywa się nauczanie

        new Thread(() -> { // wątek odpowiadający za aktualizację progress barów
            double progress;

            do {
                progress = (double) network.getIterator() / (double) network.getMaxEras();

                learnProgressBar.setProgress(progress);

                progress = network.getMinError() / network.getOverallError();

                if(progress <= 1.0 && progress != Double.POSITIVE_INFINITY) {
                    errorProgressBar.setProgress(progress);
                }
            } while(!network.isEnded());
        }).start();

    }

    public void appendLine(String text) { // metoda odpowiadająca za dodanie informacji do okna na logi
        Platform.runLater(() -> {
            outputs.appendText(text + "\n");
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> { // konfiguracja pola do rysowania
            double size = 2;
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;

            graphicsContext.setFill(Paint.valueOf("000000"));
            graphicsContext.fillRect(x, y, size, size);
        });
    }
}
