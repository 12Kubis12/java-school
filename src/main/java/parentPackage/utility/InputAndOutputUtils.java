package parentPackage.utility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class InputAndOutputUtils {

    private static Scanner scanner = new Scanner(System.in);
    private final static String readFilePath = "src/all_instances.txt";
    private final static String writeFilePath = "src/output.txt";

    public static String readLine() {
        return scanner.nextLine();
    }

    public static String readIntOption(List<String> options, boolean possibleStop) {
        String index;
        String chosenOption;
        while (true) {
            try {
                index = InputAndOutputUtils.readLine();
                if (index.equalsIgnoreCase("s") && possibleStop) {
                    chosenOption = index;
                    break;
                }
                chosenOption = options.remove(Integer.parseInt(index) - 1);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input try again!!!");
            }
        }
        return chosenOption;
    }

    public static boolean readFromFile() {
        File file = new File(readFilePath);
        try {
            scanner = new Scanner(file);
        } catch (Exception e) {
            System.out.println("File not found!!!");
            return false;
        }
        return true;
    }

    public static boolean checkFileLine() {
        return scanner.hasNextLine();
    }

    public static void writeToFile(String stringToWrite) {
        try {
            Files.writeString(Path.of(writeFilePath), stringToWrite);
        } catch (Exception e) {
            System.out.println("Something is wrong with file write!");
        }
    }
}
