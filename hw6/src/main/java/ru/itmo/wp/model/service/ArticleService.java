package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;

import java.util.List;

public class ArticleService {
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_TEXT_LENGTH = 10000;

    private final ArticleRepository articleRepository = new ArticleRepositoryImpl();

    public Article validateArticle(User user, String title, String text) throws ValidationException {
        if (user == null) {
            throw new ValidationException("You must be logged in");
        }

        Article article = new Article();
        article.setUserId(user.getId());
        article.setTitle(ValidationUtils.validateText(title, "Title", MAX_TITLE_LENGTH));
        article.setText(ValidationUtils.validateText(text, "Text", MAX_TEXT_LENGTH));
        return article;
    }

    public void create(Article article) {
        articleRepository.save(article);
    }

    public List<Article> findAllVisible() {
        return articleRepository.findAllVisible();
    }

    public List<Article> findAllByUserId(long userId) {
        return articleRepository.findAllByUserId(userId);
    }

    public void changeHidden(User user, String articleId, String hidden) throws ValidationException {
        if (user == null) {
            throw new ValidationException("You must be logged in");
        }

        long id = ValidationUtils.parseId(articleId, "Article");
        boolean value = ValidationUtils.parseFlag(hidden, "hidden");
        Article article = articleRepository.find(id);
        if (article == null) {
            throw new ValidationException("No such article");
        }
        if (article.getUserId() != user.getId()) {
            throw new ValidationException("You can change only your own articles");
        }

        articleRepository.updateHidden(id, value);
    }
}
