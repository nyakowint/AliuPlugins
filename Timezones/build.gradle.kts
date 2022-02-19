version = "1.1.6"
description = "Allows you to set a timezone for a user and view their time"

aliucord {
    changelog.set("""
        # 1.1.6
        * Make plugin compatible with Android 7 & 8
        * Now detect 24h/12h preference from system
        
        # 1.1.5
        - said option will apply if you turn on after plugin starts
        # 1.1.4
        - Added option for user time in message timestamp (looks bad but works)
        
        # 1.1.0
        - Added TimezoneDB support (by mantikafasi)
        
        # 1.0.7
        - Made settings page look slightly nicer
        
        # Announcement
        I don't plan on adding new features since the base functionality is already here.
        Feel free to PR if you want.
    """.trimIndent())
    author("mantikafasi",287555395151593473)
}