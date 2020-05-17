package com.twilio.video.app.ui.chat;


import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = ChatActivitySubcomponent.class)
public abstract class ChatActivityModule {
    @Binds
    @IntoMap
    @ClassKey(ChatActivity.class)
    abstract AndroidInjector.Factory<?> bindYourActivityInjectorFactory(
            ChatActivitySubcomponent.Factory factory);
}
