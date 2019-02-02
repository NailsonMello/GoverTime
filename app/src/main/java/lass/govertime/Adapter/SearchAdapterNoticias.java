package lass.govertime.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import lass.govertime.PerfilPoliticoAntigo;
import lass.govertime.R;
import lass.govertime.TelaNoticia;

public class SearchAdapterNoticias extends RecyclerView.Adapter<SearchAdapterNoticias.SearchViewHolderNoticias>{

    Context context;
    ArrayList<String> textoList;
    ArrayList<String> descList;
    ArrayList<String> imgNList;
    ArrayList<String> img1NList;
    ArrayList<String> idNList;
    ArrayList<String> linkList;
    ArrayList<String> dataList;

    class SearchViewHolderNoticias extends RecyclerView.ViewHolder{

        ImageView imgNoticias, imgNoticias1;
        TextView textoNoticias,descNoticias, dataNoticias;

        public SearchViewHolderNoticias(View itemView) {
            super(itemView);
            imgNoticias = (ImageView)itemView.findViewById(R.id.imgNoticias);
            imgNoticias1 = (ImageView)itemView.findViewById(R.id.imgNoticias1);
            textoNoticias = (TextView)itemView.findViewById(R.id.textoNoticias);
            descNoticias = (TextView)itemView.findViewById(R.id.descNoticias);
            dataNoticias = (TextView)itemView.findViewById(R.id.dataNoticias);

        }
    }

    public SearchAdapterNoticias(Context context, ArrayList<String> textoList,
                                 ArrayList<String> descList, ArrayList<String> imgNList, ArrayList<String> img1NList,
                                 ArrayList<String> idNList, ArrayList<String> linkList, ArrayList<String> dataList) {
        this.context = context;
        this.textoList = textoList;
        this.descList = descList;
        this.imgNList = imgNList;
        this.img1NList = img1NList;
        this.idNList = idNList;
        this.linkList = linkList;
        this.dataList = dataList;
    }

    @Override
    public SearchAdapterNoticias.SearchViewHolderNoticias onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_noticias, parent, false);
        return new SearchAdapterNoticias.SearchViewHolderNoticias(view);
    }

    @Override
    public void onBindViewHolder(final SearchAdapterNoticias.SearchViewHolderNoticias holder, final int position) {

        holder.textoNoticias.setText(textoList.get(position));
        holder.descNoticias.setText(descList.get(position));
        holder.dataNoticias.setText(dataList.get(position));
        if (imgNList.equals("default")){
            holder.imgNoticias.setVisibility(View.GONE);
        }else {
            Glide.with(context).load(imgNList.get(position)).into(holder.imgNoticias);
        }
        if (img1NList.equals("default")){
            holder.imgNoticias1.setVisibility(View.GONE);
        }else {
            Glide.with(context).load(img1NList.get(position)).into(holder.imgNoticias1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chave_noticia = linkList.get(position);
                Context ctx = holder.itemView.getContext();
                Intent telaPres = new Intent(ctx, TelaNoticia.class);
                telaPres.putExtra("linkNoticia", chave_noticia);
                telaPres.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(telaPres);


            }
        });

    }


    @Override
    public int getItemCount() {
        return textoList.size();
    }
}

