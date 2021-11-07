const UPLOAD_URL = "api/article/upload";
let selectedTheme;
$("#redirect-button").click(function () {
    window.location.href = "index.html";
});

$(".dropdown-item").click(function () {
    selectedTheme = $(this).text();
    $("#mainDropdownButton").text(selectedTheme);
});

$("#submit").click(function () {
    let file = $("#input").prop("files")[0];
    if (file === undefined || file.name.split(".").pop() !== "zip") {
        showError("Select .zip file!");
        return;
    }
    if (selectedTheme === undefined) {
        showError("Select a theme!");
        return;
    }
    let formData = new FormData();
    formData.append("file", file);
    $.ajax({
        url: `${UPLOAD_URL}?theme=${selectedTheme}`,
        type: "POST",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        origin: "*"
    }).done(function () {
        showMessage("Success!", "The article was successfully added.");
    }).fail(function (response) {
        showError(response.responseJSON.message);
    });
});