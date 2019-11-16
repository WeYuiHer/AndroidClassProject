package com.example.testfromteacher;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Album_item> mAlbumList;
    public static final String EXTRA_MESSAGE = "com.example.testfromteacher.MESSAGE";
    class MyHolder extends RecyclerView.ViewHolder{
        View album_view;
        ImageView people_Image;
        TextView people_Name;

        public MyHolder (View view)//定义内部类ViewHolder,并继承RecyclerView.ViewHolder。传入的View参数通常是RecyclerView子项的最外层布局。
        {
            super(view);
            album_view=view;
            people_Image = view.findViewById(R.id.album_image);
            people_Name = view.findViewById(R.id.album_name);
        }

    }

    public  AlbumAdapter (ArrayList<Album_item> albumList){//AlbumAdapter构造函数,用于把要展示的数据源传入,并赋予值给全局变量mAlbumList。
        this.mAlbumList = albumList;
    }

    //FruitAdapter继承RecyclerView.Adapter。必须重写onCreateViewHolder(),onBindViewHolder()和getItemCount()三个方法

    //onCreateViewHolder()用于创建ViewHolder实例,并把加载的布局传入到构造函数去,再把ViewHolder实例返回。
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        final ViewGroup p=parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item,parent,false);
        final MyHolder holder=new MyHolder(view);
        //holder = new MyHolder(view);
        holder.album_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = holder.getAdapterPosition();
                Album_item ai = mAlbumList.get(position);
                Intent intent = new Intent(p.getContext(),photos.class);
                String message = ai.getName();
                intent.putExtra(EXTRA_MESSAGE, message);
                p.getContext().startActivity(intent);
                //Toast.makeText(view.getContext(), "you clicked view" + ai.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

//onBindViewHolder()则是用于对子项的数据进行赋值,会在每个子项被滚动到屏幕内时执行。position得到当前项的Fruit实例。
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        MyHolder holder1=(MyHolder)holder;
        Album_item album = mAlbumList.get(position);
        holder1.people_Image.setImageBitmap(album.getBitmap());//这个地方才是设置前端显示的东西的，图和名字
        holder1.people_Name.setText(album.getName());
    }

    //getItemCount()返回RecyclerView的子项数目。
    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

}
