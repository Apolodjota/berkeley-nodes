import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {
    private static final Integer PUERTO = 12345;
    private List<Nodo> clientes = new ArrayList<>();

    public void inicio() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(this.PUERTO);
        System.out.println("Servidor esperando conexiones...");
        Scanner scanner = new Scanner(System.in);
        boolean complete = false;

        while (!complete) {
            try (Socket clientSocket = serverSocket.accept();
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                Nodo cliente = (Nodo) in.readObject();
                this.clientes.add(cliente);
                System.out.println("Cliente recibido y guardado: " + cliente.toString());

                System.out.println("Total de nodos conectados: " + this.clientes.size());
                System.out.print("¿Está todo el sistema distribuido completo? (Y/n): ");
                String respuesta = scanner.nextLine().trim().toLowerCase();
                if (respuesta.equals("y") || respuesta.equals("yes") || respuesta.isEmpty()) {
                    complete = true;
                }
            }
        }
        serverSocket.close();
        this.clientes.sort(Comparator.comparing(Nodo::getId));
        System.out.println("Procesando nodos...");
    }

    public Nodo findNodeById(int id) {
        return clientes.stream().filter(n -> n.getId() == id).findFirst().orElse(null);
    }

    // --- Algoritmo de Berkeley --
    public void berkeley() {
        System.out.println("\n--- INICIANDO ALGORITMO DE BERKELEY ---");
        Nodo master = server(); // Usar el método server() que ya tienes

        if (master == null) {
            System.out.println("Error: No se encontró un nodo maestro (tipo='server').");
            return;
        }

        // a. El demonio del tiempo pide la hora
        System.out.println("a. El demonio del tiempo (P" + master.getId() + ") pide la hora a las otras máquinas.");
        Integer T0 = master.getTime();
        System.out.println("\nEl servidor difunde su reloj en T0 = " + T0 + ". Los mensajes tardan 5 unidades de tiempo en llegar.");

        for (Nodo cliente : clientes) {
            // El tiempo de comunicación solo afecta a los esclavos que reciben el mensaje
            if (cliente.getId().equals(master.getId())) continue;
            cliente.comunication_time();
        }
        System.out.println("Estado de los relojes cuando los mensajes llegan:");
        clientes.forEach(System.out::println);

        // b. Las máquinas responden
        System.out.println("\nb. Cada cliente calcula la diferencia Di con T0 y la notifica al servidor.");
        // CAMBIO CLAVE: Usar un Map para mantener la relación ID -> Diferencia
        Map<Integer, Integer> diferencias = new HashMap<>();
        for (Nodo cliente : clientes) {
            if (cliente.getId().equals(master.getId())) continue;
            int diferencia = cliente.getTime() - T0;
            diferencias.put(cliente.getId(), diferencia);
            System.out.println("D" + cliente.getId() + " = " + cliente.getTime() + " - " + T0 + " = " + diferencia);
        }

        // c. Se suma el tiempo de comunicación de vuelta al servidor
        System.out.println("\nSe suma el tiempo de comunicación para las respuestas que llegan al servidor.");
        master.comunication_time(); // El tiempo del master avanza mientras espera respuestas

        // d. El servidor lee las diferencias y las ajusta por el RTT (Round Trip Time)
        Integer TLi = master.getTime();
        System.out.println("\nd. El servidor en TLi=" + TLi + " ajusta las diferencias por el tiempo de viaje (RTT/2).");

        Map<Integer, Integer> diferenciasAjustadas = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : diferencias.entrySet()) {
            Integer clienteId = entry.getKey();
            Integer diferenciaOriginal = entry.getValue();
            // Di' = Di - (TLi - T0) / 2
            int diferenciaAjustada = diferenciaOriginal - ((TLi - T0) / 2);
            diferenciasAjustadas.put(clienteId, diferenciaAjustada);
            System.out.println("D'" + clienteId + " = " + diferenciaOriginal + " - (" + TLi + " - " + T0 + " ) / 2 = " + diferenciaAjustada);
        }
        // La diferencia ajustada del master consigo mismo es 0
        diferenciasAjustadas.put(master.getId(), 0);

        // e. Se calcula la diferencia promedio
        System.out.println("\ne. Calculo de diferencia promedio (incluyendo al servidor).");
        double promedio = diferenciasAjustadas.values().stream().mapToInt(v -> v).average().orElse(0.0);
        System.out.println("Diferencia promedio = " + promedio);

        // f. y g. El servidor calcula los ajustes finales y los clientes actualizan su reloj
        System.out.println("\nf/g. El servidor calcula los ajustes y los nodos actualizan sus relojes.");
        for (Nodo cliente : clientes) {
            int ajuste = (int) Math.round(promedio - diferenciasAjustadas.get(cliente.getId()));
            System.out.println("  Ajuste para P" + cliente.getId() + ": " + ajuste + " unidades.");
            cliente.setTime(cliente.getTime() + ajuste);
        }

        System.out.println("\nEstado final sincronizado de los relojes:");
        clientes.forEach(System.out::println);
        System.out.println("--- FIN DE BERKELEY ---");
    }

    // --- Algoritmo de Ricart-Agrawala (Simulación Estática) ---
    public void agrawala(int requesterId) {
        System.out.println("\n--- INICIANDO ALGORITMO DE RICART-AGRAWALA ---");
        Nodo requester = findNodeById(requesterId);
        if (requester == null) {
            System.out.println("Error: Nodo solicitante con ID " + requesterId + " no encontrado.");
            return;
        }

        System.out.println("P" + requesterId + " quiere entrar a la Sección Crítica (SC).");
        requester.setEstado("W"); // W de WANTED
        int mtlRequester = requester.getTime();
        System.out.println("Marca de Tiempo Lógico (MTL) de P" + requesterId + ": " + mtlRequester);

        int oksRecibidos = 0;
        System.out.println("\nP" + requesterId + " envía un mensaje REQUEST a todos los demás nodos.");

        for (Nodo receptor : clientes) {
            if (receptor.getId().equals(requesterId)) continue;
            System.out.print(" -> P" + receptor.getId() + " recibe el REQUEST. ");

            String estadoReceptor = receptor.getEstado();
            switch (estadoReceptor) {
                case "R": // Released
                    System.out.println("Su estado es 'RELEASED', envía OK.");
                    oksRecibidos++;
                    break;
                case "H": // Held
                    System.out.println("Su estado es 'HELD', no responde y pone a P" + requesterId + " en su cola.");
                    receptor.agregarAListaDeEspera(requesterId);
                    break;
                case "W": // Wanted
                    System.out.print("Su estado es 'WANTED'. Comparando prioridades... ");
                    int mtlReceptor = receptor.getTime();
                    if (mtlRequester < mtlReceptor) {
                        System.out.println("MTL del solicitante ("+mtlRequester+") es menor. P" + receptor.getId() + " envía OK.");
                        oksRecibidos++;
                    } else if (mtlRequester > mtlReceptor) {
                        System.out.println("MTL del solicitante ("+mtlRequester+") es mayor. P" + receptor.getId() + " no responde y lo encola.");
                        receptor.agregarAListaDeEspera(requesterId);
                    } else { // MTL son iguales, desempatar por ID
                        if (requesterId < receptor.getId()) {
                            System.out.println("MTL iguales, ID del solicitante ("+requesterId+") es menor. P" + receptor.getId() + " envía OK.");
                            oksRecibidos++;
                        } else {
                            System.out.println("MTL iguales, ID del solicitante ("+requesterId+") es mayor. P" + receptor.getId() + " no responde y lo encola.");
                            receptor.agregarAListaDeEspera(requesterId);
                        }
                    }
                    break;
            }
        }

        // Comprobar si puede entrar a la SC
        if (oksRecibidos == clientes.size() - 1) {
            System.out.println("\nP" + requesterId + " ha recibido todos los OKs. Entrando a la Sección Crítica...");
            requester.setEstado("H");
            // Simular trabajo en la SC
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            System.out.println("P" + requesterId + " ha salido de la Sección Crítica.");
            requester.setEstado("R");

            // Enviar OK a los procesos en su cola de espera
            if (!requester.getListaDeEspera().isEmpty()) {
                System.out.println("P" + requesterId + " ahora envía OK a los nodos en su cola de espera: " + requester.getListaDeEspera());
                // En una simulación real, aquí se procesarían las colas de los otros nodos.
                requester.limpiarListaDeEspera();
            }
        } else {
            System.out.println("\nP" + requesterId + " no ha recibido todos los OKs. Debe esperar.");
        }
        System.out.println("--- FIN DE AGRAWALA ---");
    }

    // --- Algoritmo Grandulon ---
    public Nodo server() {
        for (Nodo s : this.clientes) {
            if (s.getTipo().equals("server")) {
                return s;
            }
        }
        return null;
    }

    public Boolean hayNodoFallido() {
        Nodo server = server();
        if (server == null) return true; // Si no hay servidor, se considera como que ha fallado.
        return server.getEstado().equals("F");
    }

    public void grandulon() {
        if (!hayNodoFallido()) {
            System.out.println("El nodo servidor no ha fallado, ¿quiere hacer que falle y que un nodo aleatorio lo identifique? (Y/n)");
            Scanner scanner = new Scanner(System.in);
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (respuesta.equals("y") || respuesta.equals("yes") || respuesta.isEmpty()) {
                System.out.println("El nodo servidor ha fallado.");
                Nodo server = server();
                if (server != null) {
                    server.setEstado("F");
                }
            } else {
                System.out.println("El algoritmo Grandulón no se ejecutará sin un fallo del servidor.");
                return;
            }
        }

        // Se escoge un nodo aleatorio (que no sea el fallido) para que inicie la elección.
        Random random = new Random();
        Nodo initiatorNode = null;
        while (initiatorNode == null || initiatorNode.getEstado().equals("F")) {
            int index = random.nextInt(this.clientes.size());
            initiatorNode = this.clientes.get(index);
        }

        System.out.println("----------------------------------------------------");
        System.out.println("Nodo " + initiatorNode.getId() + " ha detectado el fallo del servidor e inicia la elección.");
        System.out.println("----------------------------------------------------");

        startElection(initiatorNode);
    }

    private void startElection(Nodo initiatorNode) {
        System.out.println("Nodo " + initiatorNode.getId() + " inicia una elección.");

        boolean higherNodeResponded = false;

        // 1. P (initiatorNode) envía un mensaje ELECCION a los procesos con un número mayor.
        for (Nodo potentialResponder : this.clientes) {
            if (potentialResponder.getId() > initiatorNode.getId()) {
                System.out.println(" -> Nodo " + initiatorNode.getId() + " envía mensaje 'ELECCION' a Nodo " + potentialResponder.getId());

                // Simulamos la respuesta. Si el nodo no está fallido, responde "OK".
                if (!potentialResponder.getEstado().equals("F")) {
                    System.out.println(" <- Nodo " + potentialResponder.getId() + " responde 'OK' a Nodo " + initiatorNode.getId());
                    higherNodeResponded = true;
                    // 3. Si un proceso con número mayor responde, toma el control. El trabajo de P termina.
                    // El nodo que respondió ahora debe iniciar su propia elección.
                    startElection(potentialResponder);
                    return; // El iniciador actual detiene su proceso de elección.
                } else {
                    System.out.println(" <- Nodo " + potentialResponder.getId() + " no responde (ha fallado).");
                }
            }
        }

        // 2. Si nadie responde, P (initiatorNode) gana la elección y se convierte en el coordinador.
        if (!higherNodeResponded) {
            System.out.println("\n****************************************************");
            System.out.println("Ningún nodo con mayor ID ha respondido.");
            System.out.println("Nodo " + initiatorNode.getId() + " GANA la elección y es el nuevo coordinador.");
            System.out.println("****************************************************");

            // Actualizamos el estado del sistema.
            // Democionamos al antiguo servidor si existía.
            Nodo oldServer = server();
            if(oldServer != null) {
                oldServer.setTipo("client");
            }

            // Promocionamos al nuevo coordinador.
            initiatorNode.setTipo("server");
            initiatorNode.setEstado("R"); // H de HELD

            // El nuevo coordinador envía un mensaje de COORDINADOR a todos los demás nodos.
            System.out.println("\nNodo " + initiatorNode.getId() + " (nuevo coordinador) envía mensaje 'COORDINADOR' a todos los nodos.");
            for (Nodo node : this.clientes) {
                if (node.getId() != initiatorNode.getId() && !node.getEstado().equals("F")) {
                    System.out.println(" -> Mensaje 'COORDINADOR' enviado a Nodo " + node.getId());
                }
            }
        }
    }

    public static void main(String[] args) {
        Main servidor = new Main();
        try {
            servidor.inicio();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Server error: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nIngresa un comando:");
            System.out.println("  berkeley");
            System.out.println("  grandulon");
            System.out.println("  agrawala <ID_solicitante>");
            System.out.println("  exit");
            System.out.print("Comando: ");
            String[] comandoParts = scanner.nextLine().trim().toLowerCase().split(" ");
            String comando = comandoParts[0];

            switch (comando) {
                case "berkeley":
                    servidor.berkeley();
                    break;
                case "grandulon":
                    servidor.grandulon();
                    break;
                case "agrawala":
                    if (comandoParts.length > 1) {
                        try {
                            int requesterId = Integer.parseInt(comandoParts[1]);
                            servidor.agrawala(requesterId);
                        } catch (NumberFormatException e) {
                            System.out.println("Por favor, proporciona un ID numérico válido para el solicitante.");
                        }
                    } else {
                        System.out.println("Uso: agrawala <ID_solicitante>");
                    }
                    break;
                case "exit":
                    System.out.println("Saliendo del programa...");
                    return;
                default:
                    System.out.println("Comando no reconocido. Inténtalo de nuevo.");
            }
        }
    }
}