import java.io.File;


public class Main {

    static int newWidth = 300;
    static String srcFolder = "E:/ТестФото";
    static String dstFolder = "E:/ТестФото_Out";
    static String dstFolder2 = "E:/ТестФото_Out2";

    public static void main(String[] args) {
        File srcDir = new File(srcFolder);
        Long start = System.currentTimeMillis();
        File[] files = srcDir.listFiles();
        int filesForThread = files.length / 4;
        File[] filesThread1 = new File[filesForThread];
        File[] filesThread2 = new File[filesForThread];
        File[] filesThread3 = new File[filesForThread];
        File[] filesThread4 = new File[files.length - 3 * filesForThread];
        System.arraycopy(files, 0, filesThread1, 0, filesForThread);
        System.arraycopy(files, filesForThread, filesThread2, 0, filesForThread);
        System.arraycopy(files, (2 * filesForThread), filesThread3, 0, filesForThread);
        System.arraycopy(files, (3 * filesForThread), filesThread4, 0, (files.length - 3 * filesForThread));

//         Включение ImageResizer с методом "ближайший сосед", качество хуже. Extens Thread
//        ImageResizer imageResizerThread1 = new ImageResizer(filesThread1, newWidth, dstFolder2, start);
//        ImageResizer imageResizerThread2 = new ImageResizer(filesThread2, newWidth, dstFolder2, start);
//        ImageResizer imageResizerThread3 = new ImageResizer(filesThread3, newWidth, dstFolder2, start);
//        ImageResizer imageResizerThread4 = new ImageResizer(filesThread4, newWidth, dstFolder2, start);
//        imageResizerThread1.start();
//        imageResizerThread2.start();
//        imageResizerThread3.start();
//        imageResizerThread4.start();

        // SuperImageResizer - уменьшение с суперсэмплингом, уменьшение за несколько проходов,
        // кратно финальному размеру фото. Шаг дробный.    Implements Runnable
        new Thread(new SuperImgResizer(filesThread1, newWidth, dstFolder, start)).start();
        new Thread(new SuperImgResizer(filesThread2, newWidth, dstFolder, start)).start();
        new Thread(new SuperImgResizer(filesThread3, newWidth, dstFolder, start)).start();
        new Thread(new SuperImgResizer(filesThread4, newWidth, dstFolder, start)).start();
    }
}
