
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para crearYRegistrarReserva complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="crearYRegistrarReserva"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nicknameCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nombreVuelo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="fechaReserva" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="costo" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="tipoAsiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cantidadPasajes" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="unidadesEquipajeExtra" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="pasajerosNombres" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="pasajerosApellidos" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "crearYRegistrarReserva", propOrder = {
    "nicknameCliente",
    "nombreVuelo",
    "fechaReserva",
    "costo",
    "tipoAsiento",
    "cantidadPasajes",
    "unidadesEquipajeExtra",
    "pasajerosNombres",
    "pasajerosApellidos"
})
public class CrearYRegistrarReserva {

    protected String nicknameCliente;
    protected String nombreVuelo;
    protected String fechaReserva;
    protected double costo;
    protected String tipoAsiento;
    protected int cantidadPasajes;
    protected int unidadesEquipajeExtra;
    protected List<String> pasajerosNombres;
    protected List<String> pasajerosApellidos;

    /**
     * Obtiene el valor de la propiedad nicknameCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNicknameCliente() {
        return nicknameCliente;
    }

    /**
     * Define el valor de la propiedad nicknameCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNicknameCliente(String value) {
        this.nicknameCliente = value;
    }

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
     * Obtiene el valor de la propiedad fechaReserva.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaReserva() {
        return fechaReserva;
    }

    /**
     * Define el valor de la propiedad fechaReserva.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaReserva(String value) {
        this.fechaReserva = value;
    }

    /**
     * Obtiene el valor de la propiedad costo.
     * 
     */
    public double getCosto() {
        return costo;
    }

    /**
     * Define el valor de la propiedad costo.
     * 
     */
    public void setCosto(double value) {
        this.costo = value;
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

    /**
     * Gets the value of the pasajerosNombres property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the pasajerosNombres property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPasajerosNombres().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPasajerosNombres() {
        if (pasajerosNombres == null) {
            pasajerosNombres = new ArrayList<String>();
        }
        return this.pasajerosNombres;
    }

    /**
     * Gets the value of the pasajerosApellidos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the pasajerosApellidos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPasajerosApellidos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPasajerosApellidos() {
        if (pasajerosApellidos == null) {
            pasajerosApellidos = new ArrayList<String>();
        }
        return this.pasajerosApellidos;
    }

}
