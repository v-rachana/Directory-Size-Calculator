package dirsize;

public class SeedLoader {

    public static void seed(FileSystemService fs) {
        run(fs, "mkdir home");
        run(fs, "cd home");
        run(fs, "mkdir rachana");
        run(fs, "cd rachana");
        run(fs, "mkdir documents");
        run(fs, "cd documents");
        run(fs, "touch resume.pdf 204800");
        run(fs, "touch cover_letter.docx 51200");
        run(fs, "touch notes.txt 2048");
        run(fs, "cd ..");
        run(fs, "mkdir projects");
        run(fs, "cd projects");
        run(fs, "mkdir dirsize-app");
        run(fs, "cd dirsize-app");
        run(fs, "touch Main.java 3500");
        run(fs, "touch FileNode.java 2900");
        run(fs, "touch build.xml 1024");
        run(fs, "cd ..");
        run(fs, "mkdir ml-model");
        run(fs, "cd ml-model");
        run(fs, "touch model.pkl 5242880");
        run(fs, "touch train.py 8192");
        run(fs, "cd ..");
        run(fs, "cd ..");
        run(fs, "mkdir music");
        run(fs, "cd music");
        run(fs, "touch song1.mp3 4194304");
        run(fs, "touch song2.mp3 5000000");
        run(fs, "cd ..");
        run(fs, "cd ..");
        run(fs, "cd ..");
        run(fs, "mkdir etc");
        run(fs, "cd etc");
        run(fs, "touch config.cfg 512");
        run(fs, "touch hosts 256");
        run(fs, "cd /");
    }

    private static void run(FileSystemService fs, String cmd) {
        String[] parts = cmd.split("\\s+", 3);
        if (parts[0].equals("mkdir")) {
            fs.mkdir(parts[1]);
        } else if (parts[0].equals("cd")) {
            fs.changeDirectory(parts[1]);
        } else if (parts[0].equals("touch")) {
            fs.touch(parts[1], Long.parseLong(parts[2]));
        }
    }
}
