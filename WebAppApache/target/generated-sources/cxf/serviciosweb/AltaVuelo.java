
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para altaVuelo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="altaVuelo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nombreVuelo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nombreAerolinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nombreRuta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="fecha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="duracion" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="asientosTurista" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="asientosEjecutivo" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fechaAlta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="imagenUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "altaVuelo", propOrder = {
    "nombreVuelo",
    "nombreAerolinea",
    "nombreRuta",
    "fecha",
    "duracion",
    "asientosTurista",
    "asientosEjecutivo",
    "fechaAlta",
    "imagenUrl"
})
public class AltaVuelo {

    protected String nombreVuelo;
    protected String nombreAerolinea;
    protected String nombreRuta;
    protected String fecha;
    protected int duracion;
    protected int asientosTurista;
    protected int asientosEjecutivo;
    protected String fechaAlta;
    protected String imagenUrl;

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
     * Obtiene el valor de la propiedad nombreRuta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreRuta() {
        return nombreRuta;
    }

    /**
     * Define el valor de la propiedad nombreRuta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreRuta(String value) {
        this.nombreRuta = value;
    }

    /**
     * Obtiene el valor de la propiedad fecha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Define el valor de la propiedad fecha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFecha(String value) {
        this.fecha = value;
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
     * Obtiene el valor de la propiedad fechaAlta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaAlta() {
        return fechaAlta;
    }

    /**
     * Define el valor de la propiedad fechaAlta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaAlta(String value) {
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

}
