
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para finalizarRutaVuelo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="finalizarRutaVuelo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nombreRuta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "finalizarRutaVuelo", propOrder = {
    "nombreRuta"
})
public class FinalizarRutaVuelo {

    protected String nombreRuta;

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

}
