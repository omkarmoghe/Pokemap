package com.omkarmoghe.pokemap.views.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import com.omkarmoghe.pokemap.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import POGOProtos.Enums.PokemonIdOuterClass;

/**
 * A multi-select list preference which tells which pokemons to show on the map.
 * <p>
 * Created by fess on 26.07.16.
 */
public class PokemonsToShowPreference extends MultiSelectListPreference {

    public PokemonsToShowPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PokemonsToShowPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        List<CharSequence> entries = new ArrayList<>();
        List<CharSequence> entriesValues = new ArrayList<>();
        Set<String> defaultValues = new HashSet<>();

        String[] pokemonNames = context.getResources().getStringArray(R.array.pokemon_names);
        PokemonIdOuterClass.PokemonId[] ids = PokemonIdOuterClass.PokemonId.values();

        int min = Math.min(pokemonNames.length, ids.length);

        for (int i = 0; i < min; i++) {
            int number = ids[i].getNumber();
            if (number > 0) {
                entries.add(pokemonNames[i]);
                entriesValues.add(String.valueOf(ids[i]));
                defaultValues.add(String.valueOf(ids[i]));
            }
        }

        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entriesValues.toArray(new CharSequence[]{}));
        setDefaultValue(defaultValues);
    }
}
