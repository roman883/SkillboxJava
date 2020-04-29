import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;

public class ImageResizer extends Thread {

    private int newWidth;
    private long start;
    private String dstFolder;
    private Queue<File> files;

    public ImageResizer(Queue<File> files, int newWidth, String dstFolder, long start) {
        this.files = files;
        this.newWidth = newWidth;
        this.dstFolder = dstFolder;
        this.start = start;
    }

    @Override
    public void run() {
        File file;
        try {
            while (files.peek() != null) {
                file = files.poll();
                BufferedImage image = ImageIO.read(file);
                if (image == null) {
                    continue;
                }
                int newHeight = (int) Math.round(
                        image.getHeight() / (image.getWidth() / (double) newWidth)
                );
                BufferedImage newImage = new BufferedImage(
                        newWidth, newHeight, BufferedImage.TYPE_INT_RGB
                );

                int widthStep = image.getWidth() / newWidth;
                int heightStep = image.getHeight() / newHeight;

                for (int x = 0; x < newWidth; x++) {
                    for (int y = 0; y < newHeight; y++) {
                        int rgb = image.getRGB(x * widthStep, y * heightStep);
                        newImage.setRGB(x, y, rgb);
                    }
                }
                Files.deleteIfExists(Path.of(dstFolder + "/" + file.getName()));
                File newFile = new File(dstFolder + "/" + file.getName());
                ImageIO.write(newImage, "jpg", newFile);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Finished milliseconds after start: " + (System.currentTimeMillis() - start));
    }
}
