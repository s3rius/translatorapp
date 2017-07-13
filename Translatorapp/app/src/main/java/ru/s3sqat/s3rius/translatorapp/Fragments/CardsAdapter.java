package ru.s3sqat.s3rius.translatorapp.Fragments;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.s3sqat.s3rius.translatorapp.AllCards;
import ru.s3sqat.s3rius.translatorapp.R;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.Holder> {
    Context context;

    CardsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.text.setText(AllCards.cards.get(position).getText());
        holder.translation.setText(AllCards.cards.get(position).getTranslate());
        String direction = AllCards.cards.get(position).getDirectionFrom() + "-" + AllCards.cards.get(position).getDirectionTo();
        holder.direction.setText(direction);
        if (AllCards.cards.get(position).isMark()) {
            holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_yellow_big, null));
        } else {
            holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_big, null));
        }
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllCards.changeMark(position);
                if (AllCards.cards.get(position).isMark()) {
                    holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_yellow_big, null));
                } else {
                    holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_big, null));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return AllCards.cards.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView card;
        TextView text;
        TextView translation;
        TextView direction;
        ImageView bookmark;

        public Holder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            text = (TextView) itemView.findViewById(R.id.sourceCard);
            translation = (TextView) itemView.findViewById(R.id.translationCard);
            direction = (TextView) itemView.findViewById(R.id.directionCard);
            bookmark = (ImageView) itemView.findViewById(R.id.bookmarkCard);
        }
    }
}
