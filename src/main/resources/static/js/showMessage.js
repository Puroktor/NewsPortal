function showMessage(title, message){
    $("#modal-title").text(title)
    $("#modal-text").text(message);
    $("#modal").modal("show");
}

function showError(message) {
    showMessage("Error!", message)
}