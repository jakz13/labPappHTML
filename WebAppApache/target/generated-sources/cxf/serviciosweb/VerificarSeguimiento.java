
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verificarSeguimiento complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verificarSeguimiento"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="seguidorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="seguidoId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificarSeguimiento", propOrder = {
    "seguidorId",
    "seguidoId"
})
public class VerificarSeguimiento {

    protected String seguidorId;
    protected String seguidoId;

    /**
     * Obtiene el valor de la propiedad seguidorId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeguidorId() {
        return seguidorId;
    }

    /**
     * Define el valor de la propiedad seguidorId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeguidorId(String value) {
        this.seguidorId = value;
    }

    /**
     * Obtiene el valor de la propiedad seguidoId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeguidoId() {
        return seguidoId;
    }

    /**
     * Define el valor de la propiedad seguidoId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeguidoId(String value) {
        this.seguidoId = value;
    }

}
