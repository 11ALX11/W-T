
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

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
            try {
				writeToFile(logEntry);
			}
			catch (IOException e) {
				out.println("<p>Ошибка логирования</p>");
			}
            // Создание куки
            Cookie newCookie = new Cookie("uniqueVisitor", currentDate);
            newCookie.setMaxAge(24 * 60 * 60); // Установка срока жизни куки на 24 часа
            response.addCookie(newCookie);
        }

            // Вывод атрибутов ServletContext, HttpSession и HttpServletRequest
            out.println("<h2>Атрибуты:</h2>");

            // Получение атрибутов ServletContext
            ServletContext servletContext = getServletContext();
            Enumeration<String> servletContextAttributes = servletContext.getAttributeNames();
            while (servletContextAttributes.hasMoreElements()) {
                String attributeName = servletContextAttributes.nextElement();
                Object attributeValue = servletContext.getAttribute(attributeName);
                out.println("ServletContext attribute: " + attributeName + " = " + attributeValue + "<br>");
            }

            // Получение атрибутов HttpSession
            HttpSession session = request.getSession();
            Enumeration<String> sessionAttributes = session.getAttributeNames();
            while (sessionAttributes.hasMoreElements()) {
                String attributeName = sessionAttributes.nextElement();
                Object attributeValue = session.getAttribute(attributeName);
                out.println("HttpSession attribute: " + attributeName + " = " + attributeValue + "<br>");
            }

            // Получение атрибутов HttpServletRequest
            Enumeration<String> requestAttributes = request.getAttributeNames();
            while (requestAttributes.hasMoreElements()) {
                String attributeName = requestAttributes.nextElement();
                Object attributeValue = request.getAttribute(attributeName);
                out.println("HttpServletRequest attribute: " + attributeName + " = " + attributeValue + "<br>");
            }


        out.println("</body></html>");
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

    private void writeToFile(String logEntry) throws IOException {
        FileWriter writer = new FileWriter("/log.txt", true);
        writer.write(logEntry + "\n");
        writer.close();
    }
}