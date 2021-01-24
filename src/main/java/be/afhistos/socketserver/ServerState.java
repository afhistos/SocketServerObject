package be.afhistos.socketserver;

import java.io.Serializable;

public enum ServerState implements Serializable {
    OFF, LOADING, RUNNING,STOPPING;
    private static final long serialVersionUID = 24L;
}
