
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para obtenerTopRutasMasVisitadasResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="obtenerTopRutasMasVisitadasResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="topRutas" type="{http://ServiciosWeb/}dtRutaVuelo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerTopRutasMasVisitadasResponse", propOrder = {
    "topRutas"
})
public class ObtenerTopRutasMasVisitadasResponse {

    protected List<DtRutaVuelo> topRutas;

    /**
     * Gets the value of the topRutas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the topRutas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTopRutas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtRutaVuelo }
     * 
     * 
     */
    public List<DtRutaVuelo> getTopRutas() {
        if (topRutas == null) {
            topRutas = new ArrayList<DtRutaVuelo>();
        }
        return this.topRutas;
    }

}
