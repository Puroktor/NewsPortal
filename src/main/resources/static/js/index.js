const FETCH_ARTICLES_PAGE_URL = "http://localhost:7228/api/article/page";
const urlParams = new URLSearchParams(window.location.search);
const FETCH_THEME_PAGE_URL = "http://localhost:7228/api/article/themePage";
const pageSize = 1;
const pageTheme = urlParams.has("theme") ? urlParams.get("theme") : "All";
let pageIndex;
try {
    let temp = parseInt(urlParams.get("page"));
    pageIndex = temp > 0 ? temp - 1 : 0;
} catch (e) {
    pageIndex = 0;
}

$("#mainDropdownButton").text(pageTheme);
let URL, data;
if (pageTheme === "All") {
    data = {
        index: pageIndex,
        size: pageSize
    };
    URL = FETCH_ARTICLES_PAGE_URL;
} else {
    data = {
        index: pageIndex,
        size: pageSize,
        theme: pageTheme
    };
    URL = FETCH_THEME_PAGE_URL;
}
$.ajax({
    url: URL,
    type: "GET",
    data: data
}).done(function (response) {
    initArticles(response.content);
    initPaginationNav(response.totalPages);

}).fail(function (error) {
    if (error.status !== 404) {
        showError(error.responseJSON.message);
    }
});

$("#redirect-button").click(function () {
    window.location.href = "upload.html";
});

$(".dropdown-item").click(function () {
    window.location.href = buildURL(["theme"], [$(this).text()]);
});

function initArticles(articlesJSON) {
    let container = $("#article-container");
    container.empty();
    for (let articleJSON of articlesJSON) {
        let article = $.parseHTML(`
            <article>
                <div class="row">
                    <h2 class="title text text-center p-3"></h2>
                </div>
                <div class="row m-2">
                    <em class="text text-start"></em>
                </div>
                <div class="row m-2">
                    <hr/>
                    <p class="text p-3"></p>
                    <hr/>
                </div>
                <div class="row">
                    <em class="text text-end"></em>
                </div>
            </article>
        `);
        $(article).find("h2").text(articleJSON.title);
        $(article).find(".text-start").text(`Theme: ${articleJSON.theme}`);
        $(article).find("p").text(articleJSON.body);
        $(article).find(".text-end").text(formatDate(articleJSON.creationTime));
        container.append(article);
    }
}

function initPaginationNav(totalPages) {
    let pagination = $("#pagination");
    let from = pageIndex;
    let to = Math.min(pageIndex + 2, totalPages);
    pagination.append(`
        <li class="page-item">
            <a class="page-link" href="${buildURL(["page", "theme"], [from, pageTheme])}">Previous</a>
        </li>
    `);
    if (pageIndex === 0) {
        from = 1;
        to = Math.min(3, totalPages);
        pagination.children().last().addClass("disabled");
    }
    for (let i = from; i <= to; i++) {
        pagination.append(`
            <li class="page-item">
                <a class="page-link" href="${buildURL(["page", "theme"], [i, pageTheme])}">${i}</a>
            </li>
        `);
        if (i === pageIndex + 1) {
            pagination.children().last().addClass("active");
        }
    }
    pagination.append(`
        <li class="page-item">
            <a class="page-link" href="${buildURL(["page", "theme"], [pageIndex === 0 ? 2 : to, pageTheme])}">
                Next
            </a>
        </li>`
    );
    if (pageIndex === totalPages - 1) {
        pagination.children().last().addClass("disabled");
    }
}

function formatDate(date) {
    return date.slice(11, 16) + " (UTC), " + date.slice(0, 10);
}

function buildURL(names, values) {
    let base = "index.html?";
    let params = new URLSearchParams();
    for (let i = 0; i < names.length; i++) {
        params.append(names[i], values[i]);
    }
    return base + params.toString();
}

