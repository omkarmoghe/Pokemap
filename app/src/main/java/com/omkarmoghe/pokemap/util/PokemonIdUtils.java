package com.omkarmoghe.pokemap.util;

import android.content.Context;
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

    /**
     * try to resolve PokemonName from Resources
     * @param apiPokeName
     * @return
     */
    public static String getLocalePokemonName(Context context, String apiPokeName){

        int resId = context.getResources().getIdentifier(apiPokeName.toLowerCase(), "string", context.getPackageName());
        return resId > 0 ? context.getString(resId) : apiPokeName;
    }
}
