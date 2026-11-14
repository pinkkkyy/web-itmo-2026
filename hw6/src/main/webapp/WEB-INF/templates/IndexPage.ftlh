<#import "commons.ftlh" as c>

<@c.page>
    <template id="article-template">
        <article>
            <div class="title"></div>
            <div class="information">By <span class="author"></span>, <span class="time"></span></div>
            <div class="body"></div>
            <div class="footer">
                <div class="left">
                    <img src="/img/voteup.png" title="Vote Up" alt="Vote Up"/>
                    <span class="positive-score">0</span>
                    <img src="/img/votedown.png" title="Vote Down" alt="Vote Down"/>
                </div>
                <div class="right">
                    <img src="/img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                    <span class="time"></span>
                    <img src="/img/comments_16x16.png" title="Comments" alt="Comments"/>
                    <a href="#">0</a>
                </div>
            </div>
        </article>
    </template>

    <div class="articles">
        <div class="no-data">Loading...</div>
    </div>

    <script>
        $(function () {
            const $articles = $(".articles");
            const template = document.getElementById("article-template");

            ajax({
                url: "/index",
                data: {action: "findArticles"},
                success: function (response) {
                    $articles.empty();
                    if (response.articles.length === 0) {
                        $articles.append($("<div class='no-data'></div>").text("No articles yet"));
                        return;
                    }
                    response.articles.forEach(function (article) {
                        const $article = $(template.content.cloneNode(true));
                        $article.find(".title").text(article.title);
                        $article.find(".author").text(article.userLogin);
                        $article.find(".time").text(article.creationTime);
                        $article.find(".body").text(article.text);
                        $articles.append($article);
                    });
                }
            });
        });
    </script>
</@c.page>
