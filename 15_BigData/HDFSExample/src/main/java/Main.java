public class Main
{
    private static String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) throws Exception
    {
        System.setProperty("HADOOP_USER_NAME", "root");

        FileAccess fileAccess = new FileAccess("hdfs://119f0f2a516c:8020");
        String pathDirectoryCreate = "test902/56/";
        String path2 = "test901";
        String path3 = "test902/fileTest2.txt";
        fileAccess.create(pathDirectoryCreate);
        fileAccess.create(path2);
        fileAccess.create(path3);
        fileAccess.create("test902/00/fileTest3.txt");

//        fileAccess.delete(pathDirectoryCreate);
        String content = getRandomWord();
        System.out.println("=======================");
        System.out.println(content);
        System.out.println("======================");
        fileAccess.append(path3, content);

        System.out.println("\nЧитаем файл");
        System.out.println(fileAccess.read(path3));
        fileAccess.read(path2);
        if (fileAccess.isDirectory(path3)) {
            System.out.println(path3 + " это папка");
        } else {
            System.out.println(path3 + " это файл");
        }
        if (fileAccess.isDirectory(path2)) {
            System.out.println(path2 + " это папка");
        } else {
            System.out.println(path2 + " это файл");
        }
        System.out.println("Выводим все файлы: ");
        fileAccess.list("test902").forEach(System.out::println);

        fileAccess.delete(pathDirectoryCreate);
        fileAccess.delete(path2);
        fileAccess.delete(path3);
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
