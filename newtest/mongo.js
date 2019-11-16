const  mongoose=require('mongoose')
const model=require('./models')
let Img=mongoose.model("img",model.ImgSchema)