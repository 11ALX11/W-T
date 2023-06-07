
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.net.URLEncoder;
import java.net.URLDecoder;

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
			String lastVisitDate = URLDecoder.decode(uniqueVisitorCookie.getValue(), "UTF-8");
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
				out.println("<p>Ошибка логирования: " + e.getMessage() + "</p>");
			}
            try {
				// Создание куки
				String encodedValue = URLEncoder.encode(currentDate, "UTF-8");
				Cookie newCookie = new Cookie("uniqueVisitor", encodedValue);
				newCookie.setMaxAge(24 * 60 * 60); // Установка срока жизни куки на 24 часа
				response.addCookie(newCookie);
			}
			catch (Exception e) {
				out.println("<p>Ошибка создания куки: " + e.getMessage() + "</p>");
			}
        }

        // Вывод атрибутов ServletContext, HttpSession и HttpServletRequest
        out.println("<h2>Атрибуты:</h2>");
		
		try {
			// Получение атрибутов ServletContext
			ServletContext servletContext = getServletContext();
			Enumeration<String> servletContextAttributes = servletContext.getAttributeNames();
			while (servletContextAttributes.hasMoreElements()) {
				String attributeName = servletContextAttributes.nextElement();
				Object attributeValue = servletContext.getAttribute(attributeName);
				out.println("<b>ServletContext attribute</b>: " + attributeName + " = " + attributeValue + "<br><br>");
			}
	
			// Получение атрибутов HttpSession
			HttpSession session = request.getSession();
			Enumeration<String> sessionAttributes = session.getAttributeNames();
			while (sessionAttributes.hasMoreElements()) {
				String attributeName = sessionAttributes.nextElement();
				Object attributeValue = session.getAttribute(attributeName);
				out.println("<b>HttpSession attribute</b>: " + attributeName + " = " + attributeValue + "<br><br>");
			}
	
			// Получение атрибутов HttpServletRequest
			Enumeration<String> requestAttributes = request.getAttributeNames();
			while (requestAttributes.hasMoreElements()) {
				String attributeName = requestAttributes.nextElement();
				Object attributeValue = request.getAttribute(attributeName);
				out.println("<b>HttpServletRequest attribute</b>: " + attributeName + " = " + attributeValue + "<br><br>");
			}
		}
		catch (Exception e) {
			out.println("<p>Ошибка во время работы с атрибутами</p>");
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
        FileWriter writer = new FileWriter("C:\\Users\\marin\\Downloads\\apache-tomcat-10.1.7-windows-x64\\apache-tomcat-10.1.7\\webapps\\ROOT\\log.txt", true);
        writer.write(logEntry + "\n");
        writer.close();
    }
}