package me.aniimalz.plugins;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.Hook;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.patcher.PinePrePatchFn;
import com.aliucord.patcher.PreHook;
import com.aliucord.utils.ReflectUtils;
import com.discord.databinding.WidgetChatListAdapterItemBlockedBinding;
import com.discord.models.member.GuildMember;
import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserRelationships;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemBlocked;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.widgets.chat.overlay.ChatTypingModel$Companion$getTypingUsers$1$1;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@AliucordPlugin
public class HideBlockedMessages extends Plugin {

    @SuppressWarnings("unchecked")
    @Override
    public void start(Context context) throws NoSuchMethodException {
        patcher.patch(WidgetChatListAdapterItemBlocked.class.getDeclaredMethod("onConfigure", int.class, ChatListEntry.class), new Hook(it -> {
            try {
                View root = ((WidgetChatListAdapterItemBlockedBinding) Objects.requireNonNull(ReflectUtils.getField(it.thisObject, "binding"))).getRoot();
                root.setVisibility(View.GONE);
                root.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                logger.error(e);
            }
        }));

        final StoreUserRelationships storeUserRelationships = StoreStream.getUserRelationships();
        patcher.patch(ChatTypingModel$Companion$getTypingUsers$1$1.class.getDeclaredMethod("call", Map.class, Map.class), new PreHook(it -> {
            var h  = ((Map<Long, GuildMember>) it.args[1]).entrySet().stream()
                    .filter(entry -> Objects.requireNonNull(storeUserRelationships.getRelationships().get(entry.getKey())) != 2)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}