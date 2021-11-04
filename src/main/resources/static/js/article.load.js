$.get("http://localhost:7228/api/article/").done(function (articles) {
    let container = $("#articleContainer");
    container.empty();
    for (let article of articles) {
        container.append(`
            <div class="row">
                <h2 class="text text-center p-4">${article.title}</h2>
            </div>
            <div class="row m-2">
                <hr/>
                <p class="text p-3">${article.text}</p>
                <hr/>
            </div>
            <div class="row">
                <p class="text text-end">
                    <em>${formatDate(article.creationTime)}</em>
               </p>
            </div>
        `);
    }
}).fail(function(error){
    showError(error.responseJSON);
});

function formatDate(date) {
    return date.slice(11, 16) + ', ' + date.slice(0, 10);
}

function showError(errorJSON) {
    $("#errorText").text(errorJSON.message);
    $("#errorModal").modal("show");
}