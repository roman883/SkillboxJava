import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileAccess {

    FileSystem hdfs;

    public FileAccess(String rootPath) {
        try {
            Configuration configuration = new Configuration();
            configuration.set("dfs.client.use.datanode.hostname", "true");
            hdfs = FileSystem.get(new URI(rootPath), configuration);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void create(String path) {
        String[] temp = path.split("/");
        Path hdfsPath = new Path(path);
        try {
            if (temp[temp.length - 1].contains(".")) { // если в конечной части строки есть символы разделенные точкой, то это файл
                if (hdfs.exists(hdfsPath)) {
                    System.out.println("Такой файл уже существует");
                } else {
                    hdfs.createNewFile(hdfsPath);
                    System.out.println("Создан файл " + path);
                }
            } else {
                if (hdfs.exists(hdfsPath)) {
                    System.out.println("Такая папка уже существует");
                } else {
                    hdfs.mkdirs(hdfsPath);
                    System.out.println("Создана папка - " + path);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Appends content to the file
     *
     * @param path
     * @param content
     */
    public void append(String path, String content) {
        try {
            Path hdfsWritePath = new Path(path);
            if (!isDirectory(path) && hdfs.exists(hdfsWritePath)) {
                InputStream inputStream = hdfs.open(hdfsWritePath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(hdfs.create(hdfsWritePath, true)));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(line);
                }
                bufferedWriter.write(content);
                bufferedReader.close();
                bufferedWriter.flush();
//                FSDataOutputStream fsDataOutputStream = hdfs.append(hdfsWritePath);
//                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fsDataOutputStream, StandardCharsets.UTF_8));
//                bufferedWriter.write(content);
//                bufferedWriter.newLine(); // Или не надо??
                System.out.println("Дописали заданный контент к файлу " + path);
                bufferedWriter.close();
            } else {
                System.out.println("По указанному адресу файла не существует");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String read(String path) {
        String result = null;
        try {
            Path hdfsReadPath = new Path(path);
            if (!isDirectory(path)) {
                InputStream inputStream = hdfs.open(hdfsReadPath); // FSDataInputStream
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) { // Пока остаются строки - печатаем
                    builder.append(line);
                }
                inputStream.close();
                bufferedReader.close();
                result = builder.toString();
            } else {
                System.out.println("По указанному адресу файла не существует");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void delete(String path) {
        Path deletePath = new Path(path);
        try {
            if (hdfs.exists(deletePath)) {
                hdfs.delete(deletePath, true);
                System.out.println("Удален объект по пути " + path);
            } else {
                System.out.println("По указанному пути ничего не найдено");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isDirectory(String path) {
        boolean result = false;
        try {
            Path checkPath = new Path(path);
//        String fileRegex = "\\.+.^/+";
            result = hdfs.isDirectory(checkPath);
//        return !path.matches(fileRegex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<String> list(String path) {
        Path listPath = new Path(path);
        List<String> resultList = null;
        try {
            if (isDirectory(path)) {
                    resultList = new ArrayList<>();
                    FileStatus[] tempArray = hdfs.listStatus(listPath);
                    for (FileStatus f : tempArray) {
                        resultList.add(f.getPath().toString());
                }
            } else {
                System.out.println("Директирии по указанному пути не найдено");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultList;
    }
}