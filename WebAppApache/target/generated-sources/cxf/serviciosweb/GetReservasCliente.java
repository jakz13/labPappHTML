
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para getReservasCliente complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getReservasCliente"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
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
@XmlType(name = "getReservasCliente", propOrder = {
    "clienteNickname"
})
public class GetReservasCliente {

    protected String clienteNickname;

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
