package be.afhistos.socketserver;

import java.net.Socket;

public class ClientHandler{
    private ObjectServer server;

    public ClientHandler(ObjectServer server, Socket accept) {
        this.server = server;
    }

    public ObjectServer getServer() {
        return server;
    }

}
