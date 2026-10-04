import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.nio.charset.StandardCharsets;

public class VirtualFileSystem {

    private final Map<String, byte[]> files = new HashMap<>();
    private final List<String> directories = new ArrayList<>();

    public VirtualFileSystem(String zipPath) {
        load(zipPath);
    }

    private void load(String zipPath) {
        Path path = Path.of(zipPath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException(
                    "VFS archive not found: " + zipPath
            );
        }

        try (InputStream input = Files.newInputStream(path);
             ZipInputStream zip = new ZipInputStream(input)) {

            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                String entryPath = normalize(entry.getName());

                if (entry.isDirectory()) {
                    addDirectory(entryPath);
                } else {
                    addFile(entryPath, zip);
                }
            }

        } catch (ZipException e) {
            throw new IllegalArgumentException(
                    "Invalid VFS archive: " + zipPath
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Cannot load VFS archive: " + zipPath
            );
        }
    }

    private void addFile(String path, ZipInputStream zip)
            throws IOException {

        files.put(path, readBytes(zip));

        addParentDirectories(path);
    }

    private void addDirectory(String path) {
        if (!path.isEmpty() && !directories.contains(path)) {
            directories.add(path);
        }

        addParentDirectories(path);
    }

    private void addParentDirectories(String path) {
        int slash = path.lastIndexOf('/');

        while (slash > 0) {
            String parent = path.substring(0, slash);

            if (!directories.contains(parent)) {
                directories.add(parent);
            }

            slash = parent.lastIndexOf('/');
        }
    }

    private byte[] readBytes(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int count;

        while ((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }

        return output.toByteArray();
    }

    public List<String> list(String directory) {
        String normalized = normalize(directory);

        List<String> result = new ArrayList<>();

        for (String path : files.keySet()) {
            addChild(result, normalized, path);
        }

        for (String path : directories) {
            addChild(result, normalized, path);
        }

        result.sort(Comparator.naturalOrder());

        return result;
    }

    private void addChild(
            List<String> result,
            String directory,
            String path
    ) {
        String prefix = directory.isEmpty()
                ? ""
                : directory + "/";

        if (!path.startsWith(prefix)) {
            return;
        }

        String child = path.substring(prefix.length());

        if (!child.isEmpty() && !child.contains("/")) {
            result.add(child);
        }
    }

    public boolean isDirectory(String path) {
        String normalized = normalize(path);

        return normalized.isEmpty()
                || directories.contains(normalized);
    }

    public boolean exists(String path) {
        String normalized = normalize(path);

        return normalized.isEmpty()
                || files.containsKey(normalized)
                || directories.contains(normalized);
    }

    public boolean isFile(String path) {
        return files.containsKey(normalize(path));
    }

    public String readFile(String path) {
        String normalized = normalize(path);

        if (!files.containsKey(normalized)) {
            throw new IllegalArgumentException(
                    "file not found: " + path
            );
        }

        return new String(
                files.get(normalized),
                StandardCharsets.UTF_8
        );
    }

    private String normalize(String path) {
        String result = path.replace('\\', '/');

        while (result.startsWith("/")) {
            result = result.substring(1);
        }

        while (result.endsWith("/") && !result.isEmpty()) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }
}