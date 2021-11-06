const FETCH_ARTICLES_URL = "http://localhost:7228/api/article/page";
const urlParams = new URLSearchParams(window.location.search);
const pageSize = 1;
let pageIndex;
try {
    let temp = parseInt(urlParams.get("page"));
    pageIndex = temp > 0 ? temp - 1 : 0;
} catch (e) {
    pageIndex = 0;
}

$.ajax({
    url: FETCH_ARTICLES_URL,
    type: "GET",
    data: {
        index: pageIndex,
        size: pageSize
    }
}).done(function (response) {
    initArticles(response.content);
    initPaginationNav(response.totalPages);

}).fail(function (error) {
    if (error.status === 404 && pageIndex !== 0) {
        location.href = "?page=0";
    } else {
        showError(error.responseJSON.message);
    }
});

$("#redirect-button").click(function () {
    window.location.href = "upload.html";
});

function initArticles(articlesJSON) {
    let container = $("#article-container");
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
        $(article).find(".p-3").text(articleJSON.body);
        $(article).find("em").text(formatDate(articleJSON.creationTime));
        container.append(article);
    }
}

function initPaginationNav(totalPages) {
    let pagination = $("#pagination");
    let from = pageIndex;
    let to = Math.min(pageIndex + 2, totalPages);
    pagination.append(`<li class="page-item"><a class="page-link" href="?page=${from}">Previous</a></li>`);
    if (pageIndex === 0) {
        from = 1;
        to = 3;
        pagination.children().last().addClass("disabled");
    }
    for (let i = from; i <= to; i++) {
        pagination.append(`<li class="page-item"><a class="page-link" href="?page=${i}">${i}</a></li>`);
        if (i === pageIndex + 1) {
            pagination.children().last().addClass("active");
        }
    }
    pagination.append(`
    <li class="page-item">
        <a class="page-link" href="?page=${pageIndex === 0 ? 2 : to}">Next</a>
    </li>`);
    if (pageIndex === totalPages - 1) {
        pagination.children().last().addClass("disabled");
    }
}

function formatDate(date) {
    return date.slice(11, 16) + " (UTC), " + date.slice(0, 10);
}