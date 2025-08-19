package com.starwars.shared.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;

@Hidden
@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String getHomePage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"es\">");
        html.append("<head>");
        html.append("    <meta charset=\"UTF-8\">");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("    <title>Star Wars API - Challenge Técnico Conexa</title>");
        html.append("    <style>");
        html.append("        * {");
        html.append("            margin: 0;");
        html.append("            padding: 0;");
        html.append("            box-sizing: border-box;");
        html.append("        }");
        html.append("        ");
        html.append("        body {");
        html.append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("            background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 50%, #16213e 100%);");
        html.append("            color: #ffffff;");
        html.append("            line-height: 1.6;");
        html.append("            min-height: 100vh;");
        html.append("        }");
        html.append("        ");
        html.append("        .container {");
        html.append("            max-width: 1200px;");
        html.append("            margin: 0 auto;");
        html.append("            padding: 2rem;");
        html.append("        }");
        html.append("        ");
        html.append("        .header {");
        html.append("            text-align: center;");
        html.append("            margin-bottom: 3rem;");
        html.append("            padding: 2rem 0;");
        html.append("            border-bottom: 2px solid #ffd700;");
        html.append("        }");
        html.append("        ");
        html.append("        .header h1 {");
        html.append("            font-size: 3rem;");
        html.append("            margin-bottom: 1rem;");
        html.append("            color: #ffd700;");
        html.append("            text-shadow: 2px 2px 4px rgba(0,0,0,0.5);");
        html.append("        }");
        html.append("        ");
        html.append("        .header p {");
        html.append("            font-size: 1.2rem;");
        html.append("            color: #b8b8b8;");
        html.append("            max-width: 600px;");
        html.append("            margin: 0 auto;");
        html.append("        }");
        html.append("        ");
        html.append("        .content {");
        html.append("            display: grid;");
        html.append("            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));");
        html.append("            gap: 2rem;");
        html.append("            margin-bottom: 3rem;");
        html.append("        }");
        html.append("        ");
        html.append("        .card {");
        html.append("            background: rgba(255, 255, 255, 0.1);");
        html.append("            border-radius: 15px;");
        html.append("            padding: 2rem;");
        html.append("            border: 1px solid rgba(255, 215, 0, 0.3);");
        html.append("            backdrop-filter: blur(10px);");
        html.append("            transition: transform 0.3s ease, box-shadow 0.3s ease;");
        html.append("        }");
        html.append("        ");
        html.append("        .card:hover {");
        html.append("            transform: translateY(-5px);");
        html.append("            box-shadow: 0 10px 30px rgba(255, 215, 0, 0.2);");
        html.append("        }");
        html.append("        ");
        html.append("        .card h3 {");
        html.append("            color: #ffd700;");
        html.append("            margin-bottom: 1rem;");
        html.append("            font-size: 1.5rem;");
        html.append("        }");
        html.append("        ");
        html.append("        .card p {");
        html.append("            color: #b8b8b8;");
        html.append("            margin-bottom: 1rem;");
        html.append("        }");
        html.append("        ");
        html.append("        .endpoint {");
        html.append("            background: rgba(255, 215, 0, 0.1);");
        html.append("            border: 1px solid #ffd700;");
        html.append("            border-radius: 8px;");
        html.append("            padding: 0.5rem 1rem;");
        html.append("            font-family: 'Courier New', monospace;");
        html.append("            color: #ffd700;");
        html.append("            margin: 0.5rem 0;");
        html.append("            display: inline-block;");
        html.append("        }");
        html.append("        ");
        html.append("        .footer {");
        html.append("            text-align: center;");
        html.append("            padding: 2rem 0;");
        html.append("            border-top: 1px solid rgba(255, 215, 0, 0.3);");
        html.append("            color: #b8b8b8;");
        html.append("        }");
        html.append("        ");
        html.append("        .footer a {");
        html.append("            color: #ffd700;");
        html.append("            text-decoration: none;");
        html.append("        }");
        html.append("        ");
        html.append("        .footer a:hover {");
        html.append("            text-decoration: underline;");
        html.append("        }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        html.append("    <div class=\"container\">");
        html.append("        <div class=\"header\">");
        html.append("            <h1>🌟 Star Wars API</h1>");
        html.append("            <p>Challenge Técnico Conexa - API REST que integra con SWAPI para gestionar personajes, películas, naves espaciales y vehículos del universo Star Wars.</p>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <div class=\"content\">");
        html.append("            <div class=\"card\">");
        html.append("                <h3>🔐 Autenticación</h3>");
        html.append("                <p>Accede a todos los endpoints protegidos con JWT:</p>");
        html.append("                <div class=\"endpoint\">POST /auth/login</div>");
        html.append("                <div class=\"endpoint\">POST /auth/register</div>");
        html.append("                <div class=\"endpoint\">GET /auth/check-username/{username}</div>");
        html.append("            </div>");
        html.append("            ");
        html.append("            <div class=\"card\">");
        html.append("                <h3>🎬 Películas</h3>");
        html.append("                <p>Gestiona las películas de Star Wars:</p>");
        html.append("                <div class=\"endpoint\">GET /films?title={title}&page={page}&limit={limit}</div>");
        html.append("                <div class=\"endpoint\">GET /films/{id}</div>");
        html.append("            </div>");
        html.append("            ");
        html.append("            <div class=\"card\">");
        html.append("                <h3>👥 Personajes</h3>");
        html.append("                <p>Explora los personajes del universo Star Wars:</p>");
        html.append("                <div class=\"endpoint\">GET /people?name={name}&page={page}&limit={limit}</div>");
        html.append("                <div class=\"endpoint\">GET /people/{id}</div>");
        html.append("            </div>");
        html.append("            ");
        html.append("            <div class=\"card\">");
        html.append("                <h3>🚀 Naves Espaciales</h3>");
        html.append("                <p>Descubre las naves espaciales de Star Wars:</p>");
        html.append("                <div class=\"endpoint\">GET /starships?name={name}&page={page}&limit={limit}</div>");
        html.append("                <div class=\"endpoint\">GET /starships/{id}</div>");
        html.append("            </div>");
        html.append("            ");
        html.append("            <div class=\"card\">");
        html.append("                <h3>🚗 Vehículos</h3>");
        html.append("                <p>Explora los vehículos terrestres de Star Wars:</p>");
        html.append("                <div class=\"endpoint\">GET /vehicles?name={name}&page={page}&limit={limit}</div>");
        html.append("                <div class=\"endpoint\">GET /vehicles/{id}</div>");
        html.append("            </div>");
        html.append("            ");
        html.append("            <div class=\"card\">");
        html.append("                <h3>📚 Documentación</h3>");
        html.append("                <p>Accede a la documentación completa de la API:</p>");
        html.append("                <div class=\"endpoint\">GET /swagger-ui/index.html</div>");
        html.append("                <div class=\"endpoint\">GET /api-docs</div>");
        html.append("            </div>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <div class=\"footer\">");
        html.append("            <p>Desarrollado para el <a href=\"https://github.com/maxif96/conexa-starwars-challenge\" target=\"_blank\">Challenge Técnico Conexa</a></p>");
        html.append("            <p>Integración con <a href=\"https://www.swapi.tech\" target=\"_blank\">SWAPI (Star Wars API)</a></p>");
        html.append("        </div>");
        html.append("    </div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }

    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("name", "Star Wars API");
        apiInfo.put("description", "API REST que integra con SWAPI para gestionar datos de Star Wars");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("author", "Challenge Técnico Conexa");
        apiInfo.put("endpoints", new String[]{
            "GET / - Página de inicio",
            "GET /api - Información de la API",
            "POST /auth/login - Autenticación",
            "POST /auth/register - Registro",
            "GET /auth/check-username/{username} - Verificar disponibilidad de username",
            "GET /films - Listar/buscar películas",
            "GET /films/{id} - Obtener película por ID",
            "GET /people - Listar/buscar personajes",
            "GET /people/{id} - Obtener personaje por ID",
            "GET /starships - Listar/buscar naves espaciales",
            "GET /starships/{id} - Obtener nave espacial por ID",
            "GET /vehicles - Listar/buscar vehículos",
            "GET /vehicles/{id} - Obtener vehículo por ID",
            "GET /swagger-ui/index.html - Documentación Swagger",
            "GET /api-docs - Especificación OpenAPI"
        });
        apiInfo.put("documentation", "/swagger-ui/index.html");
        apiInfo.put("swapi_integration", "https://www.swapi.tech");
        return ResponseEntity.ok(apiInfo);
    }
}
