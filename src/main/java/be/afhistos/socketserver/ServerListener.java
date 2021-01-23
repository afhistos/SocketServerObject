package be.afhistos.socketserver;

public interface ServerListener {

    void onStateChange(ObjectServer server);
    void onConnection(ObjectServer server);
    //void onPacket(ObjectServer server, ClientHandler client, ServerPacket packet);


}
