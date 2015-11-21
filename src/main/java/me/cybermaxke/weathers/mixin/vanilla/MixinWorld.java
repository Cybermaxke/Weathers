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
package me.cybermaxke.weathers.mixin.vanilla;

import java.util.Random;

import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.cybermaxke.weathers.api.WeatherType;
import me.cybermaxke.weathers.interfaces.IMixinWorld;
import me.cybermaxke.weathers.interfaces.IMixinWorldInfo;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.storage.WorldInfo;

@Mixin(value = net.minecraft.world.World.class, priority = 1001)
public abstract class MixinWorld implements IBlockAccess, World, IMixinWorld {

    @Shadow private Random rand;
    @Shadow private WorldInfo worldInfo;
    @Shadow private boolean isRemote;

    @Overwrite
    protected void calculateInitialWeather() {
        final IMixinWorldInfo info = (IMixinWorldInfo) this.worldInfo;
        info.setWorld(this);
        if (info.getWeather() == null) {
            final Weather weather;
            int duration;
            int rainTime = this.worldInfo.raining ? this.worldInfo.getRainTime() : 0;
            int thunderTime = this.worldInfo.thundering ? this.worldInfo.thunderTime : 0;
            this.worldInfo.thunderTime = thunderTime;
            this.worldInfo.rainTime = rainTime;
            if (rainTime <= 0) {
                weather = Weathers.CLEAR;
                duration = this.worldInfo.getCleanWeatherTime();
            } else if (thunderTime > 0) {
                weather = Weathers.THUNDER_STORM;
                duration = Math.min(rainTime, thunderTime);
            } else {
                weather = Weathers.RAIN;
                duration = rainTime;
            }
            info.setWeather((WeatherType) weather);
            info.setWeatherDuration(duration);
            info.setElapsedWeatherDuration(0);
        }
        this.initWeatherVolume();
    }

    @Inject(method = "isRaining()Z", at = @At("HEAD"), cancellable = true)
    private void onIsRaining(CallbackInfoReturnable<Boolean> ci) {
        if (!this.isRemote) {
            ci.setReturnValue(this.getRainStrength() > 0.2f);
        }
    }

    @Inject(method = "isThundering()Z", at = @At("HEAD"), cancellable = true)
    private void onIsThundering(CallbackInfoReturnable<Boolean> ci) {
        if (!this.isRemote) {
            final IMixinWorldInfo info = (IMixinWorldInfo) this.worldInfo;
            ci.setReturnValue(info.getWeather().getThunderRate() > 0f && this.isWeatherOptimal());
        }
    }

    @Inject(method = "getThunderStrength(F)F", at = @At("HEAD"), cancellable = true)
    private void onGetThunderStrength(float delta, CallbackInfoReturnable<Float> ci) {
        if (!this.isRemote) {
            ci.setReturnValue(this.getDarkness());
        }
    }

    @Inject(method = "getRainStrength(F)F", at = @At("HEAD"), cancellable = true)
    private void onGetRainStrength(float delta, CallbackInfoReturnable<Float> ci) {
        if (!this.isRemote) {
            ci.setReturnValue(this.getRainStrength());
        }
    }

    /**
     * Make sure that it won't go under zero, this can happen if the darkness < 0
     */
    @Inject(method = "calculateSkylightSubtracted(F)I", at = @At("RETURN"), cancellable = true)
    private void onCalculateSkylightSubtracted(float delta, CallbackInfoReturnable<Integer> ci) {
        int value = ci.getReturnValueI();
        if (value < 0) {
            ci.setReturnValue(0);
        }
    }
}
