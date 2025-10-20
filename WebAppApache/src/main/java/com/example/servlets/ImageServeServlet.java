package com.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/Images/*")
public class ImageServeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String imagesDir = (String) req.getServletContext().getAttribute("IMAGES_DIR");
        if (imagesDir == null) {
            // try default real path
            imagesDir = req.getServletContext().getRealPath("/Images");
        }
        if (imagesDir == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String requested = req.getPathInfo(); // e.g. /filename.jpg
        if (requested == null || requested.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Normalize and prevent path traversal
        Path filePath = Paths.get(imagesDir).resolve(requested.substring(1)).normalize();
        if (!filePath.startsWith(Paths.get(imagesDir).normalize())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Set content type
        String mime = URLConnection.guessContentTypeFromName(file.getName());
        if (mime == null) {
            try {
                mime = Files.probeContentType(filePath);
            } catch (Exception ignored) {}
        }
        if (mime == null) mime = "application/octet-stream";
        resp.setContentType(mime);
        resp.setContentLengthLong(file.length());
        resp.setHeader("Cache-Control", "public, max-age=86400");

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream())) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }
}

