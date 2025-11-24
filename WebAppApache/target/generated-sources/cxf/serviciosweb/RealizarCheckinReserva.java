
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para realizarCheckinReserva complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="realizarCheckinReserva"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="reservaId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="asientosAsignados" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="horaEmbarque" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "realizarCheckinReserva", propOrder = {
    "reservaId",
    "asientosAsignados",
    "horaEmbarque"
})
public class RealizarCheckinReserva {

    protected Long reservaId;
    protected List<String> asientosAsignados;
    protected String horaEmbarque;

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
     * Gets the value of the asientosAsignados property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the asientosAsignados property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAsientosAsignados().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAsientosAsignados() {
        if (asientosAsignados == null) {
            asientosAsignados = new ArrayList<String>();
        }
        return this.asientosAsignados;
    }

    /**
     * Obtiene el valor de la propiedad horaEmbarque.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoraEmbarque() {
        return horaEmbarque;
    }

    /**
     * Define el valor de la propiedad horaEmbarque.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoraEmbarque(String value) {
        this.horaEmbarque = value;
    }

}
