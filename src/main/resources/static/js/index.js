const FETCH_ARTICLES_URL = "http://localhost:7228/api/article/";

$("#redirect-button").click(function () {
    window.location.href = "upload.html";
});

$.get(FETCH_ARTICLES_URL).done(function (articles) {
    let container = $("#article-container");
    container.empty();
    for (let article of articles) {
        container.append(`
            <article>
                <div class="row">
                    <h2 class="title text text-center p-4">${article.title}</h2>
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
            </article>
        `);
    }
}).fail(function (error) {
    showError(error.responseJSON.message);
});

function formatDate(date) {
    return date.slice(11, 16) + ' (UTC), ' + date.slice(0, 10);
}