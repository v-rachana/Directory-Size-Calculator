package dirsize;



import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileSystemService.
 *
 * Each test builds a small tree from scratch so tests are independent.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
class FileSystemServiceTest {

    private FileSystemService fs;

    @BeforeEach
    void setUp() {
        fs = new FileSystemService();
    }

    // ── mkdir ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("mkdir creates a child directory")
    void testMkdir() {
        fs.mkdir("home");
        FileNode child = fs.getCurrentDirectory().getChild("home");
        assertNotNull(child);
        assertTrue(child.isDirectory());
    }

    @Test
    @DisplayName("mkdir rejects duplicate names")
    void testMkdirDuplicate() {
        fs.mkdir("home");
        String result = fs.mkdir("home");
        assertTrue(result.startsWith("Error"), "Should report an error for duplicate");
    }

    // ── touch ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("touch creates a file with correct size")
    void testTouch() {
        fs.touch("readme.md", 1024);
        FileNode file = fs.getCurrentDirectory().getChild("readme.md");
        assertNotNull(file);
        assertTrue(file.isFile());
        assertEquals(1024, file.getSize());
    }

    @Test
    @DisplayName("touch rejects negative file size")
    void testTouchNegativeSize() {
        String result = fs.touch("bad.txt", -1);
        assertTrue(result.startsWith("Error"));
    }

    // ── cd ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cd descends into child directory")
    void testCdIntoChild() {
        fs.mkdir("docs");
        fs.changeDirectory("docs");
        assertEquals("/root/docs", fs.getCurrentPath());
    }

    @Test
    @DisplayName("cd .. returns to parent")
    void testCdParent() {
        fs.mkdir("docs");
        fs.changeDirectory("docs");
        fs.changeDirectory("..");
        assertEquals("/root", fs.getCurrentPath());
    }

    @Test
    @DisplayName("cd / goes back to root from deep path")
    void testCdRoot() {
        fs.mkdir("a");
        fs.changeDirectory("a");
        fs.mkdir("b");
        fs.changeDirectory("b");
        fs.changeDirectory("/");
        assertEquals("/root", fs.getCurrentPath());
    }

    @Test
    @DisplayName("cd .. at root stays at root")
    void testCdParentAtRoot() {
        String result = fs.changeDirectory("..");
        assertEquals("/root", fs.getCurrentPath());
        assertTrue(result.contains("root"), "Should report staying at root");
    }

    @Test
    @DisplayName("cd into non-existent directory reports error")
    void testCdMissing() {
        String result = fs.changeDirectory("ghost");
        assertTrue(result.startsWith("Error"));
    }

    @Test
    @DisplayName("cd into a file reports error")
    void testCdIntoFile() {
        fs.touch("data.txt", 100);
        String result = fs.changeDirectory("data.txt");
        assertTrue(result.startsWith("Error"));
    }

    // ── size ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("size of empty directory is 0")
    void testSizeEmpty() {
        long sz = fs.computeSize(fs.getCurrentDirectory());
        assertEquals(0, sz);
    }

    @Test
    @DisplayName("size sums direct files correctly")
    void testSizeFlatFiles() {
        fs.touch("a.txt", 500);
        fs.touch("b.txt", 300);
        long sz = fs.computeSize(fs.getCurrentDirectory());
        assertEquals(800, sz);
    }

    @Test
    @DisplayName("size recurses into sub-directories")
    void testSizeRecursive() {
        // root: file1(100)
        fs.touch("file1.txt", 100);
        // root/sub: file2(200), file3(300)
        fs.mkdir("sub");
        fs.changeDirectory("sub");
        fs.touch("file2.txt", 200);
        fs.touch("file3.txt", 300);
        fs.changeDirectory("..");
        // Total = 600
        long sz = fs.computeSize(fs.getCurrentDirectory());
        assertEquals(600, sz);
    }

    @Test
    @DisplayName("size recurses three levels deep")
    void testSizeDeepRecursion() {
        fs.mkdir("level1");
        fs.changeDirectory("level1");
        fs.mkdir("level2");
        fs.changeDirectory("level2");
        fs.touch("deep.bin", 1_000_000);
        fs.changeDirectory("/");

        long sz = fs.computeSize(fs.getCurrentDirectory());
        assertEquals(1_000_000, sz);
    }

    // ── ls ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ls shows empty message for empty directory")
    void testLsEmpty() {
        String result = fs.listDirectory();
        assertTrue(result.contains("empty"));
    }

    @Test
    @DisplayName("ls lists files and directories")
    void testLsContent() {
        fs.mkdir("src");
        fs.touch("README.md", 2048);
        String result = fs.listDirectory();
        assertTrue(result.contains("src"));
        assertTrue(result.contains("README.md"));
        assertTrue(result.contains("DIR"));
        assertTrue(result.contains("FILE"));
    }

    // ── formatSize ────────────────────────────────────────────────────────

    @Test
    @DisplayName("formatSize shows bytes for small values")
    void testFormatBytes() {
        assertEquals("512 B", FileSystemService.formatSize(512));
    }

    @Test
    @DisplayName("formatSize shows KB for values >= 1024")
    void testFormatKB() {
        String result = FileSystemService.formatSize(2048);
        assertTrue(result.endsWith("KB"));
    }

    @Test
    @DisplayName("formatSize shows MB for values >= 1 MB")
    void testFormatMB() {
        String result = FileSystemService.formatSize(1_048_576);
        assertTrue(result.endsWith("MB"));
    }
}
