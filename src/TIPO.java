public enum TIPO {
SERVER ("server"), CLIENT("cliente");

    private final String tipo;

    TIPO(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
