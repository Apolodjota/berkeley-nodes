import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente extends Nodo implements Serializable {
    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;
    private transient Socket socket;
    private transient ObjectOutputStream out;

    public Cliente(Integer id, Integer time, String estado, String tipo) {
        super(id, time, estado, tipo);
    }

    public boolean conectarAlServidor() {
        try {
            socket = new Socket(HOST, PUERTO);
            out = new ObjectOutputStream(socket.getOutputStream());

            // Send the node to the server
            out.writeObject(this);
            out.flush();

            System.out.println("Connected to the main server");
            System.out.println("Sending: " + this.toString());

            out.close();
            socket.close();
            return true;

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(" === CLIENT OF THE DISTRIBUTED SYSTEM ===");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Example: [id = 1, time = 10, estado = 'R', tipo = 'client']");
        System.out.print("Enter the client node: ");

        String input = scanner.nextLine();
        String[] parts = input.split(",");

        if (parts.length != 4) {
            System.out.println("Incorrect format.");
            return;
        }

        try {
            Integer id = Integer.parseInt(parts[0].split("=")[1].trim());
            Integer time = Integer.parseInt(parts[1].split("=")[1].trim());
            String estado = parts[2].split("=")[1].trim().replace("'", "").replace("\"", "");
            String tipo = parts[3].split("=")[1].trim().replace("'", "").replace("\"", "");

            System.out.println("\n" + "=".repeat(50));
            System.out.println("Creating node:");

            Cliente cliente = new Cliente(id, time, estado, tipo);
            System.out.println("✓ " + cliente.toString());
            System.out.println("=".repeat(50));

            // Connect to the server
            if (cliente.conectarAlServidor()) {
                System.out.println("Node sent. Exiting client.");
            } else {
                System.out.println("Could not connect to the server.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Error parsing id or time. Make sure they are integers.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error in estado or tipo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}