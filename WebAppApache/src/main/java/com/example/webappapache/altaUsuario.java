@WebServlet("/altaUsuario")
public class AltaUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Obtener parámetros del formulario
            String nickname = request.getParameter("nickname");
            String nombre = request.getParameter("nombre");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String tipoUsuario = request.getParameter("tipoUsuario");
            String imagen = request.getParameter("imagen"); // Base64 o URL

            // Obtener instancia de la fachada
            IFachada fachada = Fachada.getInstancia();

            if ("cliente".equals(tipoUsuario)) {
                // Registrar cliente
                String apellido = request.getParameter("apellido");
                String fechaNacStr = request.getParameter("fechaNacimiento");
                String nacionalidad = request.getParameter("nacionalidad");
                String tipoDocStr = request.getParameter("tipoDocumento");
                String numDoc = request.getParameter("numeroDocumento");

                // Convertir fecha y tipo de documento
                LocalDate fechaNac = LocalDate.parse(fechaNacStr);
                TipoDoc tipoDoc = TipoDoc.valueOf(tipoDocStr.toUpperCase());

                // Llamar a tu método existente
                fachada.altaCliente(nickname, nombre, apellido, email, fechaNac,
                        nacionalidad, tipoDoc, numDoc);

            } else if ("aerolinea".equals(tipoUsuario)) {
                // Registrar aerolínea
                String descripcion = request.getParameter("descripcionAerolinea");
                String sitioWeb = request.getParameter("sitioWeb");

                // Necesitarías implementar este método en tu fachada
                fachada.altaAerolinea(nickname, nombre, email, descripcion, sitioWeb);
            }

            // Éxito
            out.print("{\"success\": true, \"message\": \"Usuario registrado exitosamente\"}");

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"error\": \"Error interno del servidor\"}");
        }
    }
}