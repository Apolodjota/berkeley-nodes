public class Main {
    public static void main(String[] args) {
        Servidor servidor = new Servidor(1, 10000);

        // Crear instancias de los clientes
        Cliente cliente1 = new Cliente(2, 9998);
        Cliente cliente2 = new Cliente(3, 10001);
        Cliente cliente3 = new Cliente(4, 10005);

        // Iniciar el servidor en un hilo separado
        Thread hiloServidor = new Thread(() -> servidor.iniciar());
        hiloServidor.start();

        // Esperar un poco para asegurarse de que el servidor está escuchando
        try {
            Thread.sleep(1000); // 1 segundo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Iniciar los clientes en sus propios hilos
        Thread t1 = new Thread(() -> cliente1.sincronizar());
        Thread t2 = new Thread(() -> cliente2.sincronizar());
        Thread t3 = new Thread(() -> cliente3.sincronizar());

        t1.start();
        t2.start();
        t3.start();

        // Esperar a que todos terminen
        try {
            t1.join();
            t2.join();
            t3.join();
            hiloServidor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mostrar resultados finales
        System.out.println("\n--- Relojes sincronizados ---");
        System.out.println("Servidor: " + servidor.getTime());
        System.out.println("Cliente 1: " + cliente1.getTime());
        System.out.println("Cliente 2: " + cliente2.getTime());
        System.out.println("Cliente 3: " + cliente3.getTime());
    }
}
