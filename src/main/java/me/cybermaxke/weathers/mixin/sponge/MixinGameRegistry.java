/*
 * This file is part of Weathers, licensed under the MIT License (MIT).
 *
 * Copyright (c) Cybermaxke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.cybermaxke.weathers.mixin.sponge;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Map;

import me.cybermaxke.weathers.api.WeatherType;
import me.cybermaxke.weathers.interfaces.IMixinGameRegistry;
import me.cybermaxke.weathers.interfaces.IMixinWeather;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.registry.SpongeGameRegistry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Mixin(value = SpongeGameRegistry.class, remap = false)
public abstract class MixinGameRegistry implements GameRegistry, IMixinGameRegistry {

    @Shadow(remap = false) private Map<Class<? extends CatalogType>, Map<String, ? extends CatalogType>> catalogTypeMap;
    @Shadow(remap = false) private Map<String, Weather> weatherMappings;

    private Map<String, WeatherType> aliases;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "setWeathers", at = @At("RETURN"), remap = false)
    private void setWeathers(CallbackInfo ci) {
        ImmutableMap.Builder<Class<? extends CatalogType>, Map<String, ? extends CatalogType>> builder = ImmutableMap.builder();
        builder.putAll(this.catalogTypeMap);
        builder.put(WeatherType.class, this.weatherMappings);
        this.catalogTypeMap = builder.build();
        // Sponge has only a lookup by the name of the weather,
        // we will also add the by identifier
        for (Weather weather : Sets.newHashSet(this.weatherMappings.values())) {
            this.weatherMappings.put(weather.getId().toLowerCase(), weather);
        }
        // Original id will override the aliases
        this.aliases = Maps.newHashMap((Map) this.weatherMappings);
        // Add some default aliases and command messages
        ((IMixinWeather) Weathers.THUNDER_STORM).setAliases(Lists.newArrayList("storm", "thunder"));
        ((IMixinWeather) Weathers.THUNDER_STORM).setCommandMessage("commands.weather.thunder");
        ((IMixinWeather) Weathers.THUNDER_STORM).setRainStrength(1f);
        ((IMixinWeather) Weathers.THUNDER_STORM).setDarkness(1f);
        // The rate is the chance for every chunk, every tick
        ((IMixinWeather) Weathers.THUNDER_STORM).setLightningRate(0.00001f);
        ((IMixinWeather) Weathers.THUNDER_STORM).setThunderRate(1f);
        ((IMixinWeather) Weathers.CLEAR).setAliases(Lists.newArrayList("sunny"));
        ((IMixinWeather) Weathers.CLEAR).setCommandMessage("commands.weather.clear");
        ((IMixinWeather) Weathers.RAIN).setAliases(Lists.newArrayList("rainy"));
        ((IMixinWeather) Weathers.RAIN).setCommandMessage("commands.weather.rain");
        ((IMixinWeather) Weathers.RAIN).setRainStrength(1f);
        this.addAliases(Weathers.THUNDER_STORM);
        this.addAliases(Weathers.CLEAR);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "getAllOf", at = @At("HEAD"), remap = false, cancellable = true)
    private <T extends CatalogType> void onGetAllOf(Class<T> typeClass, CallbackInfoReturnable<Collection<T>> ci) {
        // The sponge implementation is using a list which will add our weathers twice
        if (checkNotNull(typeClass, "null type class").isAssignableFrom(Weather.class)) {
            ci.setReturnValue((Collection) ImmutableSet.copyOf(this.weatherMappings.values()));
        }
    }

    @Override
    public void registerWeather(WeatherType weatherType) {
        checkNotNull(weatherType, "weatherType");
        String id = weatherType.getId().toLowerCase();
        checkState(this.weatherMappings.containsKey(weatherType.getId()),
                "identifier is already used: " + weatherType.getId());
        String name = weatherType.getName().toLowerCase();
        this.weatherMappings.put(id, weatherType);
        this.weatherMappings.put(name, weatherType);
        this.aliases.put(id, weatherType);
        this.aliases.put(name, weatherType);
        this.addAliases(weatherType);
    }

    private void addAliases(Weather weather) {
        IMixinWeather weather0 = (IMixinWeather) weather;
        for (String alias : weather0.getAliases()) {
            this.aliases.putIfAbsent(alias.toLowerCase(), weather0);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String, WeatherType> getWeathers() {
        return ImmutableMap.copyOf((Map) this.weatherMappings);
    }

    @Override
    public Map<String, WeatherType> getWeatherAliases() {
        return ImmutableMap.copyOf(this.aliases);
    }

    @Override
    public WeatherType findWeather(String name) {
        return this.aliases.get(name.toLowerCase());
    }
}
