package dirsize;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a node in the in-memory file system tree.
 * A node is either a FILE (leaf, has size) or a DIRECTORY (has children).
 */
public class FileNode {

    public enum Type { FILE, DIRECTORY }

    private final String name;
    private final Type type;
    private final long size;  // bytes; 0 for directories
    private final Map<String, FileNode> children; // null for files

    /** Constructor for a directory node. */
    public FileNode(String name) {
        this.name = name;
        this.type = Type.DIRECTORY;
        this.size = 0;
        this.children = new LinkedHashMap<>();
    }

    /** Constructor for a file node. */
    public FileNode(String name, long size) {
        this.name = name;
        this.type = Type.FILE;
        this.size = size;
        this.children = null;
    }

    public String getName() { return name; }
    public Type getType()   { return type; }
    public boolean isDirectory() { return type == Type.DIRECTORY; }
    public boolean isFile()      { return type == Type.FILE; }

    /** Raw size for files; 0 for directories (use FileSystemService#computeSize for recursive). */
    public long getSize() { return size; }

    public Map<String, FileNode> getChildren() { return children; }

    /**
     * Add a child node (directories only).
     * @throws IllegalStateException if called on a file node.
     */
    public void addChild(FileNode child) {
        if (!isDirectory()) throw new IllegalStateException("Cannot add children to a file.");
        children.put(child.getName(), child);
    }

    /**
     * Return the child with the given name, or null if absent.
     */
    public FileNode getChild(String name) {
        if (!isDirectory()) return null;
        return children.get(name);
    }

    @Override
    public String toString() {
        if (isFile()) return String.format("[FILE]  %s  (%d bytes)", name, size);
        return String.format("[DIR]   %s/", name);
    }
}
