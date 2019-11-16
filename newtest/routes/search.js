var express = require('express');
var router = express.Router();
const  mongoose=require('mongoose')
let Img=mongoose.model('img')
var face_token_from_res;
var result;

router.get('/', function(req, res, next) {
  face_token_from_res=req.query.face_token
  userid_from_res=req.query.userid;
  Img.find({'face_token':face_token_from_res,'userid':userid_from_res},'filename',function (err,img) {
  result=img;
  console.log(result);
  res.json(result);

});
});

module.exports = router
