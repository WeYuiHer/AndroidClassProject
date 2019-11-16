var express = require('express');
var router = express.Router();
const fs=require('fs')
const  mongoose=require('mongoose')

const multer=require('multer')
let upload = multer({ dest: 'uploads/' })
const FILE_PATH="public/images/"
let Img=mongoose.model('img')
var img_result;
var img_url;
var userid_from_res;
let bitmap;

router.get('/', function(req, res, next) {
  userid_from_res=req.query.userid
  Img.findOne({'userid':userid_from_res},'filename',function (err,imgs) {
    img_url=FILE_PATH+imgs['filename'];
    fs.readFile(img_url, (err, data) => {
      if (err) throw err;
      bitmap=data;
     // console.log("bitmap::"+bitmap);
     // console.log("data::"+data);
    
    let base64str = Buffer.from(bitmap, 'binary').toString('base64');//base64编码
   // console.log(base64str);
    console.log(img_url);
    console.log(userid_from_res);
    res.json(base64str);
  //res.download(img_url);
});
});
});
module.exports = router
