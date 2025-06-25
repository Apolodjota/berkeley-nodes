import java.io.Serializable;

public class Nodo implements Serializable {
    private Integer id;
    private Integer time;
    private STATES state;
    private TIPO tipo;
    private final Integer comunication_time = 5;

    //constructor
    public Nodo(Integer id, Integer time, String state, String tipo) {
        this.id = id;
        this.time = time;
        this.state = STATES.fromString(state);
        this.tipo = TIPO.fromString(tipo);


    }
    //getters
    public Integer getId() {
        return id;
    }
    public Integer getTime() {
        return time;
    }

    //setters
    public void setId(Integer id) {
        this.id = id;
    }
    public void setTime(Integer time) {
        this.time = time;
    }


    public String toString(){
        return "P"+id + " -- Tiempo: " + time + " -- " + state + " -- " + tipo;
    }

    public void comunication_time() {
        this.time += comunication_time;
    }

}

