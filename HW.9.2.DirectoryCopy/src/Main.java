import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            for (; ; ) {
                System.out.println("Введите путь к исходной папке: ");
                Path sourcePath = getPath(scanner);

                System.out.println("Введите путь для создания копии исходной папки: ");
                Path outputPath = getPath(scanner);

                // Получаем структуру файлов и папок в исходной папке (все дерево)
                ArrayList<Path> sourceFilesAndFolderList = createFilesAndFolderList(sourcePath);

                // Получаем все дерево файлов в формате Path
                ArrayList<Path> sourceFilesList = createAllFilesList(sourceFilesAndFolderList);
                System.out.println("Размер всех файлов в исходной папке : " + getSizeOfFolder(getSizeOfAllFiles(sourcePath)));

                ArrayList<Path> outputParentFolderList;
                outputParentFolderList = createTargetStructure(sourceFilesList, sourcePath, outputPath);

                System.out.println("Копируем всю структуру файлов и папок из " + sourcePath + " в папку " + outputPath);
                copyFiles(sourceFilesList, outputParentFolderList);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Path getPath(Scanner scanner) {
        return Path.of(scanner.nextLine());
    }

    private static ArrayList<Path> createFilesAndFolderList(Path path) throws IOException {
        return (ArrayList<Path>) Files.list(path).collect(Collectors.toList());
    }

    private static ArrayList<Path> createAllFilesList(ArrayList<Path> filesAndFolderList) throws IOException {
        ArrayList<Path> result = new ArrayList<>();
        for (Path i : filesAndFolderList) {
            if (Files.isDirectory(i)) {
                ArrayList<Path> tempFolderList = createFilesAndFolderList(i);
                result.addAll(createAllFilesList(tempFolderList));
            } else {
                result.add(i);
            }
        }
        return result;
    }

    private static Long getSizeOfAllFiles (Path path) throws IOException {
        return Files.walk(path).map(Path::toFile).filter(i -> !i.isDirectory()).mapToLong(File::length).sum();
    }

    private static String getSizeOfFolder(Long allFilesSizeByte) {
        int[] sizeMultiplier = {1, 1024, 1024 * 1024, 1024 * 1024 * 1024};
        String[] sizeMultiplierText = {"B", "kB", "MB", "GB"};
        String sizeOfFolder = "";
        if (allFilesSizeByte >= 0 && allFilesSizeByte < sizeMultiplier[1]) {
            sizeOfFolder = allFilesSizeByte + " " + sizeMultiplierText[0];
        } else if (allFilesSizeByte >= sizeMultiplier[1] && allFilesSizeByte < sizeMultiplier[2]) {
            sizeOfFolder = String.format("%,.3f", ((double) allFilesSizeByte / sizeMultiplier[1])) + " " + sizeMultiplierText[1];
        } else if (allFilesSizeByte >= sizeMultiplier[2] && allFilesSizeByte < sizeMultiplier[3]) {
            sizeOfFolder = String.format("%,.3f", ((double) allFilesSizeByte / sizeMultiplier[2])) + " " + sizeMultiplierText[2];
        } else {
            sizeOfFolder = String.format("%,.3f", ((double) allFilesSizeByte / sizeMultiplier[3])) + " " + sizeMultiplierText[3];
        }
        return sizeOfFolder;
    }

    private static ArrayList<Path> createTargetStructure(ArrayList<Path> sourceFilesList, Path sourcePath, Path outputPath) {
        ArrayList<Path> result = new ArrayList<>();
        for (Path i : sourceFilesList) {
            File file = new File(i.toString());
            result.add(Path.of(file.getParent().replace(sourcePath.toString(), outputPath.toString())));
        }
        return result;
    }

    private static void copyFiles(ArrayList<Path> sourceAllFilesList, ArrayList<Path> outputFolderList) throws IOException {

        ArrayList<String> filesAlreadyExists = new ArrayList<>();
        for (int j = 0; j < sourceAllFilesList.size(); j++) {
            String absoluteFileName = outputFolderList.get(j).toString() + "\\" + new File(sourceAllFilesList.get(j).toString()).getName();
            if (!Files.exists(outputFolderList.get(j))) {
                Files.createDirectories(outputFolderList.get(j));
                Files.copy(sourceAllFilesList.get(j), outputFolderList.get(j).resolve(sourceAllFilesList.get(j).getFileName()));
            } else {
                if (!Files.exists(Path.of(absoluteFileName))) {
                    Files.copy(sourceAllFilesList.get(j), outputFolderList.get(j).resolve(sourceAllFilesList.get(j).getFileName()));
                } else {
                    filesAlreadyExists.add(absoluteFileName);
                }
            }
        }
        if (filesAlreadyExists.isEmpty()) {
            System.out.println("Все файлы успешно скопированы" + "\n------------------");
        } else {
            System.out.println("Следующие файлы не скопированы, поскольку уже существуют в указанном месте:");
            filesAlreadyExists.forEach(System.out::println);
            System.out.println("-------------------");
        }
    }
}