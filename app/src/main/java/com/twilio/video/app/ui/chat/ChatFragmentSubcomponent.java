package com.twilio.video.app.ui.chat;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface ChatFragmentSubcomponent extends AndroidInjector<ChatActivity.ChatFragment> {
    @Subcomponent.Factory
    interface Factory extends AndroidInjector.Factory<ChatActivity.ChatFragment> {}
}