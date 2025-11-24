
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultarCheckinReservaResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultarCheckinReservaResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="checkin" type="{http://ServiciosWeb/}dtReserva" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarCheckinReservaResponse", propOrder = {
    "checkin"
})
public class ConsultarCheckinReservaResponse {

    protected DtReserva checkin;

    /**
     * Obtiene el valor de la propiedad checkin.
     * 
     * @return
     *     possible object is
     *     {@link DtReserva }
     *     
     */
    public DtReserva getCheckin() {
        return checkin;
    }

    /**
     * Define el valor de la propiedad checkin.
     * 
     * @param value
     *     allowed object is
     *     {@link DtReserva }
     *     
     */
    public void setCheckin(DtReserva value) {
        this.checkin = value;
    }

}
