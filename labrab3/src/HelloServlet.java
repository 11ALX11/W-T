import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Enumeration;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

@WebServlet("/HelloServlet")
public class HelloServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String imagePath = "\"C:\\Users\\marin\\Downloads\\apache-tomcat-10.1.7-windows-x64\\apache-tomcat-10.1.7\\webapps\\ROOT\\test.jpg\"";
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            String contentType = Files.probeContentType(imageFile.toPath());
            response.setContentType(contentType);
            response.setContentLength((int) imageFile.length());
            Files.copy(imageFile.toPath(), response.getOutputStream());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Заголовки (POST):</h1>");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            out.println(headerName + ": " + headerValue + "<br>");
        }
        out.println("<h1>Переданные переменные:</h1>");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            out.println(paramName + "=" + paramValue + "<br>");
        }
        out.println("</body></html>");
    }
}
