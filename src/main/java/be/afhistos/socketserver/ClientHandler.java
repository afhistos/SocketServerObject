package be.afhistos.socketserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Deque;
import java.util.LinkedList;

public class ClientHandler{
    private ObjectServer server;
    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Thread inThread, outThread;
    private Deque<ServerPacket> packetDeque;
    private String guildId;

    public ServerPacket logoutPacket = new ServerPacket("LOGOUT", null);

    public ClientHandler(ObjectServer server, Socket s) {
        this.server = server;
        this.s = s;
        packetDeque = new LinkedList<>();
    }

    public void start(){
        try {
            in = new ObjectInputStream(s.getInputStream());
            out =  new ObjectOutputStream(s.getOutputStream());
            inThread = getInThread();
            outThread = getOutThread();
            inThread.start();
            outThread.start();
        } catch (IOException e) {
            server.getListeners().forEach(sl->{sl.onConnectionError(server,this,e);});
        }
    }

    public void logout(){
        server.addListener(new ServerListener() {
            @Override
            public void onPacketSent(ObjectServer server, ClientHandler client, ServerPacket packet) {
                if(packet == logoutPacket){
                    inThread.interrupt();
                    outThread.interrupt();
                    try {
                        in.close();
                        out.close();
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    packetDeque.clear();
                }
            }
        });
        packetDeque.offerFirst(logoutPacket);

    }

    public void sendPacket(ServerPacket packet){
        packetDeque.offer(packet);
    }

    private Thread getInThread(){
        if(in ==  null){return null;}
        return new Thread(()->{
            while(server.getState()==ServerState.RUNNING){
                server.getListeners().forEach(sl-> {
                    try {
                        sl.onPacketReceived(server,this,(ServerPacket) in.readObject());
                    } catch (IOException | ClassNotFoundException e) {
                        sl.onConnectionError(server, this, e);
                    }
                });
            }
        });
    }

    private Thread getOutThread() {
        if(out == null){return null;}
        return new Thread(()->{
           while(server.getState()==ServerState.RUNNING){
               packetDeque.forEach(serverPacket -> {
                   try {
                       out.writeObject(serverPacket);
                       server.getListeners().forEach(sl->{sl.onPacketSent(server, this, serverPacket);});
                   } catch (IOException e) {
                       server.getListeners().forEach(sl->{sl.onConnectionError(server, this, e);});
                   }
               });
           }
        });
    }

    public String getGuildId() {
        return guildId;
    }

    private void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public void setLogoutPacket(ServerPacket logoutPacket) {
        this.logoutPacket = logoutPacket;
    }
}
