package com.github.charlemaznable.bunny.rabbit.guice;

import com.github.charlemaznable.bunny.rabbit.dao.BunnyCallbackDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyLogDao;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyServeDao;
import com.github.charlemaznable.core.lang.Clz;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import lombok.val;

import java.util.List;
import java.util.Map;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.google.inject.Scopes.SINGLETON;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class BunnyEqlerModuleBuilder {

    private static final List<Class<?>> daoClasses = newArrayList(
            BunnyLogDao.class, BunnyDao.class, BunnyServeDao.class, BunnyCallbackDao.class);
    private final Map<Class, Provider> bindProviderMap = newHashMap();
    private final Map<Class, Class> bindImplementMap = newHashMap();

    public BunnyEqlerModuleBuilder bind(Class clazz, Object impl) {
        if (clazz.isAssignableFrom(impl.getClass())) {
            bindProviderMap.put(clazz, Providers.of(impl));
        }
        return this;
    }

    public BunnyEqlerModuleBuilder bind(Class clazz, Class ext) {
        if (clazz.isAssignableFrom(ext) && Clz.isConcrete(ext)) {
            bindImplementMap.put(clazz, ext);
        }
        return this;
    }

    public Module build() {
        Map<Class, Provider> defaultMap = newHashMap();
        for (val daoClass : daoClasses) {
            if (bindProviderMap.containsKey(daoClass) ||
                    bindImplementMap.containsKey(daoClass)) continue;
            defaultMap.put(daoClass, Providers.of(null));
        }
        return new AbstractModule() {
            @Override
            protected void configure() {
                for (val providerEntry : bindProviderMap.entrySet()) {
                    bind(providerEntry.getKey()).toProvider(providerEntry.getValue());
                }
                for (val providerEntry : bindImplementMap.entrySet()) {
                    bind(providerEntry.getKey()).to(providerEntry.getValue()).in(SINGLETON);
                }
                for (val providerEntry : defaultMap.entrySet()) {
                    bind(providerEntry.getKey()).toProvider(providerEntry.getValue());
                }
            }
        };
    }
}
