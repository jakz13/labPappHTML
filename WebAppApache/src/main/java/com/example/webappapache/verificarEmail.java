@WebServlet("/verificarEmail")
public class VerificarEmailServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            IFachada fachada = Fachada.getInstancia();
            // Necesitarías implementar este método en tu fachada
            boolean disponible = fachada.verificarEmailDisponible(email);

            response.getWriter().print("{\"disponible\": " + disponible + "}");

        } catch (Exception e) {
            response.getWriter().print("{\"disponible\": false}");
        }
    }
}