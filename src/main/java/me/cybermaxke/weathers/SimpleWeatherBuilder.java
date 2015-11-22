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
package me.cybermaxke.weathers;

import static com.google.common.base.Preconditions.checkState;
import static me.cybermaxke.weathers.util.Conditions.checkNotNullOrEmpty;
import static me.cybermaxke.weathers.util.Conditions.checkPlugin;

import java.util.List;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.registry.CatalogRegistryModule;
import org.spongepowered.common.weather.SpongeWeather;

import com.google.common.collect.Lists;

import me.cybermaxke.weathers.api.WeatherBuilder;
import me.cybermaxke.weathers.api.WeatherType;
import me.cybermaxke.weathers.interfaces.IMixinWeatherRegistryModule;
import me.cybermaxke.weathers.interfaces.IMixinWeather;

public final class SimpleWeatherBuilder implements WeatherBuilder {

    private List<String> aliases = Lists.newArrayList();
    private PluginContainer plugin;
    private String name;

    private float rainStrength;
    private float darkness;
    private float lightningRate;
    private float thunderRate;

    @Override
    public WeatherBuilder reset() {
        this.plugin = null;
        this.aliases.clear();
        this.name = null;
        this.rainStrength = 0f;
        this.darkness = 0f;
        this.lightningRate = 0f;
        this.thunderRate = 0f;
        return this;
    }

    @Override
    public WeatherBuilder plugin(Object plugin) {
        this.plugin = checkPlugin(plugin, "plugin");
        return this;
    }

    @Override
    public WeatherBuilder alias(String alias) {
        this.aliases.add(checkNotNullOrEmpty(alias, "alias"));
        return this;
    }

    @Override
    public WeatherBuilder name(String name) {
        this.name = checkNotNullOrEmpty(name, "name");
        return this;
    }

    @Override
    public WeatherBuilder rainStrength(float rainStrength) {
        this.rainStrength = rainStrength;
        return this;
    }

    @Override
    public WeatherBuilder darkness(float darkness) {
        this.darkness = darkness;
        return this;
    }

    @Override
    public WeatherBuilder lightningRate(float lightningRate) {
        this.lightningRate = lightningRate;
        return this;
    }

    @Override
    public WeatherBuilder thunderRate(float thunderRate) {
        this.thunderRate = thunderRate;
        return this;
    }

    @Override
    public WeatherType buildAndRegister() {
        checkState(this.name != null, "name is not set");
        checkState(this.plugin != null, "plugin is not set");
        final CatalogRegistryModule<Weather> module = SpongeImpl.getGame().getRegistry()
                .getRegistryModuleFor(Weather.class);
        final IMixinWeatherRegistryModule module0 = (IMixinWeatherRegistryModule) module;
        checkState(module0.getWeathers().get(this.name) == null, "name is in use");
        final IMixinWeather weather = (IMixinWeather) new SpongeWeather(this.name);
        weather.setCustomIdentifier(this.plugin.getId().toLowerCase() + ":" + this.name);
        weather.setAliases(this.aliases);
        weather.setDarkness(this.darkness);
        weather.setRainStrength(this.rainStrength);
        weather.setLightningRate(this.lightningRate);
        weather.setThunderRate(this.thunderRate);
        module0.registerAdditionalCatalog(weather);
        return weather;
    }
}
