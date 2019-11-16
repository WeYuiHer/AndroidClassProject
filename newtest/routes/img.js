var express = require('express');
var router = express.Router();
const fs=require('fs')
const  mongoose=require('mongoose')

//文件上传中间件(指定上传的临时文件夹是/uploads)
const multer=require('multer')
let upload = multer({ dest: 'uploads/' })
const FILE_PATH="public/images/"


let Img=mongoose.model('img')

router.post('/', upload.single('avatar'),function(req, res, next) {
     let msg={
         body:req.body,
         file:req.file
     }
     //将临时文件上传到/public/images中
     let output=fs.createWriteStream(FILE_PATH+req.file.filename)
     let input=fs.createReadStream(req.file.path)
     input.pipe(output)
     res.json(msg)
     
    console.log(msg);
    Img.create({filename:req.file.filename,face_token:req.body.face_token,userid:req.body.userid,userinfo:req.body.userinfo,groupid:req.body.groupid,upload_time:req.body.upload_time}, function(err, res){
       if (err) {
           console.log(err);
       } else {
           console.log(res);
       }
   });
   // res.send("post is success!!!");
});
module.exports = router
