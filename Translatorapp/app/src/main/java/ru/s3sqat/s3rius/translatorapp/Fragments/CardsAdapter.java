package ru.s3sqat.s3rius.translatorapp.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.s3sqat.s3rius.translatorapp.AllCards;
import ru.s3sqat.s3rius.translatorapp.MainActivity;
import ru.s3sqat.s3rius.translatorapp.R;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.Holder> {


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.text.setText(AllCards.cards.get(position).getText());
        holder.translation.setText(AllCards.cards.get(position).getTranslate());
        String direction = AllCards.cards.get(position).getDirectionFrom() + "-" + AllCards.cards.get(position).getDirectionTo();
        holder.direction.setText(direction);
        if (AllCards.cards.get(position).isMark()) {
            holder.bookmark.setImageDrawable(AllCards.getContext().getResources().getDrawable(R.drawable.bookmark_yellow_big, null));
        } else {
            holder.bookmark.setImageDrawable(AllCards.getContext().getResources().getDrawable(R.drawable.bookmark_big, null));
        }
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllCards.changeMark(holder.getPosition());
                AllCards.notifyBookmarkChanged(holder.getPosition());
                notifyItemChanged(holder.getPosition());
            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllCards.getContext());
                builder.setCancelable(true)
                        .setTitle(AllCards.getContext().getResources()
                                .getString(R.string.are_you_sure))
                        .setMessage(AllCards.getContext().getResources()
                                .getString(R.string.deleteCard))
                        .setPositiveButton(AllCards.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AllCards.removeCard(holder.getPosition());
                                notifyItemRemoved(holder.getPosition());
                                AllCards.notifyBookmarkRemoved(holder.getPosition());
                            }
                        }).setNegativeButton(AllCards.getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment translator = new Translator();
                Bundle bundle = new Bundle();
                bundle.putString("fromPosition", AllCards.cards.get(holder.getPosition()).getDirectionFrom());
                bundle.putString("toPosition", AllCards.cards.get(holder.getPosition()).getDirectionTo());
                bundle.putString("sourceText", AllCards.cards.get(holder.getPosition()).getText());
                translator.setArguments(bundle);
                android.support.v4.app.FragmentTransaction transaction =
                        ((MainActivity) AllCards.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, translator);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return AllCards.cards.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        CardView card;
        TextView text;
        TextView translation;
        TextView direction;
        ImageView bookmark;

        Holder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            text = (TextView) itemView.findViewById(R.id.sourceCard);
            translation = (TextView) itemView.findViewById(R.id.translationCard);
            direction = (TextView) itemView.findViewById(R.id.directionCard);
            bookmark = (ImageView) itemView.findViewById(R.id.bookmarkCard);
        }
    }
}
