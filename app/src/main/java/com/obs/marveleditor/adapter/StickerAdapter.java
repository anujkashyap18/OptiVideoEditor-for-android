/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.obs.marveleditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.obs.marveleditor.R;

import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerHolder> {

    Context context;
    int[] stickerIds;
    List<Integer> stringList;

    public StickerAdapter(Context context, List<Integer> stringList) {
        this.context = context;
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public StickerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_show_stickers, viewGroup, false);
        return new StickerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerHolder stickerHolder, int i) {

        stickerHolder.stickers.setImageResource(stringList.get(i));
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public void add() {
        for (int i = 0; i < stickerIds.length; i++) {
            stringList.add(stickerIds[i]);
        }
    }

    public class StickerHolder extends RecyclerView.ViewHolder {

        ImageView stickers;

        public StickerHolder(@NonNull View itemView) {
            super(itemView);
            stickers = itemView.findViewById(R.id.image_sticker);
        }
    }
}
