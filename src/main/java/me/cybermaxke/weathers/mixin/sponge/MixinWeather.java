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

import me.cybermaxke.weathers.interfaces.IMixinWeather;

import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.common.weather.SpongeWeather;

@Mixin(value = SpongeWeather.class, remap = false)
public abstract class MixinWeather implements Weather, IMixinWeather {

    private float lightningRate;
    private float thunderRate;
    private float darkness;
    private float rainStrength;

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
