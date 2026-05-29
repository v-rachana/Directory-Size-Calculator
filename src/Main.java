package dirsize;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        boolean loadSeed = true;
        for (String arg : args) {
            if ("--no-seed".equalsIgnoreCase(arg)) {
                loadSeed = false;
            }
        }

        FileSystemService fs = new FileSystemService();
        if (loadSeed) {
            SeedLoader.seed(fs);
            System.out.println("Seed data loaded. Type 'ls' to explore or 'help' for commands.");
        } else {
            System.out.println("Empty file system. Type 'help' for available commands.");
        }

        printBanner();

        CommandParser parser = new CommandParser(fs);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(fs.getCurrentPath() + " $ ");
            String input;
            try {
                input = scanner.nextLine();
            } catch (Exception e) {
                break;
            }
            String response = parser.execute(input);
            if (response == null) {
                System.out.println("Goodbye!");
                break;
            }
            if (!response.isBlank()) {
                System.out.println(response);
            }
        }
        scanner.close();
    }

    private static void printBanner() {
        System.out.println("+======================================+");
        System.out.println("|   Directory Size Calculator  v1.0    |");
        System.out.println("+======================================+");
        System.out.println("Type 'help' for commands, 'exit' to quit.");
        System.out.println();
    }
}
