package com.omkarmoghe.pokemap.util;

import com.pokegoapi.api.map.fort.Pokestop;

/**
 * Created by chris on 7/30/2016.
 */

public class PokestopUtil {

    public static boolean hasLuredPokemon(Pokestop pokestop){
        return pokestop.getFortData().hasLureInfo()
                && pokestop.getFortData().getLureInfo().getLureExpiresTimestampMs() > System.currentTimeMillis();
    }
}
