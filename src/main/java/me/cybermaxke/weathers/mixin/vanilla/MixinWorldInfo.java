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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weathers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.cybermaxke.weathers.WeathersInfo;
import me.cybermaxke.weathers.api.WeatherType;
import me.cybermaxke.weathers.interfaces.IMixinWorldInfo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;

@Mixin(value = WorldInfo.class, priority = 1001)
public abstract class MixinWorldInfo implements IMixinWorldInfo {

    @Shadow private boolean thundering;
    @Shadow private boolean raining;
    @Shadow private int thunderTime;
    @Shadow private int rainTime;
    @Shadow private int cleanWeatherTime;

    private World world;
    private WeatherType weather;

    private long elapsedWeatherDuration;
    private long weatherDuration;

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onInit(NBTTagCompound nbt, CallbackInfo ci) {
        if (nbt.hasKey(WeathersInfo.NAME)) {
            final NBTTagCompound info = nbt.getCompoundTag(WeathersInfo.NAME);
            this.weather =  Sponge.getGame().getRegistry().getType(
                    WeatherType.class, info.getString("type")).orElse(null);
            this.elapsedWeatherDuration = info.getLong("elapsed");
            this.weatherDuration = info.getLong("duration");
        }
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onInit(WorldInfo info0, CallbackInfo ci) {
        final IMixinWorldInfo info = (IMixinWorldInfo) info0;
        this.weather = info.getWeather();
        this.elapsedWeatherDuration = info.getElapsedWeatherDuration();
        this.weatherDuration = info.getWeatherDuration();
    }

    @Inject(method = "updateTagCompound", at = @At("RETURN"))
    private void onSave(NBTTagCompound nbt, NBTTagCompound playerNbt, CallbackInfo ci) {
        final NBTTagCompound data = new NBTTagCompound();
        data.setString("type", this.weather.getId());
        data.setLong("elapsed", this.elapsedWeatherDuration);
        data.setLong("duration", this.weatherDuration);
        nbt.setTag(WeathersInfo.NAME, data);
    }

    @Overwrite
    public void setCleanWeatherTime(int time) {
        if (this.world != null) {
            if (time > 0) {
                this.world.forecast(Weathers.CLEAR, time);
            }
        } else {
            this.cleanWeatherTime = time;
        }
    }

    @Overwrite
    public void setRainTime(int time) {
        if (this.world != null) {
            this.raining = time > 0;
            if (this.thundering && this.thunderTime > 0 && this.raining) {
                this.world.forecast(Weathers.THUNDER_STORM, time);
            } else if (this.raining) {
                this.world.forecast(Weathers.RAIN, time);
            } else {
                this.world.forecast(Weathers.CLEAR, this.cleanWeatherTime);
            }
        } else {
            this.rainTime = time;
        }
    }

    @Overwrite
    public void setThunderTime(int time) {
        if (this.world != null) {
            this.thundering = time > 0;
            if (this.thundering) {
                this.world.forecast(Weathers.THUNDER_STORM, time);
            } else if (this.raining && this.rainTime > 0) {
                this.world.forecast(Weathers.RAIN, this.rainTime);
            } else {
                this.world.forecast(Weathers.CLEAR, this.cleanWeatherTime);
            }
        } else {
            this.thunderTime = time;
        }
    }

    @Overwrite
    public void setThundering(boolean flag) {
        if (this.world != null) {
            if (!flag) {
                if (this.raining && this.rainTime > 0) {
                    this.world.forecast(Weathers.RAIN, this.rainTime);
                } else {
                    this.world.forecast(Weathers.CLEAR, this.cleanWeatherTime);
                }
            }
        } else {
            this.raining = flag;
        }
    }

    @Overwrite
    public void setRaining(boolean flag) {
        if (this.world != null) {
            if (!flag) {
                this.world.forecast(Weathers.CLEAR, this.cleanWeatherTime);
            }
        } else {
            this.raining = flag;
        }
    }

    @Override
    public void setWeather(WeatherType weatherType) {
        this.weather = weatherType;
    }

    @Override
    public WeatherType getWeather() {
        return this.weather;
    }

    @Override
    public long getWeatherDuration() {
        return this.weatherDuration;
    }

    @Override
    public void setWeatherDuration(long duration) {
        this.weatherDuration = duration;
    }

    @Override
    public long getElapsedWeatherDuration() {
        return this.elapsedWeatherDuration;
    }

    @Override
    public void setElapsedWeatherDuration(long duration) {
        this.elapsedWeatherDuration = duration;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }
}
