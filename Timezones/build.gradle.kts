version = "1.2.3"
description = "Allows you to set a timezone for a user and view their time"

aliucord {
    changelog.set("""
        # 1.2.3
        Show user's local time at sending time in message headers (#35)
        or something... i dont remember havent plugin dev in years

        # 1.2.2
        Fix the silly ahh 00:04 PM thing
        
        # 1.2.1
        * Add bulk fetch for user timezones
            
        # 1.2.0
        * Add caching
        
        # 1.1.9
        * Fix hook for message header
        * Increase max width for message timestamp
        * Cache timeInHeader setting
        * Hide own local time for message header

        # 1.1.8
        * move domains guhhh aa will explode

        # 1.1.7
        * fix padded zeros and ui thread error
        
        # 1.1.6
        * Make plugin compatible with Android 7 & 8
        * Now detect 24h/12h preference from system
        
        # 1.1.5
        * said option will apply if you turn on after plugin starts
        # 1.1.4
        * Added option for user time in message timestamp (looks bad but works)
        
        # 1.1.0
        * Added TimezoneDB support (by mantikafasi)
        
        # 1.0.7
        * Made settings page look slightly nicer
        
        # Announcement
        * I don't plan on adding new features since the base functionality is already here.
        * Feel free to PR if you want.
    """.trimIndent())

    author("mantikafasi", 287555395151593473)
    author("Ven", 343383572805058560)
    author("Diamond", 295190422244950017)
}
