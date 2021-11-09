package me.aniimalz.plugins;

import android.content.Context;

import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.stores.StoreStream;

@AliucordPlugin
public class LogMeOutHamachi extends Plugin {

    @Override
    public void start(Context ctx) {
        try {
            StoreStream.getAuthentication().logout();
        } catch (Exception ignored) {
        }
        Utils.mainThread.postDelayed(() -> Utils.openPageWithProxy(Utils.appActivity, new Hamachi()), 1100);
    }

    @Override
    public void stop(Context ctx) {
    }
}