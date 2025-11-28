
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para obtenerCantidadSeguidosResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="obtenerCantidadSeguidosResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cantidadSeguidos" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerCantidadSeguidosResponse", propOrder = {
    "cantidadSeguidos"
})
public class ObtenerCantidadSeguidosResponse {

    protected int cantidadSeguidos;

    /**
     * Obtiene el valor de la propiedad cantidadSeguidos.
     * 
     */
    public int getCantidadSeguidos() {
        return cantidadSeguidos;
    }

    /**
     * Define el valor de la propiedad cantidadSeguidos.
     * 
     */
    public void setCantidadSeguidos(int value) {
        this.cantidadSeguidos = value;
    }

}
