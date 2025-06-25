// Nodo.java
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class Nodo implements Serializable {
    private Integer id;
    private Integer time;
    private STATES state;
    private TIPO tipo;
    private final Integer comunication_time = 5;

    // CAMBIO: Añadida cola de espera para Agrawala
    private Queue<Integer> listaDeEspera = new LinkedList<>();

    //constructor
    public Nodo(Integer id, Integer time, String state, String tipo) {
        this.id = id;
        this.time = time;
        this.state = STATES.fromString(state);
        this.tipo = TIPO.fromString(tipo);
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public Integer getTime() { return time; }
    public String getTipo() { return tipo.getTipo(); }
    public String getEstado() { return state.getState(); }
    public void comunication_time() { this.time = this.time + this.comunication_time; } // Getter para el tiempo de comunicación
    public Queue<Integer> getListaDeEspera() { return listaDeEspera; } // Getter para la cola

    public void setId(Integer id) { this.id = id; }
    public void setTime(Integer time) { this.time = time; }
    public void setEstado(String estado) { this.state = STATES.fromString(estado); }
    public void setTipo(String tipo) { this.tipo = TIPO.fromString(tipo); }
    public void agregarAListaDeEspera(Integer id) { this.listaDeEspera.add(id); }
    public void limpiarListaDeEspera() { this.listaDeEspera.clear(); }

    @Override
    public String toString(){
        return "P" + id + " -- Tiempo: " + time + " -- Estado: " + state + " -- Tipo: " + tipo;
    }
}