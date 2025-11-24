
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultarCheckinReserva complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultarCheckinReserva"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="reservaId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="clienteNickname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarCheckinReserva", propOrder = {
    "reservaId",
    "clienteNickname"
})
public class ConsultarCheckinReserva {

    protected Long reservaId;
    protected String clienteNickname;

    /**
     * Obtiene el valor de la propiedad reservaId.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getReservaId() {
        return reservaId;
    }

    /**
     * Define el valor de la propiedad reservaId.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setReservaId(Long value) {
        this.reservaId = value;
    }

    /**
     * Obtiene el valor de la propiedad clienteNickname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClienteNickname() {
        return clienteNickname;
    }

    /**
     * Define el valor de la propiedad clienteNickname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClienteNickname(String value) {
        this.clienteNickname = value;
    }

}
