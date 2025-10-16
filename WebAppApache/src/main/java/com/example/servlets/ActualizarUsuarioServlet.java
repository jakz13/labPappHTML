package com.example.servlets;

import Logica.Fabrica;
import Logica.ISistema;
import Logica.TipoDoc;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.time.LocalDate;
import org.json.*;

@WebServlet("/api/usuario/actualizar")
public class ActualizarUsuarioServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);
            String nickname = (session != null) ? (String) session.getAttribute("usuario") : null;
            String tipoUsuario = (session != null) ? (String) session.getAttribute("tipoUsuario") : null;

            if (nickname == null || tipoUsuario == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\":false, \"error\":\"Usuario no autenticado\"}");
                return;
            }

            // Leer JSON del body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            String body = sb.toString();

            System.out.println("Datos recibidos para actualizar: " + body);
            JSONObject obj = new JSONObject(body);

            ISistema sistema = Fabrica.getInstance().getISistema();
            sistema.cargarDesdeBd();

            boolean actualizado = false;

            if ("cliente".equals(tipoUsuario)) {
                actualizado = actualizarCliente(sistema, nickname, obj);
            } else if ("aerolinea".equals(tipoUsuario)) {
                actualizado = actualizarAerolinea(sistema, nickname, obj);
            }

            if (actualizado) {
                out.print("{\"success\":true, \"message\":\"Datos actualizados correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\":false, \"error\":\"No se pudieron actualizar los datos\"}");
            }

        } catch (Exception e) {
            System.err.println("Error actualizando usuario: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false, \"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    private boolean actualizarCliente(ISistema sistema, String nickname, JSONObject datos) {
        try {
            // Obtener el cliente actual para tener el email
            var cliente = sistema.obtenerCliente(nickname);
            if (cliente == null) {
                System.err.println("Cliente no encontrado: " + nickname);
                return false;
            }

            // Obtener datos del formulario
            String nombre = datos.getString("nombre");
            String apellido = datos.getString("apellido");
            String nacionalidad = datos.getString("nacionalidad");
            String tipoDocumentoStr = datos.getString("tipoDocumento");
            String numeroDocumento = datos.getString("numeroDocumento");
            String imagenUrl = datos.getString("imagenUrl"); // Por ahora no se usa

            // Convertir tipo de documento
            TipoDoc tipoDoc = convertirTipoDocumento(tipoDocumentoStr);
            if (tipoDoc == null) {
                System.err.println("Tipo de documento no válido: " + tipoDocumentoStr);
                return false;
            }

            // Obtener fecha de nacimiento
            LocalDate fechaNacimiento = null;
            if (datos.has("fechaNacimiento") && !datos.getString("fechaNacimiento").isEmpty()) {
                fechaNacimiento = LocalDate.parse(datos.getString("fechaNacimiento"));
            }

            System.out.println("Modificando cliente - Nombre: " + nombre + ", Apellido: " + apellido +
                    ", Nacionalidad: " + nacionalidad + ", TipoDoc: " + tipoDoc);

            // Cambiar contraseña si se proporcionó
            String nuevaPassword = null;
            if (datos.has("password") && !datos.getString("password").isEmpty()) {
                nuevaPassword = datos.getString("password");
                System.out.println("Actualizando contraseña para: " + cliente.getEmail());
            }
            // Llamar a la función del sistema (igual que en Swing)
            sistema.modificarDatosClienteCompleto(nickname, nombre, apellido, nacionalidad,
                    fechaNacimiento, tipoDoc, numeroDocumento, nuevaPassword, imagenUrl);



            return true;

        } catch (Exception e) {
            System.err.println("Error actualizando cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean actualizarAerolinea(ISistema sistema, String nickname, JSONObject datos) {
        try {
            // Obtener la aerolínea actual para tener el email
            var aerolinea = sistema.obtenerAerolinea(nickname);
            if (aerolinea == null) {
                System.err.println("Aerolínea no encontrada: " + nickname);
                return false;
            }

            // Obtener datos del formulario
            String nombre = datos.getString("nombre");
            String descripcion = datos.getString("descripcion");
            String sitioWeb = datos.getString("sitioWeb");

            System.out.println("Modificando aerolínea - Nombre: " + nombre +
                    ", Descripción: " + descripcion + ", SitioWeb: " + sitioWeb);

            String nuevaPassword = null;
            // Cambiar contraseña si se proporcionó
            if (datos.has("password") && !datos.getString("password").isEmpty()) {
                nuevaPassword = datos.getString("password");
                System.out.println("Actualizando contraseña para: " + aerolinea.getEmail());
            }


            // Llamar a la función del sistema (igual que en Swing)
            sistema.modificarDatosAerolineaCompleto(nickname,nombre,descripcion, sitioWeb,nuevaPassword,
                    datos.getString("imagenUrl"));


            return true;

        } catch (Exception e) {
            System.err.println("Error actualizando aerolínea: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private TipoDoc convertirTipoDocumento(String tipoDocStr) {
        if (tipoDocStr == null) return null;

        switch (tipoDocStr.toLowerCase()) {
            case "ci":
                return TipoDoc.CI;
            case "pasaporte":
                return TipoDoc.PASAPORTE;
            default:
                return null;
        }
    }
}