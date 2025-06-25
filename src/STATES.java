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

    public static STATES fromString(String text) {
        for (STATES s : STATES.values()) {
            if (s.state.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant with state: " + text);
    }
}
