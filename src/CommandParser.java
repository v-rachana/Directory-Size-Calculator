package dirsize;



public class CommandParser {

    private final FileSystemService fs;

    public CommandParser(FileSystemService fs) {
        this.fs = fs;
    }

    public String execute(String rawInput) {
        if (rawInput == null) return null;
        String line = rawInput.trim();
        if (line.isEmpty()) return "";

        String[] parts = line.split("\\s+", 3);
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "cd":
                if (parts.length < 2) return "Usage: cd <directory>";
                return fs.changeDirectory(parts[1]);
            case "ls":
                return fs.listDirectory();
            case "size":
                return fs.sizeOfCurrentDirectory();
            case "mkdir":
                if (parts.length < 2) return "Usage: mkdir <name>";
                return fs.mkdir(parts[1]);
            case "touch":
                if (parts.length < 3) return "Usage: touch <name> <size_in_bytes>";
                try {
                    long sz = Long.parseLong(parts[2]);
                    return fs.touch(parts[1], sz);
                } catch (NumberFormatException e) {
                    return "Error: size must be a number in bytes.";
                }
            case "pwd":
                return fs.getCurrentPath();
            case "help":
                return getHelp();
            case "exit":
            case "quit":
                return null;
            default:
                return "Unknown command: '" + cmd + "'. Type 'help' for available commands.";
        }
    }

    private String getHelp() {
        return "+---------------------------+------------------------------+\n"
             + "|  cd <name>                |  Change into a sub-directory |\n"
             + "|  cd ..                    |  Move to parent directory    |\n"
             + "|  cd /                     |  Move to root                |\n"
             + "|  ls                       |  List current directory      |\n"
             + "|  size                     |  Total size (recursive)      |\n"
             + "|  mkdir <name>             |  Create a sub-directory      |\n"
             + "|  touch <name> <bytes>     |  Create a file with a size   |\n"
             + "|  pwd                      |  Print current path          |\n"
             + "|  help                     |  Show this help              |\n"
             + "|  exit / quit              |  Exit the application        |\n"
             + "+---------------------------+------------------------------+";
    }
}
