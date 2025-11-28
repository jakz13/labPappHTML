
package serviciosweb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para unfollowUsuario complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="unfollowUsuario"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="followerNickname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="targetNickname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "unfollowUsuario", propOrder = {
    "followerNickname",
    "targetNickname"
})
public class UnfollowUsuario {

    protected String followerNickname;
    protected String targetNickname;

    /**
     * Obtiene el valor de la propiedad followerNickname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFollowerNickname() {
        return followerNickname;
    }

    /**
     * Define el valor de la propiedad followerNickname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFollowerNickname(String value) {
        this.followerNickname = value;
    }

    /**
     * Obtiene el valor de la propiedad targetNickname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetNickname() {
        return targetNickname;
    }

    /**
     * Define el valor de la propiedad targetNickname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetNickname(String value) {
        this.targetNickname = value;
    }

}
