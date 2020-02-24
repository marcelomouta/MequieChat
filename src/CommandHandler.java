import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CommandHandler {

    private static NetworkClient network;

    public CommandHandler(NetworkClient network) {
        this.network = network;
    }

    public void test() throws IOException, ArithmeticException {


        String fileName = "file.txt";

        FileInputStream inFile = new FileInputStream(fileName);

        int size = Math.toIntExact(new File(fileName).length());

        network.sendTestFile(inFile, size);
        System.out.println("Ficheiro '" + fileName + "' enviado.");

        inFile.close();
    }

    public String createGroup(String s) {
        //TODO
        return null;
    }

    public void add(String s, String s1) {
        //TODO
    }

    public void remove(String s, String s1) {
        //TODO
    }

    public String groupInfo() {
        //TODO
        return null;
    }

    public String userInfo() {
        //TODO
        return null;
    }

    public void message(String userID, String msg) {
        //TODO
    }

    public void photo(String userID, String fileName) {
        //TODO
    }

    public void collect(String groupID) {
        //TODO
    }

    public void history(String groupID) {
        //TODO
    }
}
