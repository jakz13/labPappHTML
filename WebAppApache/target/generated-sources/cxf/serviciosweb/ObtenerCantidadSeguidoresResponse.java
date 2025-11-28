
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para obtenerCantidadSeguidoresResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="obtenerCantidadSeguidoresResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cantidadSeguidores" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerCantidadSeguidoresResponse", propOrder = {
    "cantidadSeguidores"
})
public class ObtenerCantidadSeguidoresResponse {

    protected int cantidadSeguidores;

    /**
     * Obtiene el valor de la propiedad cantidadSeguidores.
     * 
     */
    public int getCantidadSeguidores() {
        return cantidadSeguidores;
    }

    /**
     * Define el valor de la propiedad cantidadSeguidores.
     * 
     */
    public void setCantidadSeguidores(int value) {
        this.cantidadSeguidores = value;
    }

}
