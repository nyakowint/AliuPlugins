### Plugins for [Aliucord](https://github.com/Aliucord) Kotlin

this is where the original fart plugin began

# THESE PLUGINS ARE NOT ACTIVELY MAINTAINED -

### New plugins for AliucordRN are [here](https://github.com/ItzOnlyAnimal/AliuPloogins)

### How to install (may no longer work):

1) Join the [Aliucord](https://discord.gg/aliucord) support server
2) long press on my message
   in [#plugins-list](https://discord.com/channels/811255666990907402/811275162715553823/896571282483728444)
   then click "Open PluginDownloader"

How to install (will always work):
Download `[plugin].zip` from the builds branch and move to your Aliucord plugins folder (
usually `/sdcard/Aliucord/plugins`)

## How to add new icons (for when discord adds more pointless connections):

- Fork this repo
1) Get a Vector Drawable of the connection's icon, the easiest way is just stealing it from the
   Desktop app with inspect element
    - If needed add "discord.com" to get the asset e.g "
      discord.com/assets/6a853b4c87fce386cbfef4a2efbacb09.svg"
    - Convert SVGs to Vector Drawables with an online tool or Android Studio (preferred - resize to
      24dp) or PNGs
2) Add the icon to `UnknownConnectionIcons/src/main/res/drawable`
   2.5) Add the light theme compatible icon to `[NAME]_light`, replacing `[NAME]` with the
   connection name Discord uses
    - If there isn't one, just copy and paste your icon and rename it

3) Please increment the version number inside `UnknownConnectionIcons/build.gradle.kts`

You are technically done, but if the connection has a URL (indicated by an open link button on
desktop/rn), you might want to continue following along

- Add the connection name and URL to the getUrl function in `UnknownConnectionIcons.kt`. Example
   below:
    ```kotlin
        return when (account.g()) {
            "ebay" -> "https://ebay.com/usr/${account.d()}"
            "instagram" -> "https://instagram.com/${account.d()}"
            "tiktok" -> "https://tiktok.com/@${account.d()}"
            "twitter" -> "https://twitter.com/${account.d()}"
            "CONNECTION_NAME" -> "https://CONNECTION_URL/${account.d()}"
            else -> null
        }
   ```

4) Create a pull request!
