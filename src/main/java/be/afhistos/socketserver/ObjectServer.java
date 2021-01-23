package be.afhistos.socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ObjectServer {
    private String ip;
    private int port;
    private Thread serverThread;
    private ServerSocket serverSocket;
    private ServerState state;
    private List<ClientHandler> clients;
    private List<ServerListener> listeners;

    public ObjectServer(String ip, int port) {
        this.state = ServerState.OFF;
        this.ip = ip;
        this.port = port;
        clients = new ArrayList<>();
        listeners = new ArrayList<>();
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
        for(ServerListener sl : listeners){
            sl.onStateChange(this);
        }
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    private Thread getServerRunnable(){
        return new Thread(() -> {
            setState(ServerState.LOADING);
            clients.clear();
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setState(ServerState.RUNNING);
            while(state.equals(ServerState.RUNNING)){
                try {
                    ClientHandler client = new ClientHandler(this, serverSocket.accept());
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
