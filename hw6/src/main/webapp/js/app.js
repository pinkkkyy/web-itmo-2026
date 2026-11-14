window.notify = function (message, isError) {
    $.notify(message, {
        position: "right bottom",
        className: isError ? "error" : "success"
    });
};

window.ajax = function (options) {
    const success = options.success;
    return $.ajax($.extend({
        type: "POST",
        url: location.pathname,
        dataType: "json",
        error: function () {
            notify("Request failed, try again later", true);
        }
    }, options, {
        success: function (response) {
            if (response["redirect"]) {
                location.href = response["redirect"];
                return;
            }
            if (success) {
                success(response);
            }
        }
    }));
};

window.submitFormViaAjax = function ($form) {
    $form.submit(function () {
        const $error = $form.find(".error");
        $error.text("");
        ajax({
            data: $form.serialize(),
            success: function (response) {
                $error.text(response["error"] || "");
            }
        });
        return false;
    });
};
