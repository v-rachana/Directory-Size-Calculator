package dirsize;



import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests for CommandParser – exercises the full command dispatch.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CommandParserTest {

    private FileSystemService fs;
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        fs = new FileSystemService();
        parser = new CommandParser(fs);
    }

    @Test
    @DisplayName("help returns non-empty help text")
    void testHelp() {
        String result = parser.execute("help");
        assertNotNull(result);
        assertTrue(result.contains("cd") && result.contains("ls") && result.contains("size"));
    }

    @Test
    @DisplayName("unknown command returns error message")
    void testUnknownCommand() {
        String result = parser.execute("rm -rf /");
        assertTrue(result.toLowerCase().contains("unknown"));
    }

    @Test
    @DisplayName("exit returns null (REPL termination signal)")
    void testExit() {
        assertNull(parser.execute("exit"));
        assertNull(parser.execute("quit"));
    }

    @Test
    @DisplayName("blank input returns empty string (no crash)")
    void testBlankInput() {
        String result = parser.execute("   ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("mkdir + cd + ls round-trip")
    void testMkdirCdLs() {
        parser.execute("mkdir docs");
        parser.execute("cd docs");
        parser.execute("touch notes.txt 1024");
        String ls = parser.execute("ls");
        assertTrue(ls.contains("notes.txt"));
    }

    @Test
    @DisplayName("touch with non-numeric size reports error")
    void testTouchBadSize() {
        String result = parser.execute("touch file.txt abc");
        assertTrue(result.startsWith("Error"));
    }

    @Test
    @DisplayName("touch missing args returns usage hint")
    void testTouchMissingArgs() {
        String result = parser.execute("touch onlyname");
        assertTrue(result.toLowerCase().contains("usage"));
    }

    @Test
    @DisplayName("cd missing args returns usage hint")
    void testCdMissingArgs() {
        String result = parser.execute("cd");
        assertTrue(result.toLowerCase().contains("usage"));
    }

    @Test
    @DisplayName("size after seeding directory")
    void testSizeCommand() {
        parser.execute("mkdir project");
        parser.execute("cd project");
        parser.execute("touch app.jar 500000");
        String result = parser.execute("size");
        assertTrue(result.contains("500000") || result.contains("488") /* KB */);
    }

    @Test
    @DisplayName("pwd shows current path")
    void testPwd() {
        parser.execute("mkdir src");
        parser.execute("cd src");
        String result = parser.execute("pwd");
        assertTrue(result.endsWith("src"));
    }
}
