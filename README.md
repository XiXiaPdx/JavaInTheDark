# JavaInTheDark
A game for memorizing music lyrics.

My wife sings and learns new songs. She gave me the idea to make an app that would help her memorize songs.


## What is JavaInTheDark?

Search for a song lyric using the Music Mix API. The free API only returns 30% of the lyrics. This project is about building the concept and rooting user issues, design challenges, and learning new techniques.

Lyrics are processed and returned into a horizontal recyclerView. Each part of the lyric is clickable to start the game for that portion of the lyric.  Each portion has 5 levels and you advance through as you complete level.

You a complete a level of a portion of the lyric by saying that portion with only a portion of the lyric as a hint.   If you are unable to complete the lyric, and depending on level, you will be provided additional hints.

## Technologies

Java, Android Studio

## Known Bugs

This is a work in progress. The MVP idea has been reached. The speech recognition function is phone and emulator dependent. There are a lots of UI functionality and prompts that need to be added. There are backend logic issues with certain types of lyrics. 

## Prerequisites

You will need the following software properly installed on your computer.

* [Android Studio 2.3.2](https://developer.android.com/studio/index.html)

You will need a MusixMix API key. [You can get it here](https://www.musixmatch.com/)

## Further Exploration


## Installation

Perform the following steps to setup the app to run in Android Studio.

* #### Get the musixmatch API Client ID.

* #### In Android Studio, open the Terminal. At the prompt, navigate into your "AndroidStudioProjects" folder.

Once there, run the following command.
```
git clone https://github.com/XiXiaPdx/JavaInTheDark.git
```
*  #### Navigate into the project folder
```
cd JavaInTheDark
```
*  #### create file gradle.properties to hold your MusicMix ID token. I'm using Atom.
```
atom gradle.properties
```
* #### insert the following text into gradle.properties.

```
org.gradle.jvmargs=-Xmx1536m
LyricAPIKey = "Your Client ID From UnSplash Goes Here!!!"

```
* #### Android Studio should prompt you to Gradle Sync.  At this point, a gradle sync should clear any warnings and the whole project will gradle build.

* #### Run the app in the emulator. I am emulating a Nexus 6.


## License

Copyright (c) 2017 Xi Xia. This software is licensed under the MIT license.
