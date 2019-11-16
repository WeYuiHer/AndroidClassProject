var createError = require('http-errors');
var express = require('express');
var path = require('path');
const fs=require('fs')
var cookieParser = require('cookie-parser');
var logger = require('morgan');

//var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var searchRouter=require('./routes/search');
var getimgRouter=require('./routes/getimg');
var getphotoRouter=require('./routes/getphotos');
var getuseridRouter=require('./routes/getuserid');
var imgRouter = require('./routes/img');
//var fileRouter = require('./routes/files');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

//app.use('/', indexRouter);
app.use('/getuserid',getuseridRouter);
app.use('/search',searchRouter);
app.use('/users', usersRouter);
app.use('/img',imgRouter);
app.use('/getphotos',getphotoRouter);
app.use('/getimg',getimgRouter);
//app.use('/files', fileRouter);

app.get("/files",function (req,res) {
  fs.readdir('public/images',function (err,dir) {
    res.json(dir)
  })
})
module.exports = app;
