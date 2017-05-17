package sample;

import backpropagation_network.Network;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Controller implements Initializable {

    @FXML
    private Canvas canvas;

    @FXML
    private void check() throws IOException {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);

        Graphics2D g = imageRGB.createGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();

        ImageIO.write(imageRGB, "jpg", new File("example.jpg"));

        // reading

        bufferedImage = ImageIO.read(new File("example.jpg"));

        int[][] result = Network.convertTo2DWithoutUsingGetRGB(bufferedImage);

        int firstX = -1;
        int firstY = -1;
        int lastX = -1;
        int lastY = -1;

        for(int y = 0; y < result.length; y++) {
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

        BufferedImage subImage = bufferedImage.getSubimage(firstX, firstY, lastX - firstX, lastY - firstY);

        ImageIO.write(subImage, "jpg", new File("example.jpg"));


        BufferedImage resizedImage = Thumbnails.of(subImage).forceSize(100, 100).asBufferedImage();

        result = Main.convertTo2DWithoutUsingGetRGB(resizedImage);

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

        System.out.println(Main.network.checkDataSet(summaryList));
    }

    @FXML
    private void clear() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getHeight(), canvas.getWidth());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        canvas.setOnMouseDragged(e -> {
            double size = 2;
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;

            graphicsContext.setFill(Paint.valueOf("000000"));
            graphicsContext.fillRect(x, y, size, size);
        });
    }
}
