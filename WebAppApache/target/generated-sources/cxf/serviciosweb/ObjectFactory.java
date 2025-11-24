
package serviciosweb;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the serviciosweb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AltaAerolinea_QNAME = new QName("http://ServiciosWeb/", "altaAerolinea");
    private final static QName _AltaAerolineaResponse_QNAME = new QName("http://ServiciosWeb/", "altaAerolineaResponse");
    private final static QName _AltaCliente_QNAME = new QName("http://ServiciosWeb/", "altaCliente");
    private final static QName _AltaClienteResponse_QNAME = new QName("http://ServiciosWeb/", "altaClienteResponse");
    private final static QName _AltaRutaVuelo_QNAME = new QName("http://ServiciosWeb/", "altaRutaVuelo");
    private final static QName _AltaRutaVueloResponse_QNAME = new QName("http://ServiciosWeb/", "altaRutaVueloResponse");
    private final static QName _AltaVuelo_QNAME = new QName("http://ServiciosWeb/", "altaVuelo");
    private final static QName _AltaVueloResponse_QNAME = new QName("http://ServiciosWeb/", "altaVueloResponse");
    private final static QName _CalcularCostoReserva_QNAME = new QName("http://ServiciosWeb/", "calcularCostoReserva");
    private final static QName _CalcularCostoReservaResponse_QNAME = new QName("http://ServiciosWeb/", "calcularCostoReservaResponse");
    private final static QName _CargarDesdeBd_QNAME = new QName("http://ServiciosWeb/", "cargarDesdeBd");
    private final static QName _CargarDesdeBdResponse_QNAME = new QName("http://ServiciosWeb/", "cargarDesdeBdResponse");
    private final static QName _CompraPaquete_QNAME = new QName("http://ServiciosWeb/", "compraPaquete");
    private final static QName _CompraPaqueteResponse_QNAME = new QName("http://ServiciosWeb/", "compraPaqueteResponse");
    private final static QName _ConsultarCheckinReserva_QNAME = new QName("http://ServiciosWeb/", "consultarCheckinReserva");
    private final static QName _ConsultarCheckinReservaResponse_QNAME = new QName("http://ServiciosWeb/", "consultarCheckinReservaResponse");
    private final static QName _CrearPasajero_QNAME = new QName("http://ServiciosWeb/", "crearPasajero");
    private final static QName _CrearPasajeroResponse_QNAME = new QName("http://ServiciosWeb/", "crearPasajeroResponse");
    private final static QName _CrearYRegistrarReserva_QNAME = new QName("http://ServiciosWeb/", "crearYRegistrarReserva");
    private final static QName _CrearYRegistrarReservaResponse_QNAME = new QName("http://ServiciosWeb/", "crearYRegistrarReservaResponse");
    private final static QName _GetDtItemRutasPaquete_QNAME = new QName("http://ServiciosWeb/", "getDtItemRutasPaquete");
    private final static QName _GetDtItemRutasPaqueteResponse_QNAME = new QName("http://ServiciosWeb/", "getDtItemRutasPaqueteResponse");
    private final static QName _GetReservasCliente_QNAME = new QName("http://ServiciosWeb/", "getReservasCliente");
    private final static QName _GetReservasClienteResponse_QNAME = new QName("http://ServiciosWeb/", "getReservasClienteResponse");
    private final static QName _IncrementarVisitasRuta_QNAME = new QName("http://ServiciosWeb/", "incrementarVisitasRuta");
    private final static QName _IncrementarVisitasRutaResponse_QNAME = new QName("http://ServiciosWeb/", "incrementarVisitasRutaResponse");
    private final static QName _ListarAerolineas_QNAME = new QName("http://ServiciosWeb/", "listarAerolineas");
    private final static QName _ListarAerolineasResponse_QNAME = new QName("http://ServiciosWeb/", "listarAerolineasResponse");
    private final static QName _ListarCiudades_QNAME = new QName("http://ServiciosWeb/", "listarCiudades");
    private final static QName _ListarCiudadesResponse_QNAME = new QName("http://ServiciosWeb/", "listarCiudadesResponse");
    private final static QName _ListarClientes_QNAME = new QName("http://ServiciosWeb/", "listarClientes");
    private final static QName _ListarClientesResponse_QNAME = new QName("http://ServiciosWeb/", "listarClientesResponse");
    private final static QName _ListarPaquetes_QNAME = new QName("http://ServiciosWeb/", "listarPaquetes");
    private final static QName _ListarPaquetesResponse_QNAME = new QName("http://ServiciosWeb/", "listarPaquetesResponse");
    private final static QName _ListarRutasConfirmadas_QNAME = new QName("http://ServiciosWeb/", "listarRutasConfirmadas");
    private final static QName _ListarRutasConfirmadasResponse_QNAME = new QName("http://ServiciosWeb/", "listarRutasConfirmadasResponse");
    private final static QName _ListarRutasPorAerolinea_QNAME = new QName("http://ServiciosWeb/", "listarRutasPorAerolinea");
    private final static QName _ListarRutasPorAerolineaResponse_QNAME = new QName("http://ServiciosWeb/", "listarRutasPorAerolineaResponse");
    private final static QName _ListarVuelosPorRuta_QNAME = new QName("http://ServiciosWeb/", "listarVuelosPorRuta");
    private final static QName _ListarVuelosPorRutaResponse_QNAME = new QName("http://ServiciosWeb/", "listarVuelosPorRutaResponse");
    private final static QName _ModificarDatosAerolineaCompleto_QNAME = new QName("http://ServiciosWeb/", "modificarDatosAerolineaCompleto");
    private final static QName _ModificarDatosAerolineaCompletoResponse_QNAME = new QName("http://ServiciosWeb/", "modificarDatosAerolineaCompletoResponse");
    private final static QName _ModificarDatosClienteCompleto_QNAME = new QName("http://ServiciosWeb/", "modificarDatosClienteCompleto");
    private final static QName _ModificarDatosClienteCompletoResponse_QNAME = new QName("http://ServiciosWeb/", "modificarDatosClienteCompletoResponse");
    private final static QName _ObtenerAerolinea_QNAME = new QName("http://ServiciosWeb/", "obtenerAerolinea");
    private final static QName _ObtenerAerolineaResponse_QNAME = new QName("http://ServiciosWeb/", "obtenerAerolineaResponse");
    private final static QName _ObtenerCliente_QNAME = new QName("http://ServiciosWeb/", "obtenerCliente");
    private final static QName _ObtenerClienteResponse_QNAME = new QName("http://ServiciosWeb/", "obtenerClienteResponse");
    private final static QName _ObtenerDtPaquete_QNAME = new QName("http://ServiciosWeb/", "obtenerDtPaquete");
    private final static QName _ObtenerDtPaqueteResponse_QNAME = new QName("http://ServiciosWeb/", "obtenerDtPaqueteResponse");
    private final static QName _ObtenerReservasConCheckin_QNAME = new QName("http://ServiciosWeb/", "obtenerReservasConCheckin");
    private final static QName _ObtenerReservasConCheckinResponse_QNAME = new QName("http://ServiciosWeb/", "obtenerReservasConCheckinResponse");
    private final static QName _ObtenerTopRutasMasVisitadas_QNAME = new QName("http://ServiciosWeb/", "obtenerTopRutasMasVisitadas");
    private final static QName _ObtenerTopRutasMasVisitadasResponse_QNAME = new QName("http://ServiciosWeb/", "obtenerTopRutasMasVisitadasResponse");
    private final static QName _ObtenerVuelo_QNAME = new QName("http://ServiciosWeb/", "obtenerVuelo");
    private final static QName _ObtenerVueloResponse_QNAME = new QName("http://ServiciosWeb/", "obtenerVueloResponse");
    private final static QName _Ping_QNAME = new QName("http://ServiciosWeb/", "ping");
    private final static QName _PingResponse_QNAME = new QName("http://ServiciosWeb/", "pingResponse");
    private final static QName _RealizarCheckinReserva_QNAME = new QName("http://ServiciosWeb/", "realizarCheckinReserva");
    private final static QName _RealizarCheckinReservaResponse_QNAME = new QName("http://ServiciosWeb/", "realizarCheckinReservaResponse");
    private final static QName _VerInfoVueloDt_QNAME = new QName("http://ServiciosWeb/", "verInfoVueloDt");
    private final static QName _VerInfoVueloDtResponse_QNAME = new QName("http://ServiciosWeb/", "verInfoVueloDtResponse");
    private final static QName _VerificarLogin_QNAME = new QName("http://ServiciosWeb/", "verificarLogin");
    private final static QName _VerificarLoginResponse_QNAME = new QName("http://ServiciosWeb/", "verificarLoginResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: serviciosweb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AltaAerolinea }
     * 
     */
    public AltaAerolinea createAltaAerolinea() {
        return new AltaAerolinea();
    }

    /**
     * Create an instance of {@link AltaAerolineaResponse }
     * 
     */
    public AltaAerolineaResponse createAltaAerolineaResponse() {
        return new AltaAerolineaResponse();
    }

    /**
     * Create an instance of {@link AltaCliente }
     * 
     */
    public AltaCliente createAltaCliente() {
        return new AltaCliente();
    }

    /**
     * Create an instance of {@link AltaClienteResponse }
     * 
     */
    public AltaClienteResponse createAltaClienteResponse() {
        return new AltaClienteResponse();
    }

    /**
     * Create an instance of {@link AltaRutaVuelo }
     * 
     */
    public AltaRutaVuelo createAltaRutaVuelo() {
        return new AltaRutaVuelo();
    }

    /**
     * Create an instance of {@link AltaRutaVueloResponse }
     * 
     */
    public AltaRutaVueloResponse createAltaRutaVueloResponse() {
        return new AltaRutaVueloResponse();
    }

    /**
     * Create an instance of {@link AltaVuelo }
     * 
     */
    public AltaVuelo createAltaVuelo() {
        return new AltaVuelo();
    }

    /**
     * Create an instance of {@link AltaVueloResponse }
     * 
     */
    public AltaVueloResponse createAltaVueloResponse() {
        return new AltaVueloResponse();
    }

    /**
     * Create an instance of {@link CalcularCostoReserva }
     * 
     */
    public CalcularCostoReserva createCalcularCostoReserva() {
        return new CalcularCostoReserva();
    }

    /**
     * Create an instance of {@link CalcularCostoReservaResponse }
     * 
     */
    public CalcularCostoReservaResponse createCalcularCostoReservaResponse() {
        return new CalcularCostoReservaResponse();
    }

    /**
     * Create an instance of {@link CargarDesdeBd }
     * 
     */
    public CargarDesdeBd createCargarDesdeBd() {
        return new CargarDesdeBd();
    }

    /**
     * Create an instance of {@link CargarDesdeBdResponse }
     * 
     */
    public CargarDesdeBdResponse createCargarDesdeBdResponse() {
        return new CargarDesdeBdResponse();
    }

    /**
     * Create an instance of {@link CompraPaquete }
     * 
     */
    public CompraPaquete createCompraPaquete() {
        return new CompraPaquete();
    }

    /**
     * Create an instance of {@link CompraPaqueteResponse }
     * 
     */
    public CompraPaqueteResponse createCompraPaqueteResponse() {
        return new CompraPaqueteResponse();
    }

    /**
     * Create an instance of {@link ConsultarCheckinReserva }
     * 
     */
    public ConsultarCheckinReserva createConsultarCheckinReserva() {
        return new ConsultarCheckinReserva();
    }

    /**
     * Create an instance of {@link ConsultarCheckinReservaResponse }
     * 
     */
    public ConsultarCheckinReservaResponse createConsultarCheckinReservaResponse() {
        return new ConsultarCheckinReservaResponse();
    }

    /**
     * Create an instance of {@link CrearPasajero }
     * 
     */
    public CrearPasajero createCrearPasajero() {
        return new CrearPasajero();
    }

    /**
     * Create an instance of {@link CrearPasajeroResponse }
     * 
     */
    public CrearPasajeroResponse createCrearPasajeroResponse() {
        return new CrearPasajeroResponse();
    }

    /**
     * Create an instance of {@link CrearYRegistrarReserva }
     * 
     */
    public CrearYRegistrarReserva createCrearYRegistrarReserva() {
        return new CrearYRegistrarReserva();
    }

    /**
     * Create an instance of {@link CrearYRegistrarReservaResponse }
     * 
     */
    public CrearYRegistrarReservaResponse createCrearYRegistrarReservaResponse() {
        return new CrearYRegistrarReservaResponse();
    }

    /**
     * Create an instance of {@link GetDtItemRutasPaquete }
     * 
     */
    public GetDtItemRutasPaquete createGetDtItemRutasPaquete() {
        return new GetDtItemRutasPaquete();
    }

    /**
     * Create an instance of {@link GetDtItemRutasPaqueteResponse }
     * 
     */
    public GetDtItemRutasPaqueteResponse createGetDtItemRutasPaqueteResponse() {
        return new GetDtItemRutasPaqueteResponse();
    }

    /**
     * Create an instance of {@link GetReservasCliente }
     * 
     */
    public GetReservasCliente createGetReservasCliente() {
        return new GetReservasCliente();
    }

    /**
     * Create an instance of {@link GetReservasClienteResponse }
     * 
     */
    public GetReservasClienteResponse createGetReservasClienteResponse() {
        return new GetReservasClienteResponse();
    }

    /**
     * Create an instance of {@link IncrementarVisitasRuta }
     * 
     */
    public IncrementarVisitasRuta createIncrementarVisitasRuta() {
        return new IncrementarVisitasRuta();
    }

    /**
     * Create an instance of {@link IncrementarVisitasRutaResponse }
     * 
     */
    public IncrementarVisitasRutaResponse createIncrementarVisitasRutaResponse() {
        return new IncrementarVisitasRutaResponse();
    }

    /**
     * Create an instance of {@link ListarAerolineas }
     * 
     */
    public ListarAerolineas createListarAerolineas() {
        return new ListarAerolineas();
    }

    /**
     * Create an instance of {@link ListarAerolineasResponse }
     * 
     */
    public ListarAerolineasResponse createListarAerolineasResponse() {
        return new ListarAerolineasResponse();
    }

    /**
     * Create an instance of {@link ListarCiudades }
     * 
     */
    public ListarCiudades createListarCiudades() {
        return new ListarCiudades();
    }

    /**
     * Create an instance of {@link ListarCiudadesResponse }
     * 
     */
    public ListarCiudadesResponse createListarCiudadesResponse() {
        return new ListarCiudadesResponse();
    }

    /**
     * Create an instance of {@link ListarClientes }
     * 
     */
    public ListarClientes createListarClientes() {
        return new ListarClientes();
    }

    /**
     * Create an instance of {@link ListarClientesResponse }
     * 
     */
    public ListarClientesResponse createListarClientesResponse() {
        return new ListarClientesResponse();
    }

    /**
     * Create an instance of {@link ListarPaquetes }
     * 
     */
    public ListarPaquetes createListarPaquetes() {
        return new ListarPaquetes();
    }

    /**
     * Create an instance of {@link ListarPaquetesResponse }
     * 
     */
    public ListarPaquetesResponse createListarPaquetesResponse() {
        return new ListarPaquetesResponse();
    }

    /**
     * Create an instance of {@link ListarRutasConfirmadas }
     * 
     */
    public ListarRutasConfirmadas createListarRutasConfirmadas() {
        return new ListarRutasConfirmadas();
    }

    /**
     * Create an instance of {@link ListarRutasConfirmadasResponse }
     * 
     */
    public ListarRutasConfirmadasResponse createListarRutasConfirmadasResponse() {
        return new ListarRutasConfirmadasResponse();
    }

    /**
     * Create an instance of {@link ListarRutasPorAerolinea }
     * 
     */
    public ListarRutasPorAerolinea createListarRutasPorAerolinea() {
        return new ListarRutasPorAerolinea();
    }

    /**
     * Create an instance of {@link ListarRutasPorAerolineaResponse }
     * 
     */
    public ListarRutasPorAerolineaResponse createListarRutasPorAerolineaResponse() {
        return new ListarRutasPorAerolineaResponse();
    }

    /**
     * Create an instance of {@link ListarVuelosPorRuta }
     * 
     */
    public ListarVuelosPorRuta createListarVuelosPorRuta() {
        return new ListarVuelosPorRuta();
    }

    /**
     * Create an instance of {@link ListarVuelosPorRutaResponse }
     * 
     */
    public ListarVuelosPorRutaResponse createListarVuelosPorRutaResponse() {
        return new ListarVuelosPorRutaResponse();
    }

    /**
     * Create an instance of {@link ModificarDatosAerolineaCompleto }
     * 
     */
    public ModificarDatosAerolineaCompleto createModificarDatosAerolineaCompleto() {
        return new ModificarDatosAerolineaCompleto();
    }

    /**
     * Create an instance of {@link ModificarDatosAerolineaCompletoResponse }
     * 
     */
    public ModificarDatosAerolineaCompletoResponse createModificarDatosAerolineaCompletoResponse() {
        return new ModificarDatosAerolineaCompletoResponse();
    }

    /**
     * Create an instance of {@link ModificarDatosClienteCompleto }
     * 
     */
    public ModificarDatosClienteCompleto createModificarDatosClienteCompleto() {
        return new ModificarDatosClienteCompleto();
    }

    /**
     * Create an instance of {@link ModificarDatosClienteCompletoResponse }
     * 
     */
    public ModificarDatosClienteCompletoResponse createModificarDatosClienteCompletoResponse() {
        return new ModificarDatosClienteCompletoResponse();
    }

    /**
     * Create an instance of {@link ObtenerAerolinea }
     * 
     */
    public ObtenerAerolinea createObtenerAerolinea() {
        return new ObtenerAerolinea();
    }

    /**
     * Create an instance of {@link ObtenerAerolineaResponse }
     * 
     */
    public ObtenerAerolineaResponse createObtenerAerolineaResponse() {
        return new ObtenerAerolineaResponse();
    }

    /**
     * Create an instance of {@link ObtenerCliente }
     * 
     */
    public ObtenerCliente createObtenerCliente() {
        return new ObtenerCliente();
    }

    /**
     * Create an instance of {@link ObtenerClienteResponse }
     * 
     */
    public ObtenerClienteResponse createObtenerClienteResponse() {
        return new ObtenerClienteResponse();
    }

    /**
     * Create an instance of {@link ObtenerDtPaquete }
     * 
     */
    public ObtenerDtPaquete createObtenerDtPaquete() {
        return new ObtenerDtPaquete();
    }

    /**
     * Create an instance of {@link ObtenerDtPaqueteResponse }
     * 
     */
    public ObtenerDtPaqueteResponse createObtenerDtPaqueteResponse() {
        return new ObtenerDtPaqueteResponse();
    }

    /**
     * Create an instance of {@link ObtenerReservasConCheckin }
     * 
     */
    public ObtenerReservasConCheckin createObtenerReservasConCheckin() {
        return new ObtenerReservasConCheckin();
    }

    /**
     * Create an instance of {@link ObtenerReservasConCheckinResponse }
     * 
     */
    public ObtenerReservasConCheckinResponse createObtenerReservasConCheckinResponse() {
        return new ObtenerReservasConCheckinResponse();
    }

    /**
     * Create an instance of {@link ObtenerTopRutasMasVisitadas }
     * 
     */
    public ObtenerTopRutasMasVisitadas createObtenerTopRutasMasVisitadas() {
        return new ObtenerTopRutasMasVisitadas();
    }

    /**
     * Create an instance of {@link ObtenerTopRutasMasVisitadasResponse }
     * 
     */
    public ObtenerTopRutasMasVisitadasResponse createObtenerTopRutasMasVisitadasResponse() {
        return new ObtenerTopRutasMasVisitadasResponse();
    }

    /**
     * Create an instance of {@link ObtenerVuelo }
     * 
     */
    public ObtenerVuelo createObtenerVuelo() {
        return new ObtenerVuelo();
    }

    /**
     * Create an instance of {@link ObtenerVueloResponse }
     * 
     */
    public ObtenerVueloResponse createObtenerVueloResponse() {
        return new ObtenerVueloResponse();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link RealizarCheckinReserva }
     * 
     */
    public RealizarCheckinReserva createRealizarCheckinReserva() {
        return new RealizarCheckinReserva();
    }

    /**
     * Create an instance of {@link RealizarCheckinReservaResponse }
     * 
     */
    public RealizarCheckinReservaResponse createRealizarCheckinReservaResponse() {
        return new RealizarCheckinReservaResponse();
    }

    /**
     * Create an instance of {@link VerInfoVueloDt }
     * 
     */
    public VerInfoVueloDt createVerInfoVueloDt() {
        return new VerInfoVueloDt();
    }

    /**
     * Create an instance of {@link VerInfoVueloDtResponse }
     * 
     */
    public VerInfoVueloDtResponse createVerInfoVueloDtResponse() {
        return new VerInfoVueloDtResponse();
    }

    /**
     * Create an instance of {@link VerificarLogin }
     * 
     */
    public VerificarLogin createVerificarLogin() {
        return new VerificarLogin();
    }

    /**
     * Create an instance of {@link VerificarLoginResponse }
     * 
     */
    public VerificarLoginResponse createVerificarLoginResponse() {
        return new VerificarLoginResponse();
    }

    /**
     * Create an instance of {@link DtCiudad }
     * 
     */
    public DtCiudad createDtCiudad() {
        return new DtCiudad();
    }

    /**
     * Create an instance of {@link LocalDate }
     * 
     */
    public LocalDate createLocalDate() {
        return new LocalDate();
    }

    /**
     * Create an instance of {@link DtPaquete }
     * 
     */
    public DtPaquete createDtPaquete() {
        return new DtPaquete();
    }

    /**
     * Create an instance of {@link DtItemPaquete }
     * 
     */
    public DtItemPaquete createDtItemPaquete() {
        return new DtItemPaquete();
    }

    /**
     * Create an instance of {@link DtRutaVuelo }
     * 
     */
    public DtRutaVuelo createDtRutaVuelo() {
        return new DtRutaVuelo();
    }

    /**
     * Create an instance of {@link DtVuelo }
     * 
     */
    public DtVuelo createDtVuelo() {
        return new DtVuelo();
    }

    /**
     * Create an instance of {@link DtReserva }
     * 
     */
    public DtReserva createDtReserva() {
        return new DtReserva();
    }

    /**
     * Create an instance of {@link LocalTime }
     * 
     */
    public LocalTime createLocalTime() {
        return new LocalTime();
    }

    /**
     * Create an instance of {@link DtPasajero }
     * 
     */
    public DtPasajero createDtPasajero() {
        return new DtPasajero();
    }

    /**
     * Create an instance of {@link DtCliente }
     * 
     */
    public DtCliente createDtCliente() {
        return new DtCliente();
    }

    /**
     * Create an instance of {@link DtUsuario }
     * 
     */
    public DtUsuario createDtUsuario() {
        return new DtUsuario();
    }

    /**
     * Create an instance of {@link DtAerolinea }
     * 
     */
    public DtAerolinea createDtAerolinea() {
        return new DtAerolinea();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaAerolinea }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaAerolinea }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "altaAerolinea")
    public JAXBElement<AltaAerolinea> createAltaAerolinea(AltaAerolinea value) {
        return new JAXBElement<AltaAerolinea>(_AltaAerolinea_QNAME, AltaAerolinea.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaAerolineaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaAerolineaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "altaAerolineaResponse")
    public JAXBElement<AltaAerolineaResponse> createAltaAerolineaResponse(AltaAerolineaResponse value) {
        return new JAXBElement<AltaAerolineaResponse>(_AltaAerolineaResponse_QNAME, AltaAerolineaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaCliente }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaCliente }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "altaCliente")
    public JAXBElement<AltaCliente> createAltaCliente(AltaCliente value) {
        return new JAXBElement<AltaCliente>(_AltaCliente_QNAME, AltaCliente.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaClienteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaClienteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "altaClienteResponse")
    public JAXBElement<AltaClienteResponse> createAltaClienteResponse(AltaClienteResponse value) {
        return new JAXBElement<AltaClienteResponse>(_AltaClienteResponse_QNAME, AltaClienteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaRutaVuelo }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaRutaVuelo }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "altaRutaVuelo")
    public JAXBElement<AltaRutaVuelo> createAltaRutaVuelo(AltaRutaVuelo value) {
        return new JAXBElement<AltaRutaVuelo>(_AltaRutaVuelo_QNAME, AltaRutaVuelo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaRutaVueloResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaRutaVueloResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "altaRutaVueloResponse")
    public JAXBElement<AltaRutaVueloResponse> createAltaRutaVueloResponse(AltaRutaVueloResponse value) {
        return new JAXBElement<AltaRutaVueloResponse>(_AltaRutaVueloResponse_QNAME, AltaRutaVueloResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaVuelo }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaVuelo }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "altaVuelo")
    public JAXBElement<AltaVuelo> createAltaVuelo(AltaVuelo value) {
        return new JAXBElement<AltaVuelo>(_AltaVuelo_QNAME, AltaVuelo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltaVueloResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AltaVueloResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "altaVueloResponse")
    public JAXBElement<AltaVueloResponse> createAltaVueloResponse(AltaVueloResponse value) {
        return new JAXBElement<AltaVueloResponse>(_AltaVueloResponse_QNAME, AltaVueloResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalcularCostoReserva }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CalcularCostoReserva }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "calcularCostoReserva")
    public JAXBElement<CalcularCostoReserva> createCalcularCostoReserva(CalcularCostoReserva value) {
        return new JAXBElement<CalcularCostoReserva>(_CalcularCostoReserva_QNAME, CalcularCostoReserva.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CalcularCostoReservaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CalcularCostoReservaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "calcularCostoReservaResponse")
    public JAXBElement<CalcularCostoReservaResponse> createCalcularCostoReservaResponse(CalcularCostoReservaResponse value) {
        return new JAXBElement<CalcularCostoReservaResponse>(_CalcularCostoReservaResponse_QNAME, CalcularCostoReservaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CargarDesdeBd }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CargarDesdeBd }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "cargarDesdeBd")
    public JAXBElement<CargarDesdeBd> createCargarDesdeBd(CargarDesdeBd value) {
        return new JAXBElement<CargarDesdeBd>(_CargarDesdeBd_QNAME, CargarDesdeBd.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CargarDesdeBdResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CargarDesdeBdResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "cargarDesdeBdResponse")
    public JAXBElement<CargarDesdeBdResponse> createCargarDesdeBdResponse(CargarDesdeBdResponse value) {
        return new JAXBElement<CargarDesdeBdResponse>(_CargarDesdeBdResponse_QNAME, CargarDesdeBdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompraPaquete }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CompraPaquete }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "compraPaquete")
    public JAXBElement<CompraPaquete> createCompraPaquete(CompraPaquete value) {
        return new JAXBElement<CompraPaquete>(_CompraPaquete_QNAME, CompraPaquete.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompraPaqueteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CompraPaqueteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "compraPaqueteResponse")
    public JAXBElement<CompraPaqueteResponse> createCompraPaqueteResponse(CompraPaqueteResponse value) {
        return new JAXBElement<CompraPaqueteResponse>(_CompraPaqueteResponse_QNAME, CompraPaqueteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarCheckinReserva }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarCheckinReserva }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "consultarCheckinReserva")
    public JAXBElement<ConsultarCheckinReserva> createConsultarCheckinReserva(ConsultarCheckinReserva value) {
        return new JAXBElement<ConsultarCheckinReserva>(_ConsultarCheckinReserva_QNAME, ConsultarCheckinReserva.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarCheckinReservaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarCheckinReservaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "consultarCheckinReservaResponse")
    public JAXBElement<ConsultarCheckinReservaResponse> createConsultarCheckinReservaResponse(ConsultarCheckinReservaResponse value) {
        return new JAXBElement<ConsultarCheckinReservaResponse>(_ConsultarCheckinReservaResponse_QNAME, ConsultarCheckinReservaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearPasajero }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CrearPasajero }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "crearPasajero")
    public JAXBElement<CrearPasajero> createCrearPasajero(CrearPasajero value) {
        return new JAXBElement<CrearPasajero>(_CrearPasajero_QNAME, CrearPasajero.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearPasajeroResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CrearPasajeroResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "crearPasajeroResponse")
    public JAXBElement<CrearPasajeroResponse> createCrearPasajeroResponse(CrearPasajeroResponse value) {
        return new JAXBElement<CrearPasajeroResponse>(_CrearPasajeroResponse_QNAME, CrearPasajeroResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearYRegistrarReserva }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CrearYRegistrarReserva }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "crearYRegistrarReserva")
    public JAXBElement<CrearYRegistrarReserva> createCrearYRegistrarReserva(CrearYRegistrarReserva value) {
        return new JAXBElement<CrearYRegistrarReserva>(_CrearYRegistrarReserva_QNAME, CrearYRegistrarReserva.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearYRegistrarReservaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CrearYRegistrarReservaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "crearYRegistrarReservaResponse")
    public JAXBElement<CrearYRegistrarReservaResponse> createCrearYRegistrarReservaResponse(CrearYRegistrarReservaResponse value) {
        return new JAXBElement<CrearYRegistrarReservaResponse>(_CrearYRegistrarReservaResponse_QNAME, CrearYRegistrarReservaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDtItemRutasPaquete }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetDtItemRutasPaquete }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "getDtItemRutasPaquete")
    public JAXBElement<GetDtItemRutasPaquete> createGetDtItemRutasPaquete(GetDtItemRutasPaquete value) {
        return new JAXBElement<GetDtItemRutasPaquete>(_GetDtItemRutasPaquete_QNAME, GetDtItemRutasPaquete.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDtItemRutasPaqueteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetDtItemRutasPaqueteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "getDtItemRutasPaqueteResponse")
    public JAXBElement<GetDtItemRutasPaqueteResponse> createGetDtItemRutasPaqueteResponse(GetDtItemRutasPaqueteResponse value) {
        return new JAXBElement<GetDtItemRutasPaqueteResponse>(_GetDtItemRutasPaqueteResponse_QNAME, GetDtItemRutasPaqueteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReservasCliente }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetReservasCliente }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "getReservasCliente")
    public JAXBElement<GetReservasCliente> createGetReservasCliente(GetReservasCliente value) {
        return new JAXBElement<GetReservasCliente>(_GetReservasCliente_QNAME, GetReservasCliente.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReservasClienteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetReservasClienteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "getReservasClienteResponse")
    public JAXBElement<GetReservasClienteResponse> createGetReservasClienteResponse(GetReservasClienteResponse value) {
        return new JAXBElement<GetReservasClienteResponse>(_GetReservasClienteResponse_QNAME, GetReservasClienteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IncrementarVisitasRuta }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link IncrementarVisitasRuta }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "incrementarVisitasRuta")
    public JAXBElement<IncrementarVisitasRuta> createIncrementarVisitasRuta(IncrementarVisitasRuta value) {
        return new JAXBElement<IncrementarVisitasRuta>(_IncrementarVisitasRuta_QNAME, IncrementarVisitasRuta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IncrementarVisitasRutaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link IncrementarVisitasRutaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "incrementarVisitasRutaResponse")
    public JAXBElement<IncrementarVisitasRutaResponse> createIncrementarVisitasRutaResponse(IncrementarVisitasRutaResponse value) {
        return new JAXBElement<IncrementarVisitasRutaResponse>(_IncrementarVisitasRutaResponse_QNAME, IncrementarVisitasRutaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarAerolineas }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarAerolineas }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarAerolineas")
    public JAXBElement<ListarAerolineas> createListarAerolineas(ListarAerolineas value) {
        return new JAXBElement<ListarAerolineas>(_ListarAerolineas_QNAME, ListarAerolineas.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarAerolineasResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarAerolineasResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarAerolineasResponse")
    public JAXBElement<ListarAerolineasResponse> createListarAerolineasResponse(ListarAerolineasResponse value) {
        return new JAXBElement<ListarAerolineasResponse>(_ListarAerolineasResponse_QNAME, ListarAerolineasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarCiudades }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarCiudades }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarCiudades")
    public JAXBElement<ListarCiudades> createListarCiudades(ListarCiudades value) {
        return new JAXBElement<ListarCiudades>(_ListarCiudades_QNAME, ListarCiudades.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarCiudadesResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarCiudadesResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarCiudadesResponse")
    public JAXBElement<ListarCiudadesResponse> createListarCiudadesResponse(ListarCiudadesResponse value) {
        return new JAXBElement<ListarCiudadesResponse>(_ListarCiudadesResponse_QNAME, ListarCiudadesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarClientes }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarClientes }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarClientes")
    public JAXBElement<ListarClientes> createListarClientes(ListarClientes value) {
        return new JAXBElement<ListarClientes>(_ListarClientes_QNAME, ListarClientes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarClientesResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarClientesResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarClientesResponse")
    public JAXBElement<ListarClientesResponse> createListarClientesResponse(ListarClientesResponse value) {
        return new JAXBElement<ListarClientesResponse>(_ListarClientesResponse_QNAME, ListarClientesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarPaquetes }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarPaquetes }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarPaquetes")
    public JAXBElement<ListarPaquetes> createListarPaquetes(ListarPaquetes value) {
        return new JAXBElement<ListarPaquetes>(_ListarPaquetes_QNAME, ListarPaquetes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarPaquetesResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarPaquetesResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarPaquetesResponse")
    public JAXBElement<ListarPaquetesResponse> createListarPaquetesResponse(ListarPaquetesResponse value) {
        return new JAXBElement<ListarPaquetesResponse>(_ListarPaquetesResponse_QNAME, ListarPaquetesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarRutasConfirmadas }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarRutasConfirmadas }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarRutasConfirmadas")
    public JAXBElement<ListarRutasConfirmadas> createListarRutasConfirmadas(ListarRutasConfirmadas value) {
        return new JAXBElement<ListarRutasConfirmadas>(_ListarRutasConfirmadas_QNAME, ListarRutasConfirmadas.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarRutasConfirmadasResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarRutasConfirmadasResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarRutasConfirmadasResponse")
    public JAXBElement<ListarRutasConfirmadasResponse> createListarRutasConfirmadasResponse(ListarRutasConfirmadasResponse value) {
        return new JAXBElement<ListarRutasConfirmadasResponse>(_ListarRutasConfirmadasResponse_QNAME, ListarRutasConfirmadasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarRutasPorAerolinea }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarRutasPorAerolinea }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarRutasPorAerolinea")
    public JAXBElement<ListarRutasPorAerolinea> createListarRutasPorAerolinea(ListarRutasPorAerolinea value) {
        return new JAXBElement<ListarRutasPorAerolinea>(_ListarRutasPorAerolinea_QNAME, ListarRutasPorAerolinea.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarRutasPorAerolineaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarRutasPorAerolineaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarRutasPorAerolineaResponse")
    public JAXBElement<ListarRutasPorAerolineaResponse> createListarRutasPorAerolineaResponse(ListarRutasPorAerolineaResponse value) {
        return new JAXBElement<ListarRutasPorAerolineaResponse>(_ListarRutasPorAerolineaResponse_QNAME, ListarRutasPorAerolineaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarVuelosPorRuta }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarVuelosPorRuta }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarVuelosPorRuta")
    public JAXBElement<ListarVuelosPorRuta> createListarVuelosPorRuta(ListarVuelosPorRuta value) {
        return new JAXBElement<ListarVuelosPorRuta>(_ListarVuelosPorRuta_QNAME, ListarVuelosPorRuta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarVuelosPorRutaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarVuelosPorRutaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "listarVuelosPorRutaResponse")
    public JAXBElement<ListarVuelosPorRutaResponse> createListarVuelosPorRutaResponse(ListarVuelosPorRutaResponse value) {
        return new JAXBElement<ListarVuelosPorRutaResponse>(_ListarVuelosPorRutaResponse_QNAME, ListarVuelosPorRutaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModificarDatosAerolineaCompleto }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ModificarDatosAerolineaCompleto }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "modificarDatosAerolineaCompleto")
    public JAXBElement<ModificarDatosAerolineaCompleto> createModificarDatosAerolineaCompleto(ModificarDatosAerolineaCompleto value) {
        return new JAXBElement<ModificarDatosAerolineaCompleto>(_ModificarDatosAerolineaCompleto_QNAME, ModificarDatosAerolineaCompleto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModificarDatosAerolineaCompletoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ModificarDatosAerolineaCompletoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "modificarDatosAerolineaCompletoResponse")
    public JAXBElement<ModificarDatosAerolineaCompletoResponse> createModificarDatosAerolineaCompletoResponse(ModificarDatosAerolineaCompletoResponse value) {
        return new JAXBElement<ModificarDatosAerolineaCompletoResponse>(_ModificarDatosAerolineaCompletoResponse_QNAME, ModificarDatosAerolineaCompletoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModificarDatosClienteCompleto }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ModificarDatosClienteCompleto }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "modificarDatosClienteCompleto")
    public JAXBElement<ModificarDatosClienteCompleto> createModificarDatosClienteCompleto(ModificarDatosClienteCompleto value) {
        return new JAXBElement<ModificarDatosClienteCompleto>(_ModificarDatosClienteCompleto_QNAME, ModificarDatosClienteCompleto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModificarDatosClienteCompletoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ModificarDatosClienteCompletoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "modificarDatosClienteCompletoResponse")
    public JAXBElement<ModificarDatosClienteCompletoResponse> createModificarDatosClienteCompletoResponse(ModificarDatosClienteCompletoResponse value) {
        return new JAXBElement<ModificarDatosClienteCompletoResponse>(_ModificarDatosClienteCompletoResponse_QNAME, ModificarDatosClienteCompletoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerAerolinea }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerAerolinea }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerAerolinea")
    public JAXBElement<ObtenerAerolinea> createObtenerAerolinea(ObtenerAerolinea value) {
        return new JAXBElement<ObtenerAerolinea>(_ObtenerAerolinea_QNAME, ObtenerAerolinea.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerAerolineaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerAerolineaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerAerolineaResponse")
    public JAXBElement<ObtenerAerolineaResponse> createObtenerAerolineaResponse(ObtenerAerolineaResponse value) {
        return new JAXBElement<ObtenerAerolineaResponse>(_ObtenerAerolineaResponse_QNAME, ObtenerAerolineaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerCliente }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerCliente }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerCliente")
    public JAXBElement<ObtenerCliente> createObtenerCliente(ObtenerCliente value) {
        return new JAXBElement<ObtenerCliente>(_ObtenerCliente_QNAME, ObtenerCliente.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerClienteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerClienteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerClienteResponse")
    public JAXBElement<ObtenerClienteResponse> createObtenerClienteResponse(ObtenerClienteResponse value) {
        return new JAXBElement<ObtenerClienteResponse>(_ObtenerClienteResponse_QNAME, ObtenerClienteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerDtPaquete }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerDtPaquete }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerDtPaquete")
    public JAXBElement<ObtenerDtPaquete> createObtenerDtPaquete(ObtenerDtPaquete value) {
        return new JAXBElement<ObtenerDtPaquete>(_ObtenerDtPaquete_QNAME, ObtenerDtPaquete.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerDtPaqueteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerDtPaqueteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerDtPaqueteResponse")
    public JAXBElement<ObtenerDtPaqueteResponse> createObtenerDtPaqueteResponse(ObtenerDtPaqueteResponse value) {
        return new JAXBElement<ObtenerDtPaqueteResponse>(_ObtenerDtPaqueteResponse_QNAME, ObtenerDtPaqueteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerReservasConCheckin }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerReservasConCheckin }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerReservasConCheckin")
    public JAXBElement<ObtenerReservasConCheckin> createObtenerReservasConCheckin(ObtenerReservasConCheckin value) {
        return new JAXBElement<ObtenerReservasConCheckin>(_ObtenerReservasConCheckin_QNAME, ObtenerReservasConCheckin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerReservasConCheckinResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerReservasConCheckinResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerReservasConCheckinResponse")
    public JAXBElement<ObtenerReservasConCheckinResponse> createObtenerReservasConCheckinResponse(ObtenerReservasConCheckinResponse value) {
        return new JAXBElement<ObtenerReservasConCheckinResponse>(_ObtenerReservasConCheckinResponse_QNAME, ObtenerReservasConCheckinResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerTopRutasMasVisitadas }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerTopRutasMasVisitadas }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerTopRutasMasVisitadas")
    public JAXBElement<ObtenerTopRutasMasVisitadas> createObtenerTopRutasMasVisitadas(ObtenerTopRutasMasVisitadas value) {
        return new JAXBElement<ObtenerTopRutasMasVisitadas>(_ObtenerTopRutasMasVisitadas_QNAME, ObtenerTopRutasMasVisitadas.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerTopRutasMasVisitadasResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerTopRutasMasVisitadasResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerTopRutasMasVisitadasResponse")
    public JAXBElement<ObtenerTopRutasMasVisitadasResponse> createObtenerTopRutasMasVisitadasResponse(ObtenerTopRutasMasVisitadasResponse value) {
        return new JAXBElement<ObtenerTopRutasMasVisitadasResponse>(_ObtenerTopRutasMasVisitadasResponse_QNAME, ObtenerTopRutasMasVisitadasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerVuelo }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerVuelo }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerVuelo")
    public JAXBElement<ObtenerVuelo> createObtenerVuelo(ObtenerVuelo value) {
        return new JAXBElement<ObtenerVuelo>(_ObtenerVuelo_QNAME, ObtenerVuelo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerVueloResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ObtenerVueloResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "obtenerVueloResponse")
    public JAXBElement<ObtenerVueloResponse> createObtenerVueloResponse(ObtenerVueloResponse value) {
        return new JAXBElement<ObtenerVueloResponse>(_ObtenerVueloResponse_QNAME, ObtenerVueloResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "ping")
    public JAXBElement<Ping> createPing(Ping value) {
        return new JAXBElement<Ping>(_Ping_QNAME, Ping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PingResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "pingResponse")
    public JAXBElement<PingResponse> createPingResponse(PingResponse value) {
        return new JAXBElement<PingResponse>(_PingResponse_QNAME, PingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RealizarCheckinReserva }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RealizarCheckinReserva }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "realizarCheckinReserva")
    public JAXBElement<RealizarCheckinReserva> createRealizarCheckinReserva(RealizarCheckinReserva value) {
        return new JAXBElement<RealizarCheckinReserva>(_RealizarCheckinReserva_QNAME, RealizarCheckinReserva.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RealizarCheckinReservaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RealizarCheckinReservaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "realizarCheckinReservaResponse")
    public JAXBElement<RealizarCheckinReservaResponse> createRealizarCheckinReservaResponse(RealizarCheckinReservaResponse value) {
        return new JAXBElement<RealizarCheckinReservaResponse>(_RealizarCheckinReservaResponse_QNAME, RealizarCheckinReservaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerInfoVueloDt }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerInfoVueloDt }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "verInfoVueloDt")
    public JAXBElement<VerInfoVueloDt> createVerInfoVueloDt(VerInfoVueloDt value) {
        return new JAXBElement<VerInfoVueloDt>(_VerInfoVueloDt_QNAME, VerInfoVueloDt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerInfoVueloDtResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerInfoVueloDtResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "verInfoVueloDtResponse")
    public JAXBElement<VerInfoVueloDtResponse> createVerInfoVueloDtResponse(VerInfoVueloDtResponse value) {
        return new JAXBElement<VerInfoVueloDtResponse>(_VerInfoVueloDtResponse_QNAME, VerInfoVueloDtResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificarLogin }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerificarLogin }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "verificarLogin")
    public JAXBElement<VerificarLogin> createVerificarLogin(VerificarLogin value) {
        return new JAXBElement<VerificarLogin>(_VerificarLogin_QNAME, VerificarLogin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificarLoginResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerificarLoginResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ServiciosWeb/", name = "verificarLoginResponse")
    public JAXBElement<VerificarLoginResponse> createVerificarLoginResponse(VerificarLoginResponse value) {
        return new JAXBElement<VerificarLoginResponse>(_VerificarLoginResponse_QNAME, VerificarLoginResponse.class, null, value);
    }

}
