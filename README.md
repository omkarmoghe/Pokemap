# Pokemap [![Build Status](https://travis-ci.org/omkarmoghe/Pokemap.svg?branch=master)](https://travis-ci.org/omkarmoghe/Pokemap)
A native Android client built with https://github.com/AHAAAAAAA/PokemonGo-Map

# Building
To build this you need to add your **PTC** username and password to the `getToken` function in the Activity's `onCreate` method as follows:

```Java
...
try {
  getToken(USERNAME, PASSWORD);
} catch (IOException e) {
  e.printStackTrace();
}
...
```

# PRs - please follow the [Android style guides & best practices](https://source.android.com/source/code-style.html)
So the main work we need to do is to effectively translate the code over at the [main repo](https://github.com/AHAAAAAAA/PokemonGo-Map) to work in *native* Android. The login functionality to get the token has been implemented and I started migrating the code to grab the actual Pokemon / Pokestop data using the `.proto` files.

Please read through the main repo to see how the Python code is grabbing the spawned Pokemon, etc. We need to recreate that functionality in Java :D.

Also please read this: https://www.reddit.com/r/pokemongodev/comments/4svl1o/guide_to_pokemon_go_server_responses/

#[Offical Website] (https://jz6.github.io/PoGoMap/#)
Live visualization of all pokemon (with option to show gyms and pokestops) in your area. This is a proof of concept that we can load all nearby pokemon given a location. Currently runs on a Flask server displaying a Google Map with markers on it.

Using this software is against the ToS and can get you banned. Use at your own risk.

Building off [Mila432](https://github.com/Mila432/Pokemon_Go_API)'s PokemonGo API, [tejado's additions](https://github.com/tejado/pokemongo-api-demo), [leegao's additions](https://github.com/leegao/pokemongo-api-demo/tree/simulation) and [Flask-GoogleMaps](https://github.com/rochacbruno/Flask-GoogleMaps).

---
For instructions, please refer to [the wiki](https://github.com/AHAAAAAAA/PokemonGo-Map/wiki)
