
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para dtVuelo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="dtVuelo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="asientosEjecutivo" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="asientosTurista" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="duracion" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fecha" type="{http://ServiciosWeb/}localDate" minOccurs="0"/&gt;
 *         &lt;element name="fechaAlta" type="{http://ServiciosWeb/}localDate" minOccurs="0"/&gt;
 *         &lt;element name="imagenUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nombreAerolinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="reservas" type="{http://ServiciosWeb/}dtReserva" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="rutaVuelo" type="{http://ServiciosWeb/}dtRutaVuelo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dtVuelo", propOrder = {
    "asientosEjecutivo",
    "asientosTurista",
    "duracion",
    "fecha",
    "fechaAlta",
    "imagenUrl",
    "nombre",
    "nombreAerolinea",
    "reservas",
    "rutaVuelo"
})
public class DtVuelo {

    protected int asientosEjecutivo;
    protected int asientosTurista;
    protected int duracion;
    protected LocalDate fecha;
    protected LocalDate fechaAlta;
    protected String imagenUrl;
    protected String nombre;
    protected String nombreAerolinea;
    @XmlElement(nillable = true)
    protected List<DtReserva> reservas;
    protected DtRutaVuelo rutaVuelo;

    /**
     * Obtiene el valor de la propiedad asientosEjecutivo.
     * 
     */
    public int getAsientosEjecutivo() {
        return asientosEjecutivo;
    }

    /**
     * Define el valor de la propiedad asientosEjecutivo.
     * 
     */
    public void setAsientosEjecutivo(int value) {
        this.asientosEjecutivo = value;
    }

    /**
     * Obtiene el valor de la propiedad asientosTurista.
     * 
     */
    public int getAsientosTurista() {
        return asientosTurista;
    }

    /**
     * Define el valor de la propiedad asientosTurista.
     * 
     */
    public void setAsientosTurista(int value) {
        this.asientosTurista = value;
    }

    /**
     * Obtiene el valor de la propiedad duracion.
     * 
     */
    public int getDuracion() {
        return duracion;
    }

    /**
     * Define el valor de la propiedad duracion.
     * 
     */
    public void setDuracion(int value) {
        this.duracion = value;
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
     * Obtiene el valor de la propiedad fechaAlta.
     * 
     * @return
     *     possible object is
     *     {@link LocalDate }
     *     
     */
    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    /**
     * Define el valor de la propiedad fechaAlta.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalDate }
     *     
     */
    public void setFechaAlta(LocalDate value) {
        this.fechaAlta = value;
    }

    /**
     * Obtiene el valor de la propiedad imagenUrl.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImagenUrl() {
        return imagenUrl;
    }

    /**
     * Define el valor de la propiedad imagenUrl.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImagenUrl(String value) {
        this.imagenUrl = value;
    }

    /**
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el valor de la propiedad nombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Obtiene el valor de la propiedad nombreAerolinea.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreAerolinea() {
        return nombreAerolinea;
    }

    /**
     * Define el valor de la propiedad nombreAerolinea.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreAerolinea(String value) {
        this.nombreAerolinea = value;
    }

    /**
     * Gets the value of the reservas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the reservas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReservas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtReserva }
     * 
     * 
     */
    public List<DtReserva> getReservas() {
        if (reservas == null) {
            reservas = new ArrayList<DtReserva>();
        }
        return this.reservas;
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

}
