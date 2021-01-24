import be.afhistos.socketserver.ClientHandler;
import be.afhistos.socketserver.ObjectServer;

import java.util.logging.Logger;

public class Main {
    private static Thread t;
    private static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws InterruptedException {
        ObjectServer s = null;
        try {
            System.out.println(s.getIpAddress());

        }catch (NullPointerException e){
            //logger.severe("Impossible d'utiliser le port ");
            //logger.throwing("ObjectServer.java", "getServerRunnable", e.getCause());
        }
    }

    private static Thread getGet(){
        return new Thread(() -> {
            boolean running = true;
            while(running){
                System.out.println("Hnello");
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        });
    }
}
