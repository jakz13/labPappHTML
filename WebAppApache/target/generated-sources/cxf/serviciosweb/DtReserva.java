
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para dtReserva complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="dtReserva"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="asientosAsignados" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="cantidadPasajes" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="costo" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="estado" type="{http://ServiciosWeb/}estadoReserva" minOccurs="0"/&gt;
 *         &lt;element name="fecha" type="{http://ServiciosWeb/}localDate" minOccurs="0"/&gt;
 *         &lt;element name="fechaCheckin" type="{http://ServiciosWeb/}localDate" minOccurs="0"/&gt;
 *         &lt;element name="horaInicioEmbarque" type="{http://ServiciosWeb/}localTime" minOccurs="0"/&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="pasajeros" type="{http://ServiciosWeb/}dtPasajero" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="tipoAsiento" type="{http://ServiciosWeb/}tipoAsiento" minOccurs="0"/&gt;
 *         &lt;element name="unidadesEquipajeExtra" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="vuelo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dtReserva", propOrder = {
    "asientosAsignados",
    "cantidadPasajes",
    "costo",
    "estado",
    "fecha",
    "fechaCheckin",
    "horaInicioEmbarque",
    "id",
    "pasajeros",
    "tipoAsiento",
    "unidadesEquipajeExtra",
    "vuelo"
})
public class DtReserva {

    @XmlElement(nillable = true)
    protected List<String> asientosAsignados;
    protected int cantidadPasajes;
    protected double costo;
    @XmlSchemaType(name = "string")
    protected EstadoReserva estado;
    protected LocalDate fecha;
    protected LocalDate fechaCheckin;
    protected LocalTime horaInicioEmbarque;
    protected Long id;
    @XmlElement(nillable = true)
    protected List<DtPasajero> pasajeros;
    @XmlSchemaType(name = "string")
    protected TipoAsiento tipoAsiento;
    protected int unidadesEquipajeExtra;
    protected String vuelo;

    /**
     * Gets the value of the asientosAsignados property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the asientosAsignados property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAsientosAsignados().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAsientosAsignados() {
        if (asientosAsignados == null) {
            asientosAsignados = new ArrayList<String>();
        }
        return this.asientosAsignados;
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
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link EstadoReserva }
     *     
     */
    public EstadoReserva getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoReserva }
     *     
     */
    public void setEstado(EstadoReserva value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad fecha.
     * 
     * @return
     *     possible object is
     *     {@link LocalDate }
     *     
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Define el valor de la propiedad fecha.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalDate }
     *     
     */
    public void setFecha(LocalDate value) {
        this.fecha = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaCheckin.
     * 
     * @return
     *     possible object is
     *     {@link LocalDate }
     *     
     */
    public LocalDate getFechaCheckin() {
        return fechaCheckin;
    }

    /**
     * Define el valor de la propiedad fechaCheckin.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalDate }
     *     
     */
    public void setFechaCheckin(LocalDate value) {
        this.fechaCheckin = value;
    }

    /**
     * Obtiene el valor de la propiedad horaInicioEmbarque.
     * 
     * @return
     *     possible object is
     *     {@link LocalTime }
     *     
     */
    public LocalTime getHoraInicioEmbarque() {
        return horaInicioEmbarque;
    }

    /**
     * Define el valor de la propiedad horaInicioEmbarque.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalTime }
     *     
     */
    public void setHoraInicioEmbarque(LocalTime value) {
        this.horaInicioEmbarque = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the pasajeros property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the pasajeros property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPasajeros().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtPasajero }
     * 
     * 
     */
    public List<DtPasajero> getPasajeros() {
        if (pasajeros == null) {
            pasajeros = new ArrayList<DtPasajero>();
        }
        return this.pasajeros;
    }

    /**
     * Obtiene el valor de la propiedad tipoAsiento.
     * 
     * @return
     *     possible object is
     *     {@link TipoAsiento }
     *     
     */
    public TipoAsiento getTipoAsiento() {
        return tipoAsiento;
    }

    /**
     * Define el valor de la propiedad tipoAsiento.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoAsiento }
     *     
     */
    public void setTipoAsiento(TipoAsiento value) {
        this.tipoAsiento = value;
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
     * Obtiene el valor de la propiedad vuelo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVuelo() {
        return vuelo;
    }

    /**
     * Define el valor de la propiedad vuelo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVuelo(String value) {
        this.vuelo = value;
    }

}
