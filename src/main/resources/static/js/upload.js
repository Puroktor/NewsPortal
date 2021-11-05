const UPLOAD_URL = "http://localhost:7228/api/article/upload";

$("#redirect-button").click(function () {
    window.location.href = "index.html";
});

$("#submit").click(function () {
    let file = $("#input").prop("files")[0];
    if (file === undefined || file.name.split(".").pop() !== "zip") {
        showMessage("Select .zip file!");
        return;
    }
    let formData = new FormData();
    formData.append( "file", file);
    $.ajax({
        url: UPLOAD_URL,
        type: 'POST',
        data: formData,
        cache: false,
        contentType: false,
        processData: false
    }).done(function (){
        showMessage("Success!", "The article was successfully added.");
    }).fail(function (response) {
        showError(response.responseJSON.message);
    });
});