package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public abstract class Page {
    private static final String USER_SESSION_KEY = "user";
    private static final String MESSAGE_SESSION_KEY = "message";

    private final UserService userService = new UserService();
    private HttpServletRequest request;

    public void before(HttpServletRequest request, Map<String, Object> view) {
        this.request = request;
        putUser(view);
        if (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            putMessage(view);
        }
        view.put("userCount", userService.findCount());
    }

    public void after(HttpServletRequest request, Map<String, Object> view) {
    }

    protected void action(HttpServletRequest request, Map<String, Object> view) {
    }

    protected HttpSession getSession() {
        return request.getSession();
    }

    protected User getUser() {
        return (User) getSession().getAttribute(USER_SESSION_KEY);
    }

    protected void setUser(User user) {
        getSession().setAttribute(USER_SESSION_KEY, user);
    }

    protected void removeUser() {
        getSession().removeAttribute(USER_SESSION_KEY);
    }

    protected void setMessage(String message) {
        getSession().setAttribute(MESSAGE_SESSION_KEY, message);
    }

    protected User requireUser() {
        User user = getUser();
        if (user == null) {
            setMessage("Please enter to access this page");
            throw new RedirectException("/index");
        }
        return user;
    }

    private void putUser(Map<String, Object> view) {
        User user = getUser();
        if (user == null) {
            return;
        }

        User actualUser = userService.find(user.getId());
        if (actualUser == null) {
            removeUser();
        } else {
            setUser(actualUser);
            view.put("user", actualUser);
        }
    }

    private void putMessage(Map<String, Object> view) {
        String message = (String) getSession().getAttribute(MESSAGE_SESSION_KEY);
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            getSession().removeAttribute(MESSAGE_SESSION_KEY);
        }
    }
}
