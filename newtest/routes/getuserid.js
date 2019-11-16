var express = require('express');
var router = express.Router();
const fs=require('fs')
const  mongoose=require('mongoose')

const multer=require('multer')
let upload = multer({ dest: 'uploads/' })
const FILE_PATH="public/images/"


let Img=mongoose.model('img')
//var img_result;
//var img_url;
var userid_result;
//var userid_from_res;


Img.distinct('userid', function (err, userid) {
  if (err) 
  return handleError(err);
  // Prints "Space Ghost is a talk show host".
  else {
    userid_result=userid;
    console.log(userid_result);
  }
});

router.get('/', function(req, res, next) {
res.json(userid_result);
});
//router.get('/', function(req, res, next) {
//  userid_from_res=req.query.userid
//  Img.findOne({'userid':userid_from_res},'filename',function (err,imgs) {
//    img_url=FILE_PATH+imgs['filename'];
//  });
//  console.log(img_url);
// console.log(userid_from_res);
//  res.download(img_url);
//});
module.exports = router
