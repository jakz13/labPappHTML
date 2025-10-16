package Logica;

public enum TipoDoc {
        CI,
        PASAPORTE;

    public boolean isEmpty() {
        if (this == null) {;
            return true;
        }
        else {
            return false;
        }
    }
}
