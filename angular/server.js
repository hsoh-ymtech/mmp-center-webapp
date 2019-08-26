var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var request = require('request');

var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(function(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
    res.header('Access-Control-Allow-Headers', 'content-type');
    next();
});

var userToken = 'TEST_TOKEN';

// const proxy = require('http-proxy-middleware')
// var apiProxy = proxy('/api', {
//     target: "http://localhost:8090",
//     secure: false,
//     logLevel: "debug",
//     pathRewrite: {"^/api": ""}
//   });

// app.use(apiProxy)


// const url = require('url');
// const proxy = require('express-http-proxy');
// // New hostname+path as specified by question:
// const apiProxy = proxy('http://192.168.80.128:8090/api', {
//     forwardPath: req => url.parse(req.baseUrl).path
// });
// app.use('/api/**', apiProxy);


app.use('/api', function(req, res) {
    var url = 'http://127.0.0.1:8090/' + req.url;
    var r = null;

    if (req.method === 'POST') {
        r = request.post({uri: url, json: req.body});
        req.pipe(r, {end: false}).pipe(res);
    } else if (req.method === 'PUT') {
        r = request.put({uri: url, json: req.body});
        req.pipe(r, {end: false}).pipe(res);
    } else if (req.method === 'DELETE') { 
        r = request.delete({uri: url, json: req.body});
        req.pipe(r, {end: false}).pipe(res);
    } else {
        r = request(url);
        req.pipe(r).pipe(res);
    }
});


app.use(express.static(path.resolve(__dirname, './dist')));
app.get('*', function(req, res) {
    var indexFile = path.resolve(__dirname, './dist/index.html');
    res.sendFile(indexFile);
});

var port = 4201;
app.listen(port, function() {
    console.log('listening on port: ' + port);
});
