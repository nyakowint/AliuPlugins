package me.aniimalz.plugins;

import android.content.Context;

import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;

@AliucordPlugin
class Jemplate extends Plugin {

    @Override
    public void start(Context ctx) {

    }

    @Override
    public void stop(Context ctx) {
        patcher.unpatchAll();
        commands.unregisterAll();
    }
}