import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Enumeration;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Получение массива всех куков из запроса
        Cookie[] cookies = request.getCookies();

        // Поиск куки с именем "uniqueVisitor"
        Cookie uniqueVisitorCookie = getCookieByName(cookies, "uniqueVisitor");

        if (uniqueVisitorCookie != null) {
            // Кука уже существует, пользователь не уникальный
            String lastVisitDate = uniqueVisitorCookie.getValue();
            String userAgent = request.getHeader("User-Agent");

            out.println("<html><body>");
            out.println("<h2>Добро пожаловать снова!</h2>");
            out.println("Дата последнего посещения: " + lastVisitDate + "<br>");
            out.println("Браузер: " + userAgent);
            out.println("</body></html>");
        } else {
            // Кука не существует, пользователь уникальный
            String currentDate = new Date().toString();
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            out.println("<html><body>");
            out.println("<h2>Добро пожаловать!</h2>");
            out.println("Это ваш первый визит на сайт.<br>");
            out.println("Записываем данные в файл и создаем куку.");

            // Запись данных в текстовый файл
            String logEntry = "Дата посещения: " + currentDate + ", IP адрес: " + ipAddress + ", Браузер: " + userAgent;
            writeToFile(logEntry);

            // Создание куки
            Cookie newCookie = new Cookie("uniqueVisitor", currentDate);
            newCookie.setMaxAge(24 * 60 * 60); // Установка срока жизни куки на 24 часа
            response.addCookie(newCookie);

            out.println("</body></html>");

            // Вывод атрибутов ServletContext, HttpSession и HttpServletRequest
            out.println("<h2>Атрибуты:</h2>");

            // Получение атрибутов ServletContext
            javax.servlet.ServletContext servletContext = getServletContext();
            java.util.Enumeration<String> servletContextAttributes = servletContext.getAttributeNames();
            while (servletContextAttributes.hasMoreElements()) {
                String attributeName = servletContextAttributes.nextElement();
                Object attributeValue = servletContext.getAttribute(attributeName);
                out.println("ServletContext attribute: " + attributeName + " = " + attributeValue + "<br>");
            }

            // Получение атрибутов HttpSession
            javax.servlet.http.HttpSession session = request.getSession();
            java.util.Enumeration<String> sessionAttributes = session.getAttributeNames();
            while (sessionAttributes.hasMoreElements()) {
                String attributeName = sessionAttributes.nextElement();
                Object attributeValue = session.getAttribute(attributeName);
                out.println("HttpSession attribute: " + attributeName + " = " + attributeValue + "<br>");
            }

            // Получение атрибутов HttpServletRequest
            java.util.Enumeration<String> requestAttributes = request.getAttributeNames();
            while (requestAttributes.hasMoreElements()) {
                String attributeName = requestAttributes.nextElement();
                Object attributeValue = request.getAttribute(attributeName);
                out.println("HttpServletRequest attribute: " + attributeName + " = " + attributeValue + "<br>");
            }
        }
    }

    private Cookie getCookieByName(Cookie[] cookies, String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private void writeToFile(String logEntry) {
        FileWriter writer = new FileWriter("log.txt", true);
        writer.write(logEntry + "\n");
        writer.close();
    }
}