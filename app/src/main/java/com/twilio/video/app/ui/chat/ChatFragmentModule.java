package com.twilio.video.app.ui.chat;

import com.twilio.video.app.ui.settings.SettingsActivity;
import com.twilio.video.app.ui.settings.SettingsFragmentSubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = ChatFragmentSubcomponent.class)
public abstract class ChatFragmentModule {
    @Binds
    @IntoMap
    @ClassKey(ChatActivity.ChatFragment.class)
    abstract AndroidInjector.Factory<?> bindYourFragmentInjectorFactory(
            ChatFragmentSubcomponent.Factory factory);
}
