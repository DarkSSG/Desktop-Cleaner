import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DesktopOrganizer {

    private static final Map<String, String[]> extensionsFolders = new HashMap<>();

    static {
        extensionsFolders.put("Documents", new String[]{".pdf", ".doc", ".docx", ".txt", ".xls", ".xlsx", ".ppt", ".pptx", ".rtf", ".odt", ".html", ".htm", ".xml", ".csv", ".odp", ".psd", ".ai", ".eps", ".svg"});
        extensionsFolders.put("Images", new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".tif", ".svg", ".raw", ".webp", ".heic", ".heif", ".psd", ".eps"});
        extensionsFolders.put("Videos", new String[]{".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv", ".webm", ".mpg", ".mpeg", ".rm", ".rmvb", ".3gp", ".ogg", ".divx"});
        extensionsFolders.put("Music", new String[]{".mp3", ".wav", ".flac", ".aac", ".wma", ".aiff", ".mid", ".amr", ".m4a", ".ac3", ".dsd", ".opus", ".pcm", ".au", ".ra"});
        extensionsFolders.put("Archives", new String[]{".zip", ".rar", ".tar", ".gz", ".7z", ".tar.gz", ".tgz", ".tar.bz2", ".tbz", ".tar.xz", ".tar.Z", ".tar.lz", ".tar.sz ", ".cab", ".iso", ".deb", ".rpm", ".jar", ".tar.gz2", ".zipx"});
        extensionsFolders.put("Executables", new String[]{".exe", ".msi", ".bat", ".cmd", ".bin", ".run", ".sh", ".app", ".command", ".tool"});
        extensionsFolders.put("Others", new String[]{});
    }

    public static void main(String[] args) {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        while (true) {
            try {
                organizeFiles(desktopPath);
                Thread.sleep(5000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void organizeFiles(String desktopPath) throws IOException {
        String cleanedDesktopPath = desktopPath + File.separator + "CleanDesk";
        createDirectory(cleanedDesktopPath);

        for (String folder : extensionsFolders.keySet()) {
            createDirectory(cleanedDesktopPath + File.separator + folder);
        }

        File[] files = new File(desktopPath).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.getName().equals("CleanDesk") && !file.getName().startsWith(".")) {
                    moveFileToAppropriateFolder(cleanedDesktopPath, file);
                }
            }
        }
    }

    private static void createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static void moveFileToAppropriateFolder(String cleanedDesktopPath, File file) throws IOException {
        String fileName = file.getName();
        boolean fileMoved = false;

        for (Map.Entry<String, String[]> entry : extensionsFolders.entrySet()) {
            for (String ext : entry.getValue()) {
                if (fileName.toLowerCase().endsWith(ext)) {
                    String[] yearMonth = getYearMonth(file);
                    String yearMonthFolder = cleanedDesktopPath + File.separator + entry.getKey() + File.separator + yearMonth[0] + File.separator + yearMonth[1];
                    createDirectory(yearMonthFolder);
                    moveFile(file, yearMonthFolder + File.separator + fileName);
                    fileMoved = true;
                    break;
                }
            }
            if (fileMoved) break;
        }

        if (!fileMoved) {
            moveFile(file, cleanedDesktopPath + File.separator + "Others" + File.separator + fileName);
        }
    }

    private static void moveFile(File sourceFile, String destinationPath) throws IOException {
        File destFile = new File(destinationPath);
        if (destFile.exists()) {
            destinationPath = generateUniqueFilename(destinationPath);
        }
        Files.move(sourceFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
    }

    private static String generateUniqueFilename(String filePath) {
        int counter = 1;
        String baseName = filePath.substring(0, filePath.lastIndexOf('.'));
        String extension = filePath.substring(filePath.lastIndexOf('.'));
        while (Files.exists(Paths.get(filePath))) {
            filePath = baseName + "_" + counter++ + extension;
        }
        return filePath;
    }

    private static String[] getYearMonth(File file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        long time = attr.lastModifiedTime().toMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        String month = sdf.format(time);
        sdf.applyPattern("yyyy");
        String year = sdf.format(time);
        return new String[]{year, month};
    }
}
