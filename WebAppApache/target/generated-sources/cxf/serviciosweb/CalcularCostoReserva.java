
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para calcularCostoReserva complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="calcularCostoReserva"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nombreVuelo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipoAsiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cantidadPasajes" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="unidadesEquipajeExtra" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calcularCostoReserva", propOrder = {
    "nombreVuelo",
    "tipoAsiento",
    "cantidadPasajes",
    "unidadesEquipajeExtra"
})
public class CalcularCostoReserva {

    protected String nombreVuelo;
    protected String tipoAsiento;
    protected int cantidadPasajes;
    protected int unidadesEquipajeExtra;

    /**
     * Obtiene el valor de la propiedad nombreVuelo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreVuelo() {
        return nombreVuelo;
    }

    /**
     * Define el valor de la propiedad nombreVuelo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreVuelo(String value) {
        this.nombreVuelo = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoAsiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoAsiento() {
        return tipoAsiento;
    }

    /**
     * Define el valor de la propiedad tipoAsiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoAsiento(String value) {
        this.tipoAsiento = value;
    }

    /**
     * Obtiene el valor de la propiedad cantidadPasajes.
     * 
     */
    public int getCantidadPasajes() {
        return cantidadPasajes;
    }

    /**
     * Define el valor de la propiedad cantidadPasajes.
     * 
     */
    public void setCantidadPasajes(int value) {
        this.cantidadPasajes = value;
    }

    /**
     * Obtiene el valor de la propiedad unidadesEquipajeExtra.
     * 
     */
    public int getUnidadesEquipajeExtra() {
        return unidadesEquipajeExtra;
    }

    /**
     * Define el valor de la propiedad unidadesEquipajeExtra.
     * 
     */
    public void setUnidadesEquipajeExtra(int value) {
        this.unidadesEquipajeExtra = value;
    }

}
