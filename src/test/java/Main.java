import be.afhistos.socketserver.ClientHandler;
import be.afhistos.socketserver.ObjectServer;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ObjectServer server = new ObjectServer(44444);
        server.addListener(new VulcainListener());
        server.start();
        Thread.sleep(40000);
        server.stop();
    }

}
