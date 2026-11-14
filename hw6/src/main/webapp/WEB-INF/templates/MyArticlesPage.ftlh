<#-- @ftlvariable name="articles" type="ru.itmo.wp.model.domain.Article[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="my-articles datatable">
        <div class="caption">My articles</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Visibility</th>
            </tr>
            </thead>
            <tbody>
            <#list articles as article>
                <tr>
                    <td>${article.id}</td>
                    <td>${article.title}</td>
                    <td>
                        <button class="toggle-hidden" data-article-id="${article.id?c}" data-hidden="${article.hidden?c}">
                            ${article.hidden?then("Show", "Hide")}
                        </button>
                    </td>
                </tr>
            <#else>
                <tr class="noData">
                    <td colspan="3">No data</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
    <script>
        $(function () {
            $(".my-articles .toggle-hidden").click(function () {
                const $button = $(this);
                const hidden = $button.attr("data-hidden") !== "true";
                ajax({
                    url: "/myArticles",
                    data: {action: "changeHidden", articleId: $button.attr("data-article-id"), hidden: hidden},
                    success: function (response) {
                        if (response.error) {
                            notify(response.error, true);
                            return;
                        }
                        $button.attr("data-hidden", String(hidden));
                        $button.text(hidden ? "Show" : "Hide");
                    }
                });
            });
        });
    </script>
</@c.page>
