package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

public class CaptchaFilter implements Filter {
    private static final String PASSED = "captchaPassed";
    private static final String EXPECTED = "captchaExpected";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();

        if (Boolean.TRUE.equals(session.getAttribute(PASSED))) {
            chain.doFilter(request, response);
            return;
        }

        if ("POST".equals(request.getMethod()) && request.getParameter("captcha") != null) {
            Object expected = session.getAttribute(EXPECTED);
            String answer = request.getParameter("captcha").trim();
            if (expected != null && expected.equals(answer)) {
                session.setAttribute(PASSED, true);
                session.removeAttribute(EXPECTED);
                String query = request.getQueryString();
                response.sendRedirect(request.getRequestURI() + (query == null ? "" : "?" + query));
            } else {
                showCaptcha(request, response);
            }
            return;
        }

        if ("GET".equals(request.getMethod())) {
            showCaptcha(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void showCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100, 1000)); // 100..999
        request.getSession().setAttribute(EXPECTED, code);

        String image = Base64.getEncoder().encodeToString(ImageUtils.toPng(code));

        String html = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">"
                + "<title>Captcha</title></head><body>"
                + "<form method=\"post\">"
                + "<img src=\"data:image/png;base64," + image + "\" alt=\"captcha\"><br>"
                + "<label for=\"captcha\">Enter the code:</label> "
                + "<input name=\"captcha\" id=\"captcha\" autocomplete=\"off\" autofocus> "
                + "<button type=\"submit\">Submit</button>"
                + "</form></body></html>";

        response.setHeader("Cache-Control", "no-store");
        response.setContentType("text/html;charset=UTF-8");
        response.getOutputStream().write(html.getBytes(StandardCharsets.UTF_8));
        response.getOutputStream().flush();
    }
}