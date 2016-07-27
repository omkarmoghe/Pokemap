package com.omkarmoghe.pokemap.views.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;
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
public class PokemonToShowPreference extends MultiSelectListPreference {

    private PokemonToShowAdapter mAdapter;

    private PokemapSharedPreferences mPref;

    public PokemonToShowPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PokemonToShowPreference(Context context) {
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
                entries.add(PokemonIdUtils.getLocalePokemonName(context, pokemonId.name()));
                entriesValues.add(String.valueOf(pokemonId.getNumber()));
                defaultValues.add(String.valueOf(pokemonId.getNumber()));
            }
        }

        setEntries(entries.toArray(new CharSequence[]{}));
        setEntryValues(entriesValues.toArray(new CharSequence[]{}));

        // all pokemons are shown by default
        setDefaultValue(defaultValues);

        mPref = new PokemapSharedPreferences(context);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        final CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();

        if (entries == null || entryValues == null || entries.length != entryValues.length) {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        mAdapter = new PokemonToShowAdapter(getContext(), entries);
        builder.setAdapter(mAdapter, null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            Set<PokemonIdOuterClass.PokemonId> pokemonIDs = mAdapter.getShowablePokemonIDs();
            mPref.setShowablePokemonIDs(pokemonIDs);
        }
    }
}