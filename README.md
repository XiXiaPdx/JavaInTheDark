# Shot In The Dark
Educational Learning App for concepts in Java

Have you ever looked up a Java tutorial and found it lacking?  Did the writer make too many assumptions of your knowledge? Did the writer skip over important details in their process and leave you stuck and taking shots in the dark at finding a solution?

If you haven't, you probably don't need that much help learning to code.  

Otherwise, you might like this app.  



## What is Shot In The Dark?

In barista parlance, a Shot In the Dark is a shot of espresso in a cup of coffee. It is a super concentrated and highly effective stimulant.  Almost never fails!

As an app, Shot In The Dark is a educational learning app that teaches a common and complex idea in Java. It will be a 15-20 level educational game with gamification rewards and level ups to keep attention and user motivation.

Each level will consist of a written and video reference to the subject matter being taught.

Each level will require the user to pass an interactive test in order to reach the next level. Similar in spirit to [CSS Diner](https://flukeout.github.io/).  

The test is driven by drag N drop gestures from the user.

## User Stories

* create a profile and account, saved in Firebase.
* can run through a tutorial example at any time
* can save their progress
* can go back and play accomplished levels
* can drag solutions together on the testing challenge

## Planning

* Not Logged In, can use tutorial (Level 1).
* Logged In, go to current Level.
* After level complete, update Firebase with new current level.
* NavDrawer option for tutorial. Also for completed levels...which goes to a recyclerView Fragment.
* User on this fragment can visit any previous level.
* previous level button while playing?  

#### Phase 1, Research

Log In Page.
- email and password
- create account (name, password, email)
-- full animated loading fragment
- Bonus: google account
- Bonus: facebook account

RecyclerView Vertical of concepts in Java

Drag and Drop feature.
- when drag initiated, the target is under the thumb...hard to see.
- when over target, signal is not obvious that can be dropped.
- an overflow menu with an option to Reset State of question.

List of lessons in a concepts
- Non staggered grid view
- Title, # of questions, "Lock" symbol meaning you haven't unlocked yet. grey background
- Unlocked have different coloring for background, text label, and "play" button.

Each lesson has a chat room, comment thread. Can filter for most recent, most popular. Each question in each lesson has a comment thread. Each comment has a thumb up, down, shows number of replies, which can open up again.

Code Playground, mobile code emulator is built in. 

- [ ] Drag and Drop Reaction test on a fragment
- [ ] Can zoom in and see a larger process flow picture with pinching.
- [ ] Setup NavDrawer

# Technologies

Java, Android Studio

## Prerequisites

You will need the following software properly installed on your computer.

* [Android Studio 2.3.2](https://developer.android.com/studio/index.html)

You'll also need an API Client ID token from UnSplash. [You can get it here](https://unsplash.com/login)

They will ask for your App name but you don't need to have one and will still get a API token. You are limited to 50 calls per hour.


## Installation

Perform the following steps to setup the app to run in Android Studio.

* #### Get the UnSplash API Client ID. [You can get it here](https://unsplash.com/login)

* #### In Android Studio, open the Terminal. At the prompt, navigate into your "AndroidStudioProjects" folder.



Once there, run the following command.
```
git clone https://github.com/XiXiaPdx/XiXiaAndroidProject.git
```
*  #### Navigate into the project folder
```
cd XiXiaAndroidProject
```
*  #### create file gradle.properties to hold your UnSplash Client ID token. I'm using Atom.
```
atom gradle.properties
```
* #### insert the following text into gradle.properties.

```
org.gradle.jvmargs=-Xmx1536m
UnSplashId = "Your Client ID From UnSplash Goes Here!!!"

```
* #### Android Studio should prompt you to Gradle Sync.  At this point, a gradle sync should clear any warnings and the whole project will gradle build.

* #### Run the app in the emulator. I am emulating a Nexus 6.

### Login Details


## Further Exploration



## License

Copyright (c) 2017 Xi Xia. This software is licensed under the MIT license.
