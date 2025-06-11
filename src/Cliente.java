// Cliente.java
import java.io.*;
import java.net.*;

public class Cliente extends Nodo {
    public String host = "localhost";
    public int puerto = 12345;
    private Integer comunicatio_time = 5;

    public Cliente(Integer id, Integer time) {
        super(id, time);
    }
    public void suma_comunication_time(){
        this.setTime(this.getTime() + comunicatio_time);
    }
    public void sincronizar() {
        try (Socket socket = new Socket(host, puerto)) {
            System.out.println("Cliente " + getId() + " conectado al servidor.");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // 1. Llegada de tiempo T0 del servidor
            Integer t0 = Integer.parseInt(entrada.readLine());
            System.out.println("Cliente " + getId() + " recibió T0: " + t0);
            suma_comunication_time();

            // 2. Calculo de diferencia de tiempo
            Integer dif = getTime() - t0;
            System.out.println("Cliente " + getId() + " calculó diferencia: " + dif +
                    " (T0:" + t0 + " - Tiempo local:" + getTime() + ")");

            // 3. Notifica diferencia de tiempo al servidor
            salida.println(dif);
            suma_comunication_time();

            // 4. Se recibe el ajuste del servidor
            suma_comunication_time();
            Integer ajuste = Integer.parseInt(entrada.readLine());
            System.out.println("Cliente " + getId() + " recibió ajuste: " + ajuste);

            // 5. Se suma el ajuste al tiempo local
            Integer tiempoOriginal = getTime();
            setTime(getTime() + ajuste);
            System.out.println("Cliente " + getId() + ": tiempo original = " + tiempoOriginal +
                    ", ajuste = " + ajuste + ", nuevo tiempo = " + getTime());

        } catch (IOException e) {
            System.out.println("Error en el cliente " + getId() + ": " + e.getMessage());
        }
    }
}
