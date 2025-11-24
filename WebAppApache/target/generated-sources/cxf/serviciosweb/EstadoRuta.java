
package serviciosweb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para estadoRuta.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="estadoRuta"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="INGRESADA"/&gt;
 *     &lt;enumeration value="CONFIRMADA"/&gt;
 *     &lt;enumeration value="RECHAZADA"/&gt;
 *     &lt;enumeration value="FINALIZADA"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "estadoRuta")
@XmlEnum
public enum EstadoRuta {

    INGRESADA,
    CONFIRMADA,
    RECHAZADA,
    FINALIZADA;

    public String value() {
        return name();
    }

    public static EstadoRuta fromValue(String v) {
        return valueOf(v);
    }

}
