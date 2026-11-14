package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused"})
public class ArticlePage extends Page {
    private final ArticleService articleService = new ArticleService();

    @Override
    public void before(HttpServletRequest request, Map<String, Object> view) {
        super.before(request, view);
        requireUser();
    }

    @Json
    private void create(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        Article article = articleService.validateArticle(getUser(), request.getParameter("title"), request.getParameter("text"));
        articleService.create(article);
        setMessage("Article is created");
        throw new RedirectException("/index");
    }
}
