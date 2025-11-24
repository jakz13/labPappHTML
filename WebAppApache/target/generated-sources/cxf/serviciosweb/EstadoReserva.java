
package serviciosweb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para estadoReserva.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="estadoReserva"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PENDIENTE"/&gt;
 *     &lt;enumeration value="CHECKIN_REALIZADO"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "estadoReserva")
@XmlEnum
public enum EstadoReserva {

    PENDIENTE,
    CHECKIN_REALIZADO;

    public String value() {
        return name();
    }

    public static EstadoReserva fromValue(String v) {
        return valueOf(v);
    }

}
