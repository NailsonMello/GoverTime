package lass.govertime.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import lass.govertime.PerfilPoliticoAntigo;
import lass.govertime.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;
    ArrayList<String> nomeList;
    ArrayList<String> imgList;
    ArrayList<String> idList;

    class SearchViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imgPres;
        TextView nomePress;

        public SearchViewHolder(View itemView) {
            super(itemView);
            imgPres = (CircleImageView)itemView.findViewById(R.id.imagem_anuncio);
            nomePress = (TextView)itemView.findViewById(R.id.idNome);

        }
    }
    public SearchAdapter(Context context, ArrayList<String> nomeList, ArrayList<String> imgList, ArrayList<String> idList) {
        this.context = context;
        this.nomeList = nomeList;
        this.imgList = imgList;
        this.idList = idList;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_presidentes, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {

        holder.nomePress.setText(nomeList.get(position));

        Glide.with(context).load(imgList.get(position)).asBitmap().placeholder(R.drawable.default_avatar).into(holder.imgPres);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rr = idList.get(position);
                Context ctx = holder.itemView.getContext();
                Intent telaPres = new Intent(ctx, PerfilPoliticoAntigo.class);
                telaPres.putExtra("anuncio_id", rr);
                telaPres.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(telaPres);


            }
        });

    }


    @Override
    public int getItemCount() {
        return nomeList.size();
    }
}
