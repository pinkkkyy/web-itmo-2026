package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageServlet extends HttpServlet {
    private static final Gson GSON = new Gson();

    private final Object lock = new Object();
    private final List<Message> messages = new ArrayList<>();
    private long nextId = 1;

    private static class Message {
        private final transient long id;
        private final String user;
        private final String text;

        Message(long id, String user, String text) {
            this.id = id;
            this.user = user;
            this.text = text;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getPathInfo();
        HttpSession session = request.getSession();
        Object result;

        if ("/auth".equals(action)) {
            String user = request.getParameter("user");
            if (user != null && !user.trim().isEmpty()) {
                session.setAttribute("user", user.trim());
            }
            Object current = session.getAttribute("user");
            result = current == null ? "" : current;
        } else if ("/findAll".equals(action)) {
            List<Message> copy;
            synchronized (lock) {
                copy = new ArrayList<>(messages);
            }
            Collections.sort(copy, Comparator.comparingLong(m -> m.id));
            result = copy;
        } else if ("/add".equals(action)) {
            String user = (String) session.getAttribute("user");
            String text = request.getParameter("text");
            if (user == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            if (text != null && !text.trim().isEmpty()) {
                synchronized (lock) {
                    messages.add(new Message(nextId++, user, text.trim()));
                }
            }
            result = Collections.emptyMap();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] bytes = GSON.toJson(result).getBytes(StandardCharsets.UTF_8);
        response.setContentType("application/json");
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }
}
