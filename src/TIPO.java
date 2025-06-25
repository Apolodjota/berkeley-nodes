public enum TIPO {
SERVER ("server"),
CLIENT("client");

    private final String tipo;

    TIPO(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
    public static TIPO fromString(String text) { // Changed return type to TIPO
        for (TIPO t : TIPO.values()) { // Iterating over TIPO values
            if (t.tipo.equalsIgnoreCase(text)) {
                return t;
            }
        }
        // It's good practice to make the error message more specific to the enum
        throw new IllegalArgumentException("No enum constant with tipo: " + text);
    }
}
