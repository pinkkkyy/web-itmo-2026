<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->
<#import "commons.ftlh" as c>

<#assign isAdmin = (user?? && user.admin)>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Created</th>
                <#if isAdmin>
                    <th>Admin</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <#list users as u>
                <tr>
                    <td>${u.id}</td>
                    <td>${u.login}</td>
                    <td>${u.creationTime?datetime}</td>
                    <#if isAdmin>
                        <td>
                            <span class="admin-value">${u.admin?c}</span>
                            <button class="toggle-admin" data-user-id="${u.id?c}" data-admin="${u.admin?c}">
                                ${u.admin?then("Disable", "Enable")}
                            </button>
                        </td>
                    </#if>
                </tr>
            <#else>
                <tr class="noData">
                    <td colspan="${isAdmin?then(4, 3)}">No data</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
    <#if isAdmin>
        <script>
            $(function () {
                $(".users .toggle-admin").click(function () {
                    const $button = $(this);
                    const admin = $button.attr("data-admin") !== "true";
                    ajax({
                        url: "/users",
                        data: {action: "changeAdmin", userId: $button.attr("data-user-id"), admin: admin},
                        success: function (response) {
                            if (response.error) {
                                notify(response.error, true);
                                return;
                            }
                            $button.attr("data-admin", String(admin));
                            $button.text(admin ? "Disable" : "Enable");
                            $button.siblings(".admin-value").text(String(admin));
                        }
                    });
                });
            });
        </script>
    </#if>
</@c.page>
