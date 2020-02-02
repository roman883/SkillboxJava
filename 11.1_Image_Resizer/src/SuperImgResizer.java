import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SuperImgResizer implements Runnable {

    File[] files;
    int newWidth;
    long start;
    String dstFolder;

    @Override
    public void run() {
        int width;
        try {
            for (File file : files) {
                BufferedImage image = ImageIO.read(file);
                if (image == null) {
                    continue;
                }
                if (image.getWidth() >= (newWidth * 4)) { // Уменьшаем минимум в 4 раза
                    width = 4 * newWidth;
                    nearestNeighbour(file, width);
                    File tempFile = new File(dstFolder + "/" + file.getName());
                    width /= 2;
                    nearestNeighbour(tempFile, width);
                    File newFile = new File(dstFolder + "/" + tempFile.getName());
                    width = newWidth;
                    nearestNeighbour(newFile, width);
                } else if (image.getWidth() >= (newWidth * 2)) { // Уменьшаем в 2-4 раза
                    width = 2 * newWidth;
                    nearestNeighbour(file, width);
                    File tempFile = new File(dstFolder + "/" + file.getName());
                    width = newWidth;
                    nearestNeighbour(tempFile, width);
                } else { // Увеличиваем (или уменьшаем менее чем в 2 раза)
                    nearestNeighbour(file, newWidth);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Finished milliseconds after start: " + (System.currentTimeMillis() - start));
    }

    private void nearestNeighbour(File file, int width) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int height = (int) Math.round(
                image.getHeight() / (image.getWidth() / (double) width)
        );
        BufferedImage newImage = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB
        );
        double widthStep = (double) image.getWidth() / width;
        double heightStep = (double) image.getHeight() / height;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB((int) (x * widthStep), (int) (y * heightStep));
                newImage.setRGB(x, y, rgb);
            }
        }
        Files.deleteIfExists(Path.of(dstFolder + "/" + file.getName()));
        File newFile = new File(dstFolder + "/" + file.getName());
        ImageIO.write(newImage, "jpg", newFile);
    }

    public SuperImgResizer(File[] files, int newWidth, String dstFolder, long start) {
        this.files = files;
        this.newWidth = newWidth;
        this.start = start;
        this.dstFolder = dstFolder;
    }
}
