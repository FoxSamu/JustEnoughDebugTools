package net.shadew.debug.util;

import java.nio.file.Path;

public class PathUtil {
    public static Path resolve(Path root, Path path) {
        if (path.toFile().isAbsolute())
            return path;
        return root.resolve(path);
    }

    public static Path resolve(Path root, String path) {
        return resolve(root, root.getFileSystem().getPath(path));
    }
}
