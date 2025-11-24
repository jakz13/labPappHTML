
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para obtenerReservasConCheckinResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="obtenerReservasConCheckinResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="reservasConCheckin" type="{http://ServiciosWeb/}dtReserva" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerReservasConCheckinResponse", propOrder = {
    "reservasConCheckin"
})
public class ObtenerReservasConCheckinResponse {

    protected List<DtReserva> reservasConCheckin;

    /**
     * Gets the value of the reservasConCheckin property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the reservasConCheckin property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReservasConCheckin().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtReserva }
     * 
     * 
     */
    public List<DtReserva> getReservasConCheckin() {
        if (reservasConCheckin == null) {
            reservasConCheckin = new ArrayList<DtReserva>();
        }
        return this.reservasConCheckin;
    }

}
