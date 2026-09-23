import java.util.List;

public class CommandHadler {

    public void execute(String command, List<String> args) {
        if (command.equals("ls")) {
            executeLS(args);
        }
        else if (command.equals("cd")) {
            executeCd(args);
        }
        else {
            throw new IllegalArgumentException("unknow command " + command);
        }
    }

    private void executeLS(List<String> args){
        System.out.println("ls");
        for (String arg : args) {
            System.out.println(" argument: " + arg);
        }
    }
    private void executeCd(List<String> args){
        System.out.println("cd");
        for (String arg : args) {
            System.out.println(" argument: " + arg);
        }
    }
}
