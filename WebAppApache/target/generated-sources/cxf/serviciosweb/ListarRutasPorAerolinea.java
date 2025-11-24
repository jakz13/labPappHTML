
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para listarRutasPorAerolinea complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="listarRutasPorAerolinea"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nombreAerolinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listarRutasPorAerolinea", propOrder = {
    "nombreAerolinea"
})
public class ListarRutasPorAerolinea {

    protected String nombreAerolinea;

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

}
