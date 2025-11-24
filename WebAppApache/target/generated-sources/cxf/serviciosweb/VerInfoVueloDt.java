
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verInfoVueloDt complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verInfoVueloDt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nombreVuelo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verInfoVueloDt", propOrder = {
    "nombreVuelo"
})
public class VerInfoVueloDt {

    protected String nombreVuelo;

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

}
