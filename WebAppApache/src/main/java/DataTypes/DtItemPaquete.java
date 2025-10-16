package DataTypes;

public class DtItemPaquete {
    private DtRutaVuelo rutaVuelo; // referencia a la ruta de vuelo
    private int cantAsientos;
    private String tipoAsiento; // en texto para facilitar transporte

    public DtItemPaquete() {
    }

    public DtItemPaquete(DtRutaVuelo rutaVuelo, int cantAsientos, String tipoAsiento) {
        this.rutaVuelo = rutaVuelo;
        this.cantAsientos = cantAsientos;
        this.tipoAsiento = tipoAsiento;
    }

    // --- Getters y Setters ---


    public DtRutaVuelo getRutaVuelo() {
        return rutaVuelo;
    }

    public void setRutaVuelo(DtRutaVuelo rutaVuelo) {
        this.rutaVuelo = rutaVuelo;
    }


    public int getCantAsientos() {
        return cantAsientos;
    }

    public void setCantAsientos(int cantAsientos) {
        this.cantAsientos = cantAsientos;
    }

    public String getTipoAsiento() {
        return tipoAsiento;
    }

    public void setTipoAsiento(String tipoAsiento) {
        this.tipoAsiento = tipoAsiento;
    }

    @Override
    public String toString() {
        return "DtItemPaquete{" +
                ", cantAsientos=" + cantAsientos +
                ", tipoAsiento='" + tipoAsiento + '\'' +
                '}';
    }
}
