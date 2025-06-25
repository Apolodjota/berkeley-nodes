import java.io.*;
import java.net.*;

public class Main {
    public void startServer() {
        try {
            ServerSocket server = new ServerSocket(12345);
            Socket cliente = server.accept();
            ObjectInputStream in = new ObjectInputStream(cliente.getInputStream());
            Nodo nodoRecibido = (Nodo) in.readObject();
            in.close();
            cliente.close();
            server.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

    }
}
