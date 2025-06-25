import java.io.Serializable;

public class Nodo implements Serializable {
    private Integer id;
    private Integer time;
    private Integer time_mark;
    private STATES state;
    private TIPO tipo;
    private final Integer comunication_time = 5;

    //constructor
    public Nodo(Integer id, Integer time, Integer time_mark, String state, String tipo) {
        this.id = id;
        this.time = time;
        this.time_mark = time_mark;
        this.state = STATES.valueOf(state);
        this.tipo = TIPO.valueOf(tipo);


    }
    //getters
    public Integer getId() {
        return id;
    }
    public Integer getTime() {
        return time;
    }
    public Integer getTime_mark() {
        return time_mark;
    }

    //setters
    public void setId(Integer id) {
        this.id = id;
    }
    public void setTime(Integer time) {
        this.time = time;
    }

    public void setTime_mark(Integer time_mark) {
        this.time_mark = time_mark;
    }

    public String to_String(){
        return id + " " + time + " -- " + state + " -- " + tipo;
    }

    public void comunication_time() {
        this.time += comunication_time;
    }

}

