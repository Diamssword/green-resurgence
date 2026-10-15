package com.diamssword.greenresurgence;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ClientProfile extends ConfigWrapper<com.diamssword.greenresurgence.ClientProfileModel> {

    public final Keys keys = new Keys();

    private final Option<com.diamssword.greenresurgence.ClientProfileModel.HoloSettings> holos = this.optionForKey(this.keys.holos);

    private ClientProfile() {
        super(com.diamssword.greenresurgence.ClientProfileModel.class);
    }

    private ClientProfile(Consumer<Jankson.Builder> janksonBuilder) {
        super(com.diamssword.greenresurgence.ClientProfileModel.class, janksonBuilder);
    }

    public static ClientProfile createAndLoad() {
        var wrapper = new ClientProfile();
        wrapper.load();
        return wrapper;
    }

    public static ClientProfile createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new ClientProfile(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public com.diamssword.greenresurgence.ClientProfileModel.HoloSettings holos() {
        return holos.value();
    }

    public void holos(com.diamssword.greenresurgence.ClientProfileModel.HoloSettings value) {
        holos.set(value);
    }


    public static class Keys {
        public final Option.Key holos = new Option.Key("holos");
    }
}

