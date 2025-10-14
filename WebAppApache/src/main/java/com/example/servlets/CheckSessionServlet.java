package com.example.servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/api/check-session")
public class CheckSessionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        boolean authenticated = (session != null && session.getAttribute("usuario") != null);
        String nickname = authenticated ? (String) session.getAttribute("usuario") : "";
        response.getWriter().print("{\"authenticated\":" + authenticated + ", \"nickname\":\"" + nickname + "\"}");
    }
}
