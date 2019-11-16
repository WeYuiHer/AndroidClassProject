var express = require('express');
var router = express.Router();
const fs=require('fs')
const  mongoose=require('mongoose')

const multer=require('multer')
let upload = multer({ dest: 'uploads/' })
const FILE_PATH="public/images/"
let Img=mongoose.model('img')
var img_url;
var userid_from_res;
var base64_set=[];


router.get('/', function(req, res, next) {
base64_set=[];  
userid_from_res=req.query.userid
  Img.find({'userid':userid_from_res},'filename',function (err,imgs) {
    for (var i = 0; i < imgs.length; i++) { 
    (function(i){
      img_url=FILE_PATH+imgs[i]['filename'];
      fs.readFile(img_url, (err, data) => {
        if (err) throw err;
        bitmap=data;
        let base64str = Buffer.from(bitmap, 'binary').toString('base64');//base64
        base64_set[i]=base64str;
        console.log("第"+i+"次进入读文件循环");
   //     console.log(base64str);
        if (i==imgs.length-1) {res.json(base64_set);}
      });
    })(i);   

    } 
});
});

module.exports = router
