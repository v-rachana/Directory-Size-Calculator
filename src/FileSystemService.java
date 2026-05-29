package dirsize;



import java.util.ArrayList;
import java.util.List;

/**
 * Manages the in-memory virtual file system.
 *
 * Responsibilities:
 *  - Maintain the root node and the current working directory stack
 *  - Implement cd, ls, size, mkdir, touch commands
 */
public class FileSystemService {

    private final FileNode root;
    private final List<FileNode> pathStack; // breadcrumb from root to cwd

    public FileSystemService() {
        root = new FileNode("root");
        pathStack = new ArrayList<>();
        pathStack.add(root);
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    /** The node the user is currently inside. */
    public FileNode getCurrentDirectory() {
        return pathStack.get(pathStack.size() - 1);
    }

    /** Human-readable path string, e.g. "/root/home/user". */
    public String getCurrentPath() {
        StringBuilder sb = new StringBuilder();
        for (FileNode node : pathStack) {
            sb.append("/").append(node.getName());
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // cd
    // ------------------------------------------------------------------

    /**
     * Change directory.
     *
     * Supports:
     *  - "."   → stay
     *  - ".."  → parent
     *  - "/"   → root
     *  - simple child name
     *
     * @return result message
     */
    public String changeDirectory(String target) {
        target = target.trim();

        switch (target) {
            case ".":
                return "Stayed in " + getCurrentPath();

            case "..":
                if (pathStack.size() == 1) {
                    return "Already at root.";
                }
                pathStack.remove(pathStack.size() - 1);
                return "Changed to " + getCurrentPath();

            case "/":
                pathStack.clear();
                pathStack.add(root);
                return "Changed to " + getCurrentPath();

            default:
                FileNode cwd = getCurrentDirectory();
                FileNode child = cwd.getChild(target);
                if (child == null) {
                    return "Error: No such directory '" + target + "'";
                }
                if (!child.isDirectory()) {
                    return "Error: '" + target + "' is a file, not a directory";
                }
                pathStack.add(child);
                return "Changed to " + getCurrentPath();
        }
    }

    // ------------------------------------------------------------------
    // ls
    // ------------------------------------------------------------------

    /**
     * List contents of the current directory.
     * @return formatted multi-line string
     */
    public String listDirectory() {
        FileNode cwd = getCurrentDirectory();
        if (cwd.getChildren().isEmpty()) {
            return "(empty directory)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s %-12s %s%n", "TYPE", "SIZE", "NAME"));
        sb.append("-".repeat(40)).append("\n");

        for (FileNode child : cwd.getChildren().values()) {
            if (child.isDirectory()) {
                sb.append(String.format("%-8s %-12s %s/%n", "DIR", "-", child.getName()));
            } else {
                sb.append(String.format("%-8s %-12s %s%n", "FILE",
                        formatSize(child.getSize()), child.getName()));
            }
        }
        return sb.toString().trim();
    }

    // ------------------------------------------------------------------
    // size (recursive)
    // ------------------------------------------------------------------

    /**
     * Recursively compute the total size of the current directory.
     * @return formatted result message
     */
    public String sizeOfCurrentDirectory() {
        FileNode cwd = getCurrentDirectory();
        long total = computeSize(cwd);
        return String.format("Total size of '%s': %d bytes (%s)",
                getCurrentPath(), total, formatSize(total));
    }

    /**
     * Recursive helper – sums sizes of all descendant files.
     */
    public long computeSize(FileNode node) {
        if (node.isFile()) {
            return node.getSize();
        }
        long total = 0;
        for (FileNode child : node.getChildren().values()) {
            total += computeSize(child);
        }
        return total;
    }

    // ------------------------------------------------------------------
    // mkdir / touch  (helper commands for building the tree)
    // ------------------------------------------------------------------

    /**
     * Create a sub-directory inside the current directory.
     */
    public String mkdir(String name) {
        FileNode cwd = getCurrentDirectory();
        if (cwd.getChild(name) != null) {
            return "Error: '" + name + "' already exists.";
        }
        cwd.addChild(new FileNode(name));
        return "Directory '" + name + "' created.";
    }

    /**
     * Create a file inside the current directory with a given size.
     */
    public String touch(String name, long size) {
        if (size < 0) return "Error: file size cannot be negative.";
        FileNode cwd = getCurrentDirectory();
        if (cwd.getChild(name) != null) {
            return "Error: '" + name + "' already exists.";
        }
        cwd.addChild(new FileNode(name, size));
        return String.format("File '%s' created (%s).", name, formatSize(size));
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /** Pretty-print a byte count. */
    public static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
