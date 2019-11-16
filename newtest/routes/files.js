var express = require('express');
var router = express.Router();
module.exports = router
const  mongoose=require('mongoose')


const fs=require('fs')
//
const multer=require('multer')
let upload = multer({ dest: 'uploads/' })


let Img=mongoose.model('img')
router.get('/files', function(req, res, next) {
//    Img.create({filename:'fromget',imgurl:20}, function(err, res){
  //      if (err) {
    //        console.log(err);
      //  } else {
        //    console.log(res);
       // }
   // });
    fs.readdir('public/images',function (err,dir) {
        res.json(dir)
    })

    res.send("get is success!!!");
});
