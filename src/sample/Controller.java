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

        BufferedImage resizedImage = new BufferedImage(30, 30, bufferedImage.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(bufferedImage, 0, 0, 30, 30, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics2D.dispose();

        int[][] result = Network.convertTo2DWithoutUsingGetRGB(resizedImage);

        java.util.List<Double> oneInput = new ArrayList<>();

        for (int[] aResult : result) {
            for (int anAResult : aResult) {
                oneInput.add((double) anAResult);
            }
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
