
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para altaRutaVuelo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="altaRutaVuelo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descripcionCorta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="aerolineaNickname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ciudadOrigen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ciudadDestino" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="fechaAlta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="costoTurista" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="costoEjecutivo" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="costoEquipajeExtra" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="categorias" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="imagenUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="videoUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "altaRutaVuelo", propOrder = {
    "nombre",
    "descripcion",
    "descripcionCorta",
    "aerolineaNickname",
    "ciudadOrigen",
    "ciudadDestino",
    "hora",
    "fechaAlta",
    "costoTurista",
    "costoEjecutivo",
    "costoEquipajeExtra",
    "categorias",
    "imagenUrl",
    "videoUrl"
})
public class AltaRutaVuelo {

    protected String nombre;
    protected String descripcion;
    protected String descripcionCorta;
    protected String aerolineaNickname;
    protected String ciudadOrigen;
    protected String ciudadDestino;
    protected String hora;
    protected String fechaAlta;
    protected double costoTurista;
    protected double costoEjecutivo;
    protected double costoEquipajeExtra;
    protected List<String> categorias;
    protected String imagenUrl;
    protected String videoUrl;

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
     * Obtiene el valor de la propiedad descripcion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Define el valor de la propiedad descripcion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcion(String value) {
        this.descripcion = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcionCorta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    /**
     * Define el valor de la propiedad descripcionCorta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionCorta(String value) {
        this.descripcionCorta = value;
    }

    /**
     * Obtiene el valor de la propiedad aerolineaNickname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAerolineaNickname() {
        return aerolineaNickname;
    }

    /**
     * Define el valor de la propiedad aerolineaNickname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAerolineaNickname(String value) {
        this.aerolineaNickname = value;
    }

    /**
     * Obtiene el valor de la propiedad ciudadOrigen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiudadOrigen() {
        return ciudadOrigen;
    }

    /**
     * Define el valor de la propiedad ciudadOrigen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiudadOrigen(String value) {
        this.ciudadOrigen = value;
    }

    /**
     * Obtiene el valor de la propiedad ciudadDestino.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiudadDestino() {
        return ciudadDestino;
    }

    /**
     * Define el valor de la propiedad ciudadDestino.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiudadDestino(String value) {
        this.ciudadDestino = value;
    }

    /**
     * Obtiene el valor de la propiedad hora.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHora() {
        return hora;
    }

    /**
     * Define el valor de la propiedad hora.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHora(String value) {
        this.hora = value;
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
     * Obtiene el valor de la propiedad costoTurista.
     * 
     */
    public double getCostoTurista() {
        return costoTurista;
    }

    /**
     * Define el valor de la propiedad costoTurista.
     * 
     */
    public void setCostoTurista(double value) {
        this.costoTurista = value;
    }

    /**
     * Obtiene el valor de la propiedad costoEjecutivo.
     * 
     */
    public double getCostoEjecutivo() {
        return costoEjecutivo;
    }

    /**
     * Define el valor de la propiedad costoEjecutivo.
     * 
     */
    public void setCostoEjecutivo(double value) {
        this.costoEjecutivo = value;
    }

    /**
     * Obtiene el valor de la propiedad costoEquipajeExtra.
     * 
     */
    public double getCostoEquipajeExtra() {
        return costoEquipajeExtra;
    }

    /**
     * Define el valor de la propiedad costoEquipajeExtra.
     * 
     */
    public void setCostoEquipajeExtra(double value) {
        this.costoEquipajeExtra = value;
    }

    /**
     * Gets the value of the categorias property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the categorias property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategorias().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCategorias() {
        if (categorias == null) {
            categorias = new ArrayList<String>();
        }
        return this.categorias;
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
     * Obtiene el valor de la propiedad videoUrl.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * Define el valor de la propiedad videoUrl.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVideoUrl(String value) {
        this.videoUrl = value;
    }

}
