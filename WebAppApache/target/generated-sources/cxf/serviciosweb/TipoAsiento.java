
package serviciosweb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipoAsiento.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tipoAsiento"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TURISTA"/&gt;
 *     &lt;enumeration value="EJECUTIVO"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tipoAsiento")
@XmlEnum
public enum TipoAsiento {

    TURISTA,
    EJECUTIVO;

    public String value() {
        return name();
    }

    public static TipoAsiento fromValue(String v) {
        return valueOf(v);
    }

}
