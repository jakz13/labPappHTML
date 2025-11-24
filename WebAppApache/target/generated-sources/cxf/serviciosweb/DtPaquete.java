
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para dtPaquete complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="dtPaquete"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="costo" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descuentoPorc" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fechaAlta" type="{http://ServiciosWeb/}localDate" minOccurs="0"/&gt;
 *         &lt;element name="items" type="{http://ServiciosWeb/}dtItemPaquete" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="periodoValidezDias" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dtPaquete", propOrder = {
    "costo",
    "descripcion",
    "descuentoPorc",
    "fechaAlta",
    "items",
    "nombre",
    "periodoValidezDias"
})
public class DtPaquete {

    protected double costo;
    protected String descripcion;
    protected int descuentoPorc;
    protected LocalDate fechaAlta;
    @XmlElement(nillable = true)
    protected List<DtItemPaquete> items;
    protected String nombre;
    protected int periodoValidezDias;

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
     * Obtiene el valor de la propiedad descuentoPorc.
     * 
     */
    public int getDescuentoPorc() {
        return descuentoPorc;
    }

    /**
     * Define el valor de la propiedad descuentoPorc.
     * 
     */
    public void setDescuentoPorc(int value) {
        this.descuentoPorc = value;
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
     * Gets the value of the items property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the items property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtItemPaquete }
     * 
     * 
     */
    public List<DtItemPaquete> getItems() {
        if (items == null) {
            items = new ArrayList<DtItemPaquete>();
        }
        return this.items;
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
     * Obtiene el valor de la propiedad periodoValidezDias.
     * 
     */
    public int getPeriodoValidezDias() {
        return periodoValidezDias;
    }

    /**
     * Define el valor de la propiedad periodoValidezDias.
     * 
     */
    public void setPeriodoValidezDias(int value) {
        this.periodoValidezDias = value;
    }

}
