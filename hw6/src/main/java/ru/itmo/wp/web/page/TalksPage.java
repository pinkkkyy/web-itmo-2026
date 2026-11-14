package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.TalkService;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
public class TalksPage extends Page {
    private final TalkService talkService = new TalkService();
    private final UserService userService = new UserService();

    @Override
    public void before(HttpServletRequest request, Map<String, Object> view) {
        super.before(request, view);
        requireUser();
    }

    @Override
    public void after(HttpServletRequest request, Map<String, Object> view) {
        User user = getUser();
        view.put("recipients", userService.findAll().stream()
                .filter(u -> u.getId() != user.getId())
                .collect(Collectors.toList()));
        view.put("talks", talkService.findAllByUserId(user.getId()));
    }

    private void send(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        Talk talk = talkService.validateTalk(getUser(), request.getParameter("targetUserId"), request.getParameter("text"));
        talkService.save(talk);
        setMessage("Message sent");
        throw new RedirectException("/talks");
    }
}
