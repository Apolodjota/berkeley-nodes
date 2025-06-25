// Cliente.java
import java.io.*;
import java.net.*;

public class Cliente extends Nodo {
    public String host = "localhost";
    public int puerto = 12345;
    public int puertoGrandulon = 12346;
    public int puertoBerkeley = 12347;
    public int puertoAgrawala = 12348;


    public Cliente(Integer id, Integer time, Integer time_mark, String estado, String tipo) {
        super(id, time, time_mark, estado, tipo);
    }

    public void sendToGrandulon() {
        try (Socket socket = new Socket(host, puertoGrandulon);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(this);
            System.out.println("Cliente sent to Grandulon server.");
        } catch (IOException e) {
            System.out.println("Error sending to server: " + e.getMessage());
        }
    }
    public void sendToBerkeley() {
        try (Socket socket = new Socket(host, puertoBerkeley);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(this);
            System.out.println("Cliente sent to Berkeley server.");
        } catch (IOException e) {
            System.out.println("Error sending to server: " + e.getMessage());
        }
    }
    public void sendToAgrawala() {
        try (Socket socket = new Socket(host, puertoAgrawala);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(this);
            System.out.println("Cliente sent to Agrawala server.");
        } catch (IOException e) {
            System.out.println("Error sending to server: " + e.getMessage());
        }
    }

    public void connectToNode(String host, int puerto) {
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(this);
            Nodo response = (Nodo) in.readObject();
            System.out.println("Connected to node: " + response.to_String());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error connecting to node: " + e.getMessage());
        }
    }

    public  void main(String[] args) {
        System.out.println("Ingrese las caracteristicas del nodo:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("ID: ");
            Integer id = Integer.parseInt(reader.readLine());
            System.out.print("Tiempo: ");
            Integer time = Integer.parseInt(reader.readLine());
            System.out.print("Marca de tiempo: ");
            Integer time_mark = Integer.parseInt(reader.readLine());
            System.out.print("Estado (R, W, H, F): ");
            String estado = reader.readLine();
            System.out.print("Tipo (CLIENT, SERVER): ");
            String tipo = reader.readLine();
            Cliente cliente = new Cliente(id, time, time_mark, estado, tipo);

            cliente.sendToGrandulon();
            cliente.sendToBerkeley();
            cliente.sendToAgrawala();

        } catch (IOException e) {
            System.out.println("Error al leer la entrada: " + e.getMessage());
        }
    }

}
