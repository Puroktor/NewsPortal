const FETCH_ARTICLES_URL = "http://localhost:7228/api/article/";

$("#redirect-button").click(function () {
    window.location.href = "upload.html";
});

$.get(FETCH_ARTICLES_URL).done(function (articlesJSON) {
    let container = $("#article-container");
    container.empty();
    for (let articleJSON of articlesJSON) {
        let article = $.parseHTML(`
            <article>
                <div class="row">
                    <h2 class="title text text-center p-4"></h2>
                </div>
                <div class="row m-2">
                    <hr/>
                    <p class="text p-3"></p>
                    <hr/>
                </div>
                <div class="row">
                    <p class="text text-end">
                        <em></em>
                   </p>
                </div>
            </article>
        `);
        $(article).find("h2").text(articleJSON.title);
        $(article).find(".p-3").text(articleJSON.text);
        $(article).find("em").text(formatDate(articleJSON.creationTime));
        container.append(article);

    }
}).fail(function (error) {
    showError(error.responseJSON.message);
});

function formatDate(date) {
    return date.slice(11, 16) + " (UTC), " + date.slice(0, 10);
}