public enum STATES {
    RELEASED("R"), // No requiere del recurso compartido
    WANTED("W"), // Requiriendo el recurso compartido
    HELD("H"), //haciendo uso del recurso compartido
    FAILED("F"); // Nodo fallido

    private final String state;

    STATES(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
