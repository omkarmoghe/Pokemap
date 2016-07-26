package com.omkarmoghe.pokemap.views.settings;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import com.omkarmoghe.pokemap.util.PokemonIdUtils;

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

        PokemonIdOuterClass.PokemonId[] ids = PokemonIdOuterClass.PokemonId.values();

        for (PokemonIdOuterClass.PokemonId pokemonId : ids) {
            if ((pokemonId != PokemonIdOuterClass.PokemonId.MISSINGNO) && (pokemonId != PokemonIdOuterClass.PokemonId.UNRECOGNIZED)) {
                entries.add(PokemonIdUtils.getLocalePokemonName(context.getResources(), pokemonId));
                entriesValues.add(String.valueOf(pokemonId.getNumber()));
                defaultValues.add(String.valueOf(pokemonId.getNumber()));
            }
        }

        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entriesValues.toArray(new CharSequence[]{}));
        // all pokemons are shown by default
        setDefaultValue(defaultValues);
    }
}
