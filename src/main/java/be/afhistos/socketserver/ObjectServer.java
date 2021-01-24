package be.afhistos.socketserver;


import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class ObjectServer {
    private Logger logger;
    private String ip;
    private int port;
    private Thread serverThread;
    private ServerSocket serverSocket;
    private ServerState state;
    private List<ClientHandler> clients;
    private List<ServerListener> listeners;

    public ObjectServer(String ip, int port) {
        logger = Logger.getLogger(ObjectServer.class.getName());
        this.state = ServerState.OFF;
        this.ip = ip;
        this.port = port;
        clients = new ArrayList<>();
        listeners = new ArrayList<>();
        serverThread = getServerRunnable();
    }

    public ObjectServer(Properties props){
        this(props.getProperty("ip", "localhost"), Integer.parseInt(props.getProperty("port", "4444")));
    }

    public ObjectServer(String ip) {
        this(ip, 4444);
    }

    public ObjectServer(int port) {
        this("localhost", port);
    }
    public ObjectServer(){
        this("localhost", 4444);
    }

    public void start(){
        serverThread.start();
    }

    public void stop(){
        setState(ServerState.STOPPING);
        clients.forEach(ClientHandler::logout);
        clients.clear();
        serverThread.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            listeners.forEach(sl->sl.onError(this, e));
            e.printStackTrace();
        }
    }

    private Thread getServerRunnable(){
        return new Thread(() -> {
            setState(ServerState.LOADING);
            clients.clear();
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                listeners.forEach(sl->sl.onError(this, e));
                setState(ServerState.OFF);
            }
            setState(ServerState.RUNNING);
            while(state.equals(ServerState.RUNNING)){
                try {
                    ClientHandler client = new ClientHandler(this, serverSocket.accept());
                    clients.add(client);
                    client.start();
                    listeners.forEach(sl-> sl.onConnection(this, client));
                } catch (SocketException e){
                    listeners.forEach(sl->sl.onError(this,e));
                    logger.severe("Socket fermé de manière innatendue!");
                    stop();
                } catch (IOException e) {
                    listeners.forEach(sl-> sl.onError(this,e));
                    logger.severe("Impossible d'utiliser le port "+port);
                    stop();
                }
            }
        });
    }

    public String getIpAddress() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public ServerState getState() {
        return state;
    }

    private void setState(ServerState state) {
        this.state = state;
        listeners.forEach(sl -> sl.onStateChange(this));
    }

    public List<ClientHandler> getClients() {
        return clients;
    }
    public void addListener(ServerListener listener){
        listeners.add(listener);
    }
    public void removeListener(ServerListener listener){
        listeners.remove(listener);
    }

    public List<ServerListener> getListeners() {
        return listeners;
    }
}
