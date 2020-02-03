import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Main {

    static int newWidth = 300;
    static String srcFolder = "E:/ТестФото";
    static String dstFolder = "E:/ТестФото_Out";
    static String dstFolder2 = "E:/ТестФото_Out2";
    static Queue<File> filesQueue = new ConcurrentLinkedQueue<File>();

    public static void main(String[] args) {
        File srcDir = new File(srcFolder);
        Long start = System.currentTimeMillis();
        filesQueue.addAll(Arrays.asList(Objects.requireNonNull(srcDir.listFiles())));

//         Включение ImageResizer с методом "ближайший сосед", качество хуже. Extens Thread
//        ImageResizer imageResizerThread1 = new ImageResizer(filesQueue, newWidth, dstFolder2, start);
//        ImageResizer imageResizerThread2 = new ImageResizer(filesQueue, newWidth, dstFolder2, start);
//        ImageResizer imageResizerThread3 = new ImageResizer(filesQueue, newWidth, dstFolder2, start);
//        ImageResizer imageResizerThread4 = new ImageResizer(filesQueue, newWidth, dstFolder2, start);
//        imageResizerThread1.start();
//        imageResizerThread2.start();
//        imageResizerThread3.start();
//        imageResizerThread4.start();

        // SuperImageResizer - уменьшение с суперсэмплингом, уменьшение за несколько проходов,
        // кратно финальному размеру фото. Шаг дробный.    Implements Runnable
        new Thread(new SuperImgResizer(filesQueue, newWidth, dstFolder, start)).start();
        new Thread(new SuperImgResizer(filesQueue, newWidth, dstFolder, start)).start();
        new Thread(new SuperImgResizer(filesQueue, newWidth, dstFolder, start)).start();
        new Thread(new SuperImgResizer(filesQueue, newWidth, dstFolder, start)).start();
    }
}
