@WebServlet("/altaRuta")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 2, // 2MB
        maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class AltaRutaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Obtener parámetros del formulario
            String nombre = request.getParameter("nombreRuta");
            String descripcionCorta = request.getParameter("descripcionCorta");
            String descripcion = request.getParameter("descripcion");
            String nombreAerolinea = request.getParameter("aerolinea"); // ← Este viene de la sesión
            String ciudadOrigen = request.getParameter("origen");
            String ciudadDestino = request.getParameter("destino");
            String hora = request.getParameter("hora");
            double costoTurista = Double.parseDouble(request.getParameter("costoTurista"));
            double costoEjecutivo = Double.parseDouble(request.getParameter("costoEjecutivo"));
            double costoEquipaje = Double.parseDouble(request.getParameter("costoEquipaje"));
            String[] categorias = request.getParameterValues("categorias");

            // Procesar imagen
            String imagen = null;
            Part filePart = request.getPart("imagenRuta");
            if (filePart != null && filePart.getSize() > 0) {
                imagen = guardarImagen(filePart);
            }

            // Obtener instancia de la fachada
            IFachada fachada = Fachada.getInstancia();

            // Llamar al método modificado
            fachada.altaRutaVuelo(nombre, descripcionCorta, descripcion, nombreAerolinea,
                    ciudadOrigen, ciudadDestino, hora, costoTurista,
                    costoEjecutivo, costoEquipaje, categorias, imagen);

            // Éxito
            out.print("{\"success\": true, \"message\": \"Ruta de vuelo creada exitosamente. Estado: Ingresada - Esperando confirmación del administrador.\"}");

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"error\": \"Error interno del servidor: " + e.getMessage() + "\"}");
        }
    }

    private String guardarImagen(Part filePart) throws IOException {
        // Implementar lógica para guardar la imagen
        String fileName = System.currentTimeMillis() + "_" + extractFileName(filePart);
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        String filePath = uploadPath + File.separator + fileName;
        try (InputStream input = filePart.getInputStream();
             OutputStream output = new FileOutputStream(filePath)) {
            input.transferTo(output);
        }

        return "uploads/" + fileName;
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }
}