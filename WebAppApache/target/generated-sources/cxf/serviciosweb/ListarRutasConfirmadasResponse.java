
package serviciosweb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para listarRutasConfirmadasResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="listarRutasConfirmadasResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rutasConfirmadas" type="{http://ServiciosWeb/}dtRutaVuelo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listarRutasConfirmadasResponse", propOrder = {
    "rutasConfirmadas"
})
public class ListarRutasConfirmadasResponse {

    protected List<DtRutaVuelo> rutasConfirmadas;

    /**
     * Gets the value of the rutasConfirmadas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the rutasConfirmadas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRutasConfirmadas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DtRutaVuelo }
     * 
     * 
     */
    public List<DtRutaVuelo> getRutasConfirmadas() {
        if (rutasConfirmadas == null) {
            rutasConfirmadas = new ArrayList<DtRutaVuelo>();
        }
        return this.rutasConfirmadas;
    }

}
