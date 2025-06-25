import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente extends Nodo {
    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;

    public Cliente(Integer id, Integer time, String estado, String tipo) {
        super(id, time, estado, tipo);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingres el nodo cliente con este formato [id = n, time = n, estado = 'estado', tipo = 'tipo']:");
        String input = scanner.nextLine();
        String[] parts = input.split(",");
        if (parts.length != 4) {
            System.out.println("Formato incorrecto. Debe ser [id = n, time = n, estado = 'estado', tipo = 'tipo']");
            return;
        }
        //crear cliente
        try {
            Integer id = Integer.parseInt(parts[0].split("=")[1].trim());
            Integer time = Integer.parseInt(parts[1].split("=")[1].trim());
            String estado = parts[2].split("=")[1].trim().replace("'", "");
            String tipo = parts[3].split("=")[1].trim().replace("'", "");
            Cliente cliente = new Cliente(id, time, estado, tipo);
            System.out.println(cliente.toString());
            //Conectar al main
            try {
                Socket socket = new Socket("localhost", PUERTO);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(cliente);
            } catch (IOException e) {
                e.printStackTrace();
            }




        } catch (NumberFormatException e) {
            System.out.println("Error al parsear id o time. Asegúrese de que sean números enteros.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error en el estado o tipo: " + e.getMessage());
        }



    }

}