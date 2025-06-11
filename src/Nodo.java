public class Nodo {
    private Integer id;
    private Integer time;
    //constructor
    public Nodo(Integer id, Integer time) {
        this.id = id;
        this.time = time;
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

    public String to_String(){
        return id + " " + time + " -- ";
    }
}

