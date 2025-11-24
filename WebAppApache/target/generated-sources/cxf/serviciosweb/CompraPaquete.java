
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para compraPaquete complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="compraPaquete"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="paqueteId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="clienteId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="validezDias" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fechaCompra" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="costo" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "compraPaquete", propOrder = {
    "paqueteId",
    "clienteId",
    "validezDias",
    "fechaCompra",
    "costo"
})
public class CompraPaquete {

    protected String paqueteId;
    protected String clienteId;
    protected int validezDias;
    protected String fechaCompra;
    protected double costo;

    /**
     * Obtiene el valor de la propiedad paqueteId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaqueteId() {
        return paqueteId;
    }

    /**
     * Define el valor de la propiedad paqueteId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaqueteId(String value) {
        this.paqueteId = value;
    }

    /**
     * Obtiene el valor de la propiedad clienteId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClienteId() {
        return clienteId;
    }

    /**
     * Define el valor de la propiedad clienteId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClienteId(String value) {
        this.clienteId = value;
    }

    /**
     * Obtiene el valor de la propiedad validezDias.
     * 
     */
    public int getValidezDias() {
        return validezDias;
    }

    /**
     * Define el valor de la propiedad validezDias.
     * 
     */
    public void setValidezDias(int value) {
        this.validezDias = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaCompra.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaCompra() {
        return fechaCompra;
    }

    /**
     * Define el valor de la propiedad fechaCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaCompra(String value) {
        this.fechaCompra = value;
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

}
