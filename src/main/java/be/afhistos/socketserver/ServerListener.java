package be.afhistos.socketserver;

public interface ServerListener {

    default void onStateChange(ObjectServer server, ServerState oldState, ServerState newState){};
    default void onConnection(ObjectServer server, ClientHandler client){};
    default void onError(ObjectServer server, Exception e){};//Error from server
    default void onConnectionError(ObjectServer server, ClientHandler client, Exception e){};//Error at connexion with client (socket closed,...)
    default void onPacketReceived(ObjectServer server, ClientHandler client, ServerPacket packet){};
    default void onPacketSent(ObjectServer server, ClientHandler client, ServerPacket packet){};


}
