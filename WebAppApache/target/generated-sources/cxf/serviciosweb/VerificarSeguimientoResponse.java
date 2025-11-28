
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verificarSeguimientoResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verificarSeguimientoResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="siguiendo" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificarSeguimientoResponse", propOrder = {
    "siguiendo"
})
public class VerificarSeguimientoResponse {

    protected boolean siguiendo;

    /**
     * Obtiene el valor de la propiedad siguiendo.
     * 
     */
    public boolean isSiguiendo() {
        return siguiendo;
    }

    /**
     * Define el valor de la propiedad siguiendo.
     * 
     */
    public void setSiguiendo(boolean value) {
        this.siguiendo = value;
    }

}
