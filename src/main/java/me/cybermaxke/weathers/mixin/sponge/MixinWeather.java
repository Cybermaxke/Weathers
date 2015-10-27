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

import java.util.Collection;
import java.util.List;

import me.cybermaxke.weathers.interfaces.IMixinWeather;

import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.common.weather.SpongeWeather;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Mixin(value = SpongeWeather.class, remap = false)
public abstract class MixinWeather implements Weather, IMixinWeather {

    private float lightningRate;
    private float thunderRate;
    private float darkness;
    private float rainStrength;

    private List<String> aliases;
    private String commandMessage;
    private String identifier;

    @Override
    public void setCustomIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Overwrite
    @Override
    public String getId() {
        if (this.identifier != null) {
            return this.identifier;
        } else {
            return "minecraft:" + this.getName();
        }
    }

    @Override
    public String getCommandMessage() {
        if (this.commandMessage == null) {
            this.commandMessage = "Changing to " + this.getName() + " weather";
        }
        return this.commandMessage;
    }

    @Override
    public void setCommandMessage(String message) {
        this.commandMessage = message;
    }

    @Override
    public void setAliases(Collection<String> aliases) {
        this.aliases = Lists.newArrayList(aliases);
    }

    @Override
    public List<String> getAliases() {
        if (this.aliases == null) {
            this.aliases = Lists.newArrayList();
        }
        return ImmutableList.copyOf(this.aliases);
    }

    @Override
    public float getLightningRate() {
        return this.lightningRate;
    }

    @Override
    public float getThunderRate() {
        return this.thunderRate;
    }

    @Override
    public float getDarkness() {
        return this.darkness;
    }

    @Override
    public float getRainStrength() {
        return this.rainStrength;
    }

    @Override
    public void setLightningRate(float rate) {
        this.lightningRate = rate;
    }

    @Override
    public void setThunderRate(float rate) {
        this.thunderRate = rate;
    }

    @Override
    public void setDarkness(float darkness) {
        this.darkness = darkness;
    }

    @Override
    public void setRainStrength(float strength) {
        this.rainStrength = strength;
    }
}
