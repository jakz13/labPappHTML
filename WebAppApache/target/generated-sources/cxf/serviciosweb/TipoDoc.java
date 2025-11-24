
package serviciosweb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipoDoc.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="tipoDoc"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CEDULAIDENTIDAD"/&gt;
 *     &lt;enumeration value="PASAPORTE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "tipoDoc")
@XmlEnum
public enum TipoDoc {

    CEDULAIDENTIDAD,
    PASAPORTE;

    public String value() {
        return name();
    }

    public static TipoDoc fromValue(String v) {
        return valueOf(v);
    }

}
