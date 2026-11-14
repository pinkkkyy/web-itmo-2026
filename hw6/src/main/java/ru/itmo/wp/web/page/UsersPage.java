package ru.itmo.wp.web.page;

import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.annotation.Json;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused"})
public class UsersPage extends Page {
    private final UserService userService = new UserService();

    @Override
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        view.put("users", userService.findAll());
    }

    @Json
    private void changeAdmin(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        userService.changeAdmin(getUser(), request.getParameter("userId"), request.getParameter("admin"));
        view.put("admin", Boolean.parseBoolean(request.getParameter("admin")));
    }
}
