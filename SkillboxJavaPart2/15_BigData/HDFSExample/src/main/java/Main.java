public class Main
{
    private static String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) throws Exception
    {
        System.setProperty("HADOOP_USER_NAME", "root");

        FileAccess fileAccess = new FileAccess("hdfs://540cc67e0fa6:8020");
        String pathDir1 = "test001/01/";
        String pathDir2 = "test002";
        String pathFile1 = "test002/fileTest1.txt";
        String pathFile2 = "test002/internal/fileTest2.txt";
        fileAccess.create(pathDir1);
        fileAccess.create(pathDir2);
        fileAccess.create(pathFile1);
        fileAccess.create(pathFile2);

        String content = getRandomWord();
        System.out.println("=======================");
        System.out.println(content);
        System.out.println("======================");
        fileAccess.append(pathFile2, content);

        System.out.println("\nЧитаем файл:");
        System.out.println(fileAccess.read(pathFile2));
        fileAccess.read(pathFile2);
        System.out.println("\nПроверка является ли объект папкой/файлом:");
        if (fileAccess.isDirectory(pathDir2)) {
            System.out.println("\t" + pathDir2 + " - папка");
        } else {
            System.out.println("\t" + pathDir2 + " - файл");
        }
        if (fileAccess.isDirectory(pathFile1)) {
            System.out.println("\t" + pathFile1 + " - папка");
        } else {
            System.out.println("\t" + pathFile1 + " - файл");
        }
        System.out.println("\nВыводим все файлы и папки: ");
        fileAccess.list(pathDir2).forEach(System.out::println);
        System.out.println("\nУдаляем все файлы и папки:");
        fileAccess.delete(pathFile1);
        fileAccess.delete(pathFile2);
        fileAccess.delete(pathDir1);
        fileAccess.delete(pathDir2);
        fileAccess.hdfs.close();
    }

    private static String getRandomWord()
    {
        StringBuilder builder = new StringBuilder();
        int length = 2 + (int) Math.round(10 * Math.random());
        int symbolsCount = symbols.length();
        for(int i = 0; i < length; i++) {
            builder.append(symbols.charAt((int) (symbolsCount * Math.random())));
        }
        return builder.toString();
    }
}