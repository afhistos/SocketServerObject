package be.afhistos.socketserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ObjectServer {
    private int port;
    private Thread serverThread;
    private ServerSocket serverSocket;
    private ServerState state;
    private List<ClientHandler> clients;
    private List<ServerListener> listeners;

    public ObjectServer(int port) {
        this.state = ServerState.OFF;
        this.port = port;
        clients = new ArrayList<>();
        listeners = new ArrayList<>();
        serverThread = getServerRunnable();
    }

    public ObjectServer(Properties props){
        this(Integer.parseInt(props.getProperty("port", "44444")));
    }


    public ObjectServer(){
        this(44444);
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
        setState(ServerState.OFF);
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
                } catch (SocketException ign){
                    /*
                    Error thrown when the server closes.
                    Because ServerSocket#accept is blocking, the Thread does not terminate when calling interrupt(),
                    but the socket is still closed.
                    So just ignore this error.
                     */
                } catch (IOException e) {
                    listeners.forEach(sl-> sl.onError(this,e));
                    stop();
                }
            }
        });
    }

    public int getPort() {
        return port;
    }

    public ServerState getState() {
        return state;
    }

    private void setState(ServerState state) {
        ServerState oldState = this.state;
        this.state = state;
        listeners.forEach(sl -> sl.onStateChange(this, oldState, state));
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
