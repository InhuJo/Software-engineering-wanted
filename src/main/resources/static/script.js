const url = 'http://localhost:8080/_count_';

fetch(url)
.then(data => data.json())
.then((json) => {
    alert(JSON.stringify(json));
})
