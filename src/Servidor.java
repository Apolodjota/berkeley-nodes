// Servidor.java
import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor extends Nodo {
    private Integer puerto = 12345;
    private Integer numClientes = 3;
    private Integer comunicatio_time = 5;
    private List<Integer> ajustesClientes = new ArrayList<>();
    private List<Integer> dif_tiemposClientes = new ArrayList<>();

    public Servidor(Integer id, Integer time) {
        super(id, time);
    }

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor escuchando en el puerto " + puerto);

            Integer t0 = this.getTime();
            System.out.println("Servidor T0: " + t0);

            // Lista para almacenar las conexiones
            List<Socket> conexiones = new ArrayList<>();
            List<BufferedReader> entradas = new ArrayList<>();
            List<PrintWriter> salidas = new ArrayList<>();

            // Aceptar todas las conexiones de los clientes
            for (int i = 0; i < numClientes; i++) {
                Socket cliente = servidor.accept();
                conexiones.add(cliente);
                entradas.add(new BufferedReader(new InputStreamReader(cliente.getInputStream())));
                salidas.add(new PrintWriter(cliente.getOutputStream(), true));
                System.out.println("Cliente " + (i + 1) + " conectado");
            }

            // 1. El servidor difunde su tiempo actual
            for (int i = 0; i < numClientes; i++) {
                salidas.get(i).println(t0);
            }
            this.setTime(this.getTime() + comunicatio_time);

            // 2. Recibir diferencias de tiempos de cada cliente
            for (int i = 0; i < numClientes; i++) {
                Integer dif_tiempo = Integer.parseInt(entradas.get(i).readLine());
                dif_tiemposClientes.add(dif_tiempo);
                System.out.println("Diferencia del Cliente " + (i + 1) + ": " + dif_tiempo);
            }

            // 3. TLi
            this.setTime(this.getTime() + comunicatio_time);
            Integer tli = this.getTime();
            System.out.println("Servidor TLi: " + tli);

            // 4. Ajuste de diferencia segun formula
            for (int i = 0; i < numClientes; i++) {
                Integer ajuste = dif_tiemposClientes.get(i) - (tli - t0) / 2;
                ajustesClientes.add(ajuste);
                System.out.println("Ajuste Cliente " + (i + 1) + ": " + ajuste);
            }

            // 5. Calculo de la D
            Integer D = 0;
            for (int i = 0; i < numClientes; i++) {
                D = D + ajustesClientes.get(i);
            }
            D = D/numClientes;

            // 6. Incremento del server de tiempo + D
            this.setTime(this.getTime() + D);
            System.out.println("Tiempo del Servidor: " + getTime());

            // 7. Enviar ajustes a cada cliente
            for (int i = 0; i < numClientes; i++) {
                Integer ajusteFinal = D - ajustesClientes.get(i);
                salidas.get(i).println(ajusteFinal);
                System.out.println("Ajuste final enviado al Cliente " + (i + 1) + ": " + ajusteFinal);
            }
            this.setTime(this.getTime() + comunicatio_time);

            // Cerrar todas las conexiones
            for (int i = 0; i < numClientes; i++) {
                entradas.get(i).close();
                salidas.get(i).close();
                conexiones.get(i).close();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}
