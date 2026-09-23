import java.util.*;

public class Shell {

    private String vfsName;
    private CommandParser parser;
    private CommandHadler handler;

    public Shell (String vfsName) {
        this.vfsName = vfsName;
        this.parser = new CommandParser();
        this.handler = new CommandHadler();
    }

    public void process() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(vfsName + "> ");

            String line = scanner.nextLine();

            try {
                List<String> tockens = parser.parse(line);

                String command = tockens.get(0);
                List<String> args = tockens.subList(1, tockens.size());
                if (command.equals("exit")) {
                    System.out.println("The session is over");
                    break;
                }
                handler.execute(command, args);
            }
            catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

}
