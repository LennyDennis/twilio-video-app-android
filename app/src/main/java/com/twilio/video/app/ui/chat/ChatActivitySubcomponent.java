package com.twilio.video.app.ui.chat;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface ChatActivitySubcomponent extends AndroidInjector<ChatActivity> {
    @Subcomponent.Factory
    interface Factory extends AndroidInjector.Factory<ChatActivity> {}
}