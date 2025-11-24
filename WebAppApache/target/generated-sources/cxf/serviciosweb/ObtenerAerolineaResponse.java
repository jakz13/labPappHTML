
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para obtenerAerolineaResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="obtenerAerolineaResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="aerolinea" type="{http://ServiciosWeb/}dtAerolinea" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerAerolineaResponse", propOrder = {
    "aerolinea"
})
public class ObtenerAerolineaResponse {

    protected DtAerolinea aerolinea;

    /**
     * Obtiene el valor de la propiedad aerolinea.
     * 
     * @return
     *     possible object is
     *     {@link DtAerolinea }
     *     
     */
    public DtAerolinea getAerolinea() {
        return aerolinea;
    }

    /**
     * Define el valor de la propiedad aerolinea.
     * 
     * @param value
     *     allowed object is
     *     {@link DtAerolinea }
     *     
     */
    public void setAerolinea(DtAerolinea value) {
        this.aerolinea = value;
    }

}
