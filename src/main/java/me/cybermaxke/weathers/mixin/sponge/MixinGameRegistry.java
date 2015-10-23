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

import java.util.Map;

import me.cybermaxke.weathers.api.WeatherType;
import me.cybermaxke.weathers.interfaces.IMixinGameRegistry;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.common.registry.SpongeGameRegistry;

import com.google.common.collect.ImmutableMap;

@Mixin(value = SpongeGameRegistry.class, remap = false)
public abstract class MixinGameRegistry implements GameRegistry, IMixinGameRegistry {

    @Shadow(remap = false) private Map<Class<? extends CatalogType>, Map<String, ? extends CatalogType>> catalogTypeMap;
    @Shadow(remap = false) private Map<String, Weather> weatherMappings;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void onInit() {
        ImmutableMap.Builder<Class<? extends CatalogType>, Map<String, ? extends CatalogType>> builder = ImmutableMap.builder();
        builder.putAll(this.catalogTypeMap);
        builder.put(WeatherType.class, this.weatherMappings);
        this.catalogTypeMap = builder.build();
    }

    @Override
    public void registerWeather(WeatherType weatherType) {
        checkNotNull(weatherType, "weatherType");
        checkState(this.weatherMappings.containsKey(weatherType.getId()),
                "identifier is already used: " + weatherType.getId());
        this.weatherMappings.put(weatherType.getId(), weatherType);
    }
}
