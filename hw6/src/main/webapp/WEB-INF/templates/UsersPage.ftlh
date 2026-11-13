<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Created</th>
            </tr>
            </thead>
            <tbody>
            <#list users as u>
                <tr>
                    <td>${u.id}</td>
                    <td>${u.login}</td>
                    <td>${u.creationTime?datetime}</td>
                </tr>
            <#else>
                <tr class="noData">
                    <td colspan="3">No data</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>
