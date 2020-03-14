<img align="left" src="https://raw.githubusercontent.com/wilburt/Jade-Player/master/images/icon.svg?sanitize=true" width="50px">

# Jade Player

<p>
    <img src="https://raw.githubusercontent.com/wilburt/Jade-Player/master/images/explore.jpg" width="150px" height="auto" hspace="5" vspace="20"/>
    <img src="https://raw.githubusercontent.com/wilburt/Jade-Player/master/images/songs.jpg" width="150px" height="auto" hspace="5" vspace="20"/>
   <img src="https://raw.githubusercontent.com/wilburt/Jade-Player/master/images/current.jpg" width="150px" height="auto" hspace="5" vspace="20"/>
   <img src="https://raw.githubusercontent.com/wilburt/Jade-Player/master/images/player.jpg" width="150px" height="auto" hspace="5" vspace="20"/>
   <img src="https://raw.githubusercontent.com/wilburt/Jade-Player/master/images/album.jpg" width="150px" height="auto" hspace="5" vspace="20"/>
</p>

Jade Player is a media player for Android. The goal is to implement a local audio & video playback while doing online radio streaming. However, only audio playback has been fully implemented. I also hope to integrate lyrics and sound identification

## Design
- MVVM architecture
- Used HTTP for REST calls and Glide custom loaders for image loading
- Fetched album and artist images from Spotify and Last.fm
- Employed Jetpack Components like  Data Binding, LiveData, Navigation, Room (for storing recently saved tracks)
- Used Timber for logging and Firebase Crashlytics for crash data collection
- I Applied ExoPlayer and I borrowed heavily from [Universal Android Music Player](https://github.com/android/uamp) for playback management
- Used Koin for service locator

## Building
1. Clone the repo to your local machine
2. Spin up a Firebase project and start a collection named "properties" in Cloud Firestore. Then add a document named "keys" to it. Finally add the following field-value pairs:

Field | Value
--- | ---
acrHost| "VALUE"
acrKey|"VALUE"
acrKeyFile|"VALUE"
acrSecret|"VALUE"
acrSecretFile|"VALUE"
lastFmKey|"VALUE"
spotifyClientId|"VALUE"
spotifySecret|"VALUE"

Please, remember to use a rule that allows unauthenticated reads.

3. Add the app to the Firebase project and save `google-services.json` to *PROJECT/app* folder
4. Open and run the the project with Android Studio 3.x

## Contribution
Feature requests, issues and pull requests are welcome.

## Credits
**Jerryson Ibe**: UI/UX Designer ( [Dribble](https://dribbble.com/Jerryboy) | [Behance](https://www.behance.net/Jerrysonibe))

**Isma'il Ahmad**: Graphic Artist ( [Dribble](https://dribbble.com/theIsmailAh) | [Behance](https://www.behance.net/theIsmailAh) | [Instagram](https://www.behance.net/theIsmailAh))
