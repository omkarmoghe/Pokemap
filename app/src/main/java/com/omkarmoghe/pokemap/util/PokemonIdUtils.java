package com.omkarmoghe.pokemap.util;

import android.content.Context;

/**
 * Utility methods to ease localization and handling of pokemon IDs.
 * <p>
 * Created by fess on 26.07.16.
 */
public class PokemonIdUtils {

    //Getting correct pokemon Id eg: 1 must be 001, 10 must be 010
    public static String getCorrectPokemonImageId(int pokemonNumber) {
        return String.format("%03d", pokemonNumber);
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
