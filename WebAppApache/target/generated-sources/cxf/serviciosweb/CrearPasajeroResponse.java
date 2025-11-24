
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para crearPasajeroResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="crearPasajeroResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="pasajero" type="{http://ServiciosWeb/}dtPasajero" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "crearPasajeroResponse", propOrder = {
    "pasajero"
})
public class CrearPasajeroResponse {

    protected DtPasajero pasajero;

    /**
     * Obtiene el valor de la propiedad pasajero.
     * 
     * @return
     *     possible object is
     *     {@link DtPasajero }
     *     
     */
    public DtPasajero getPasajero() {
        return pasajero;
    }

    /**
     * Define el valor de la propiedad pasajero.
     * 
     * @param value
     *     allowed object is
     *     {@link DtPasajero }
     *     
     */
    public void setPasajero(DtPasajero value) {
        this.pasajero = value;
    }

}
