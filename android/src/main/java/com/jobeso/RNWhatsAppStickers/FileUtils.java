import java.io.File;

public class FileUtils {
    public static boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return true;
        }

        if (!directory.isDirectory()) {
            return false;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively delete subdirectories
                    deleteDirectory(file);
                } else {
                    // Delete files
                    if (!file.delete()) {
                        return false;
                    }
                }
            }
        }

        // Delete the empty directory itself
        return directory.delete();
    }
}
