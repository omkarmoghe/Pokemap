# Pokemap [![Build Status](https://travis-ci.org/omkarmoghe/Pokemap.svg?branch=dev)](https://travis-ci.org/omkarmoghe/Pokemap) [![Download](https://img.shields.io/badge/download-latest-brightgreen.svg?style=flat-square)](https://github.com/omkarmoghe/Pokemap/releases)
A native Android client built with https://github.com/AHAAAAAAA/PokemonGo-Map

[Join the Slack channel](https://pokemap-android-slack.herokuapp.com/) **Please accept your invite ASAP. We have a limit on how many invites we can send out based on the number of people that actually accept them. We want to give everyone interested an opportunity to join the Slack.**

# [RTFM](https://github.com/omkarmoghe/Pokemap/wiki)
## Why aren't Pokemon showing up?
We're still testing and haven't merged it into this repo yet. Here's a sneak peak:
<img src="http://i.imgur.com/zzad874.png" width="540" height="960">

# PRs - please follow the [Android style guides & best practices](https://source.android.com/source/code-style.html)
## All PRs should go to the `dev` branch. `master` will be updated periodically with stable(ish) releases.

## [TODO](https://slack-files.com/T1TQY34KE-F1TSY25UL-10400392c2)
Please read through the main repo to see how the Python code is grabbing the spawned Pokemon, etc. We need to recreate that functionality in Java :D.

Also please read this: https://www.reddit.com/r/pokemongodev/comments/4svl1o/guide_to_pokemon_go_server_responses/

#[Official Website] (https://jz6.github.io/PoGoMap/#)
Live visualization of all pokemon (with option to show gyms and pokestops) in your area. This is a proof of concept that we can load all nearby pokemon given a location. Currently runs on a Flask server displaying a Google Map with markers on it.

Using this software is against the ToS and can get you banned. Use at your own risk.

Building off [Mila432](https://github.com/Mila432/Pokemon_Go_API)'s PokemonGo API, [tejado's additions](https://github.com/tejado/pokemongo-api-demo), [leegao's additions](https://github.com/leegao/pokemongo-api-demo/tree/simulation) and [Flask-GoogleMaps](https://github.com/rochacbruno/Flask-GoogleMaps).

---
For instructions, please refer to [the wiki](https://github.com/AHAAAAAAA/PokemonGo-Map/wiki)
