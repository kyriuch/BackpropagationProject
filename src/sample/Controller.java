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

        int firstYPixel = -1;
        int lastYPixel = -1;
        int firstXPixel = -1;
        int lastXPixel = -1;

        for(int x = 0; x < result.length; x++) {
            for (int y = 0; y < result[x].length; y++) {

                if(result[y][x] != -1) {
                    if(firstYPixel == -1) {
                        firstYPixel = y;
                        lastYPixel = y;
                        firstXPixel = x;
                        lastXPixel = x;
                    }

                    if(y < firstYPixel) {
                        firstYPixel = y;
                    }

                    if(y > lastXPixel) {
                        lastYPixel = y;
                    }

                    if(x > lastXPixel) {
                        lastXPixel = x;
                    }

                    if(x < firstXPixel) {
                        firstXPixel = x;
                    }
                }
            }
        }

        System.out.println(firstXPixel + ":" + lastXPixel);
        System.out.println(firstYPixel + ":" + lastYPixel);

        BufferedImage subImage = bufferedImage.getSubimage(firstXPixel - 2, firstYPixel - 2, lastXPixel - firstXPixel + 4, lastYPixel - firstYPixel + 4);

        ImageIO.write(subImage, "jpg", new File("example.jpg"));


        BufferedImage resizedImage = new BufferedImage(36, 36, subImage.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(subImage, 0, 0, 36, 36, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics2D.dispose();

        result = Network.convertTo2DWithoutUsingGetRGB(resizedImage);

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

        Main.network.check(oneInput);
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
            double size = 5.0;
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;

            graphicsContext.setFill(Paint.valueOf("000000"));
            graphicsContext.fillRect(x, y, size, size);
        });
    }
}
