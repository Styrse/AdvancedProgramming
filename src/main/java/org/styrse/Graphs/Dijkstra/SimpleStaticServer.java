package org.styrse.Graphs.Dijkstra;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SimpleStaticServer {
  private static final Map<String, String> CONTENT_TYPES = Map.ofEntries(
      Map.entry(".html", "text/html; charset=utf-8"),
      Map.entry(".css", "text/css; charset=utf-8"),
      Map.entry(".js", "text/javascript; charset=utf-8"),
      Map.entry(".svg", "image/svg+xml"),
      Map.entry(".png", "image/png"),
      Map.entry(".jpg", "image/jpeg"),
      Map.entry(".jpeg", "image/jpeg"),
      Map.entry(".gif", "image/gif"),
      Map.entry(".txt", "text/plain; charset=utf-8")
  );

  public static HttpServer start(int port, String classpathRoot) throws IOException {
    if (!classpathRoot.startsWith("/")) classpathRoot = "/" + classpathRoot;
    if (classpathRoot.endsWith("/")) classpathRoot = classpathRoot.substring(0, classpathRoot.length() - 1);

    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);

    String root = classpathRoot;
    server.createContext("/", exchange -> {
      String path = exchange.getRequestURI().getPath();
      if (path == null || path.isBlank() || path.equals("/")) path = "/index.html";

      if (path.contains("..")) {
        writeText(exchange, 400, "Bad request");
        return;
      }

      String resourcePath = root + path;
      try (InputStream in = SimpleStaticServer.class.getResourceAsStream(resourcePath)) {
        if (in == null) {
          writeText(exchange, 404, "Not found: " + path);
          return;
        }
        byte[] bytes = in.readAllBytes();
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentTypeFor(path));
        headers.set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
          os.write(bytes);
        }
      } catch (Exception e) {
        writeText(exchange, 500, "Server error: " + e.getMessage());
      } finally {
        exchange.close();
      }
    });

    server.start();
    return server;
  }

  private static String contentTypeFor(String path) {
    String lower = path.toLowerCase();
    for (Map.Entry<String, String> e : CONTENT_TYPES.entrySet()) {
      if (lower.endsWith(e.getKey())) return e.getValue();
    }
    return "application/octet-stream";
  }

  private static void writeText(HttpExchange exchange, int code, String text) throws IOException {
    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
    exchange.sendResponseHeaders(code, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }
}

