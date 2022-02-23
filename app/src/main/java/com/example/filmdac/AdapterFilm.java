package com.example.filmdac;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class AdapterFilm extends FirestoreRecyclerAdapter<ModelFilm, AdapterFilm.FilmsViewHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterFilm(@NonNull FirestoreRecyclerOptions<ModelFilm> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FilmsViewHolder holder, int position, @NonNull ModelFilm model) {
        String titre = model.getTitre();
        String synopsis = model.getSynopsis();
        String affiche = model.getAffiche();

        holder.tvTitre.setText(titre);
        holder.tvSynopsis.setText(synopsis);

        RequestOptions opts = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic__movies_black_48)
                .placeholder(R.mipmap.ic_launcher);

        Context ctx = holder.ivAffiche.getContext();

        Glide.with(ctx)
                .load(affiche)
                .apply(opts)
                .fitCenter()
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivAffiche);
    }

    @NonNull
    @Override
    public FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v1 = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.row_film, parent, false);
        return new FilmsViewHolder(v1);
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.e("Adapter Film", "Error Firebase : "+ e.getMessage());
    }

    public class FilmsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAffiche;
        private TextView tvTitre, tvSynopsis;

        public FilmsViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAffiche = itemView.findViewById(R.id.ivAfficheFilm);
            tvTitre = itemView.findViewById(R.id.tvTitre);
            tvSynopsis = itemView.findViewById(R.id.tvSynopsis);


            itemView.setOnClickListener(new View.OnClickListener() {
           /*     int count = 0;
                Handler handler = new Handler();
                Runnable runnable = () -> count = 0;*/

                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && filmClickListener != null) {
                        DocumentSnapshot filmSS = getSnapshots().getSnapshot(pos);
                        filmClickListener.onItemClick(filmSS, pos);

                        /*
                    if (!handler.hasCallbacks(runnable))
                        handler.postDelayed(runnable, 500);
                    if(count==2) {
                        View is double clicked.Now code here.

                        int pos = getBindingAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION && filmClickListener != null) {
                            DocumentSnapshot filmSS = getSnapshots().getSnapshot(pos);
                            filmClickListener.onItemClick(filmSS, pos);
                        }*/
                    }
                }


            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int pos);
    }

    private OnItemClickListener filmClickListener;

    public void setOnItemClickListener(OnItemClickListener filmCL1) {
        this.filmClickListener = filmCL1;
    }
}
