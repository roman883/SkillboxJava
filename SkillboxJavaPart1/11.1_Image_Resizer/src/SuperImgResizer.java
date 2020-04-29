import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;

public class SuperImgResizer implements Runnable {

    private int newWidth;
    private long start;
    private String dstFolder;
    private Queue<File> files;

    @Override
    public void run() {
        int width;
        File file;
        try {
            while (files.peek() != null) {
                file = files.poll();
                BufferedImage image = ImageIO.read(file);
                BufferedImage newImage;
                if (image == null) {
                    continue;
                }
                if (image.getWidth() >= (newWidth * 4)) { // Уменьшаем минимум в 4 раза
                    width = 4 * newWidth;
                    BufferedImage tempImageX4 = nearestNeighbour(image, width);
                    width /= 2;
                    BufferedImage tempImage = nearestNeighbour(tempImageX4, width);
                    width = newWidth;
                    newImage = nearestNeighbour(tempImage, width);
                } else if (image.getWidth() >= (newWidth * 2)) { // Уменьшаем в 2-4 раза
                    width = 2 * newWidth;
                    BufferedImage tempImage = nearestNeighbour(image, width);
                    width = newWidth;
                    newImage = nearestNeighbour(tempImage, width);
                } else { // Увеличиваем (или уменьшаем менее чем в 2 раза)
                    newImage = nearestNeighbour(image, newWidth);
                }
                createFile(newImage, file.getName());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Finished milliseconds after start: " + (System.currentTimeMillis() - start));
    }

    private BufferedImage nearestNeighbour(BufferedImage image, int width) throws IOException {
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
        return newImage;
    }

    public SuperImgResizer(Queue<File> files, int newWidth, String dstFolder, long start) {
        this.files = files;
        this.newWidth = newWidth;
        this.start = start;
        this.dstFolder = dstFolder;
    }

    private void createFile(BufferedImage image, String fileName) throws IOException {
        Files.deleteIfExists(Path.of(dstFolder + "/" + fileName));
        File newFile = new File(dstFolder + "/" + fileName);
        ImageIO.write(image, "jpg", newFile);
    }
}
