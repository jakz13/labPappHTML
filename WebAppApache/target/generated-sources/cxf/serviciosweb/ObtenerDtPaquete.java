
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para obtenerDtPaquete complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="obtenerDtPaquete"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="paqueteId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerDtPaquete", propOrder = {
    "paqueteId"
})
public class ObtenerDtPaquete {

    protected String paqueteId;

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

}
