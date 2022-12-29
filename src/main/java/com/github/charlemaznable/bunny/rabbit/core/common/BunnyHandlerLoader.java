package com.github.charlemaznable.bunny.rabbit.core.common;

import com.github.charlemaznable.bunny.plugin.BunnyHandler;

import java.util.List;

@SuppressWarnings("rawtypes")
public interface BunnyHandlerLoader {

    List<BunnyHandler> loadHandlers();
}
