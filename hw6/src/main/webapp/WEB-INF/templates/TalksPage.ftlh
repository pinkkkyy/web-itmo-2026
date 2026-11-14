<#-- @ftlvariable name="recipients" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="talks" type="ru.itmo.wp.model.domain.Talk[]" -->
<#-- @ftlvariable name="targetUserId" type="java.lang.String" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->
<#-- @ftlvariable name="error" type="java.lang.String" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="talk-form form-box">
        <div class="header">Send message</div>
        <div class="body">
            <form method="post" action="/talks">
                <input type="hidden" name="action" value="send"/>
                <div class="field">
                    <div class="name">
                        <label for="targetUserId">To</label>
                    </div>
                    <div class="value">
                        <select id="targetUserId" name="targetUserId">
                            <#list recipients as recipient>
                                <option value="${recipient.id?c}"<#if (targetUserId!"") == recipient.id?c> selected</#if>>${recipient.login}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="text">Text</label>
                    </div>
                    <div class="value">
                        <textarea id="text" name="text" rows="5">${text!}</textarea>
                    </div>
                </div>
                <#if error??>
                    <div class="error">${error}</div>
                </#if>
                <div class="button-field">
                    <input type="submit" value="Send">
                </div>
            </form>
        </div>
    </div>

    <div class="talks datatable">
        <div class="caption">Messages</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>From</th>
                <th>To</th>
                <th>Text</th>
                <th>When</th>
            </tr>
            </thead>
            <tbody>
            <#list talks as talk>
                <tr>
                    <td>${talk.id}</td>
                    <td>${talk.sourceUserLogin}</td>
                    <td>${talk.targetUserLogin}</td>
                    <td class="talk-text">${talk.text}</td>
                    <td>${talk.creationTime?datetime}</td>
                </tr>
            <#else>
                <tr class="noData">
                    <td colspan="5">No data</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>
