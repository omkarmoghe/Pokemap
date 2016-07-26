package com.omkarmoghe.pokemap.util;

import android.content.res.Resources;

import com.omkarmoghe.pokemap.R;
import com.pokegoapi.util.Log;

import java.lang.reflect.Field;

import POGOProtos.Enums.PokemonIdOuterClass;

/**
 * Utility methods to ease localization and handling of pokemon IDs.
 * <p>
 * Created by fess on 26.07.16.
 */
public class PokemonIdUtils {

    /**
     * try to resolve PokemonName from Resources
     *
     * @param pokemonId the PokemonID from the API.
     * @return a localized name of the pokemon.
     */
    public static String getLocalePokemonName(Resources resources,
                                              PokemonIdOuterClass.PokemonId pokemonId) {
        String apiPokeName = pokemonId.name();
        int resId = 0;
        try {
            Class resClass = R.string.class;
            Field field = resClass.getField(apiPokeName.toLowerCase());
            resId = field.getInt(null);
        } catch (Exception e) {
            Log.e("PokemonTranslation", "Failure to get Name", e);
            resId = -1;
        }
        return resId > 0 ? resources.getString(resId) : apiPokeName;
    }

    //Getting correct pokemon Id eg: 1 must be 001, 10 must be 010
    public static String getCorrectPokemonImageId(int pokemonNumber) {
        String actualNumber = String.valueOf(pokemonNumber);
        if (pokemonNumber < 10) {
            return "00" + actualNumber;
        } else if (pokemonNumber < 100) {
            return "0" + actualNumber;
        } else {
            return actualNumber;
        }
    }
}
