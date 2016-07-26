package com.omkarmoghe.pokemap.views.settings;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

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
                // TODO: 26.07.16 maybe enable localization here?
                entries.add(capitalize(pokemonId.name().toLowerCase()));
                entriesValues.add(String.valueOf(pokemonId.getNumber()));
                defaultValues.add(String.valueOf(pokemonId.getNumber()));
            }
        }

        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entriesValues.toArray(new CharSequence[]{}));
        // all pokemons are shown by default
        setDefaultValue(defaultValues);
    }

    private String capitalize(String string) {
        return string.toUpperCase().charAt(0) + string.substring(1);
    }
}
