
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para dtItemPaquete complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="dtItemPaquete"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cantAsientos" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="rutaVuelo" type="{http://ServiciosWeb/}dtRutaVuelo" minOccurs="0"/&gt;
 *         &lt;element name="tipoAsiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dtItemPaquete", propOrder = {
    "cantAsientos",
    "rutaVuelo",
    "tipoAsiento"
})
public class DtItemPaquete {

    protected int cantAsientos;
    protected DtRutaVuelo rutaVuelo;
    protected String tipoAsiento;

    /**
     * Obtiene el valor de la propiedad cantAsientos.
     * 
     */
    public int getCantAsientos() {
        return cantAsientos;
    }

    /**
     * Define el valor de la propiedad cantAsientos.
     * 
     */
    public void setCantAsientos(int value) {
        this.cantAsientos = value;
    }

    /**
     * Obtiene el valor de la propiedad rutaVuelo.
     * 
     * @return
     *     possible object is
     *     {@link DtRutaVuelo }
     *     
     */
    public DtRutaVuelo getRutaVuelo() {
        return rutaVuelo;
    }

    /**
     * Define el valor de la propiedad rutaVuelo.
     * 
     * @param value
     *     allowed object is
     *     {@link DtRutaVuelo }
     *     
     */
    public void setRutaVuelo(DtRutaVuelo value) {
        this.rutaVuelo = value;
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

}
