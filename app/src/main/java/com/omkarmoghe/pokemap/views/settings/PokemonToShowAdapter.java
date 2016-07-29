package com.omkarmoghe.pokemap.views.settings;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;
import com.omkarmoghe.pokemap.helpers.RemoteImageLoader;
import com.omkarmoghe.pokemap.util.PokemonIdUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import POGOProtos.Enums.PokemonIdOuterClass;

/**
 * Custom adapter to show pokemon and their icons in the preferences screen.
 * <p>
 * Created by fess on 26.07.16.
 */
class PokemonToShowAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private final List<CharSequence> mEntries = new ArrayList<>();

    private final Set<PokemonIdOuterClass.PokemonId> mSelected = new HashSet<>();

    PokemonToShowAdapter(Context context,
                         CharSequence[] entries) {
        Collections.addAll(mEntries, entries);

        mInflater = LayoutInflater.from(context);
        PokemapAppPreferences mPref = new PokemapSharedPreferences(context);
        mSelected.addAll(mPref.getShowablePokemonIDs());
    }

    Set<PokemonIdOuterClass.PokemonId> getShowablePokemonIDs() {
        return mSelected;
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        CustomHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.item_pokemon_to_show_preference, viewGroup, false);
            holder = new CustomHolder(row);
        } else {
            holder = (CustomHolder) row.getTag();
        }

        holder.bind(row, position);
        row.setTag(holder);

        return row;
    }

    private class CustomHolder {
        private CheckedTextView mCheckableTextView = null;
        private ImageView mImageView = null;

        CustomHolder(View row) {
            mCheckableTextView = (CheckedTextView) row.findViewById(R.id.textView);
            mImageView = (ImageView) row.findViewById(R.id.imageView);
        }

        void bind(final View row, final int position) {
            final PokemonIdOuterClass.PokemonId pokemonId = PokemonIdOuterClass.PokemonId.forNumber(position + 1);

            mCheckableTextView.setText((CharSequence) getItem(position));
            mCheckableTextView.setChecked(mSelected.contains(pokemonId));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PokemonIdOuterClass.PokemonId pokemonId = PokemonIdOuterClass.PokemonId.forNumber(position + 1);
                    if (mSelected.contains(pokemonId)) {
                        mSelected.remove(pokemonId);
                    } else {
                        mSelected.add(pokemonId);
                    }
                    mCheckableTextView.setChecked(mSelected.contains(pokemonId));
                }
            });

            RemoteImageLoader.loadInto(mImageView,
                    "http://serebii.net/pokemongo/pokemon/" + PokemonIdUtils.getCorrectPokemonImageId(pokemonId.getNumber()) + ".png");

        }
    }
}
