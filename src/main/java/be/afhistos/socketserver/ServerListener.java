package be.afhistos.socketserver;

public interface ServerListener {
    public enum ServerEventType{STATE_CHANGE, CONNECTION, ERROR}

    void onStateChange(ObjectServer server);
    void onConnection(ObjectServer server, ClientHandler client);
    void onError(ObjectServer server, Exception e);
    //void onPacket(ObjectServer server, ClientHandler client, ServerPacket packet);


}
