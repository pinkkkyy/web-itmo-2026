package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.TalkRepository;
import ru.itmo.wp.model.repository.UserRepository;
import ru.itmo.wp.model.repository.impl.TalkRepositoryImpl;
import ru.itmo.wp.model.repository.impl.UserRepositoryImpl;

import java.util.List;

public class TalkService {
    private static final int MAX_TEXT_LENGTH = 10000;

    private final TalkRepository talkRepository = new TalkRepositoryImpl();
    private final UserRepository userRepository = new UserRepositoryImpl();

    public Talk validateTalk(User sourceUser, String targetUserId, String text) throws ValidationException {
        if (sourceUser == null) {
            throw new ValidationException("You must be logged in");
        }

        long targetId = ValidationUtils.parseId(targetUserId, "Recipient");
        if (userRepository.find(targetId) == null) {
            throw new ValidationException("No such recipient");
        }
        if (targetId == sourceUser.getId()) {
            throw new ValidationException("You can't send a message to yourself");
        }

        Talk talk = new Talk();
        talk.setSourceUserId(sourceUser.getId());
        talk.setTargetUserId(targetId);
        talk.setText(ValidationUtils.validateText(text, "Text", MAX_TEXT_LENGTH));
        return talk;
    }

    public void save(Talk talk) {
        talkRepository.save(talk);
    }

    public List<Talk> findAllByUserId(long userId) {
        return talkRepository.findAllByUserId(userId);
    }
}
