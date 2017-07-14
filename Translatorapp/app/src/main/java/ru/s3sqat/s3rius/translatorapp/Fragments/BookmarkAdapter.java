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
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.s3sqat.s3rius.translatorapp.AllCards;
import ru.s3sqat.s3rius.translatorapp.MainActivity;
import ru.s3sqat.s3rius.translatorapp.R;

/**
 * Created by s3rius on 14.07.17.
 */

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.Holder> {


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
            holder.setVisibility(true);
            holder.bookmark.setImageDrawable(AllCards.getContext().getResources().getDrawable(R.drawable.bookmark_yellow_big, null));
        } else {
            holder.setVisibility(false);
        }
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllCards.changeMark(position);
                AllCards.notifyCardChanged(position);
                notifyItemChanged(position);
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
                                AllCards.removeCard(position);
                                notifyItemRemoved(position);
                                AllCards.notifyCardRemoved(position);
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
                bundle.putString("fromPosition", AllCards.cards.get(position).getDirectionFrom());
                bundle.putString("toPosition", AllCards.cards.get(position).getDirectionTo());
                bundle.putString("sourceText", AllCards.cards.get(position).getText());
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

        void setVisibility(boolean isVisible) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }
}
