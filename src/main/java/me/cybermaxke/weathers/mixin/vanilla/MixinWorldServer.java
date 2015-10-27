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

import static com.google.common.base.Preconditions.checkNotNull;
import static me.cybermaxke.weathers.WeatherHelper.FADE_SPEED;
import static me.cybermaxke.weathers.WeatherHelper.getRainStrengthValue;
import static me.cybermaxke.weathers.WeatherHelper.getThunderStrengthValue;

import java.util.Collection;
import java.util.List;

import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.Sponge;

import com.google.common.collect.Lists;

import me.cybermaxke.weathers.WeathersPlugin;
import me.cybermaxke.weathers.api.WeatherType;
import me.cybermaxke.weathers.interfaces.IMixinWorld;
import me.cybermaxke.weathers.interfaces.IMixinWorldInfo;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

@Mixin(value = WorldServer.class, priority = 1001)
@Implements(@Interface(iface = org.spongepowered.api.world.World.class, prefix = "sponge$"))
public abstract class MixinWorldServer extends World implements IThreadListener, IMixinWorld {

    @Shadow private MinecraftServer mcServer;

    private float darknessTarget;
    private float darkness;

    private float rainStrengthTarget;
    private float rainStrength;

    protected MixinWorldServer(ISaveHandler saveHandler, WorldInfo worldInfo, WorldProvider worldProvider,
            Profiler profiler, boolean client) {
        super(saveHandler, worldInfo, worldProvider, profiler, client);
    }

    @Override
    public boolean isWeatherOptimal() {
        return Math.abs(this.darknessTarget - this.darkness) <= 0.1f &&
                Math.abs(this.rainStrengthTarget - this.rainStrength) <= 0.1f;
    }

    @Override
    public void initWeatherVolume() {
        WeathersPlugin.log().debug("Initializing weather volume");

        IMixinWorldInfo info = (IMixinWorldInfo) this.worldInfo;
        WeatherType current = info.getWeather();

        this.rainStrength = current.getRainStrength();
        this.rainStrengthTarget = this.rainStrength;

        this.darkness = current.getDarkness();
        this.darknessTarget = this.darkness;
    }

    @Override
    public float getTargetRainStrength() {
        return this.rainStrengthTarget;
    }

    @Override
    public float getRainStrength() {
        return this.rainStrength;
    }

    @Override
    public float getDarkness() {
        return this.darkness;
    }

    @Overwrite
    @Override
    protected void updateWeather() {
        IMixinWorldInfo info = (IMixinWorldInfo) this.worldInfo;

        long duration = info.getWeatherDuration();
        long elapsedDuration = info.getElapsedWeatherDuration();

        if (++elapsedDuration >= duration) {
            this.forecast(this.getRandomWeather(info.getWeather()), true);
        } else {
            if (this.worldInfo.rainTime > 0) {
                this.worldInfo.rainTime--;
            }
            if (this.worldInfo.thunderTime > 0) {
                this.worldInfo.thunderTime--;
            }
            if (this.worldInfo.cleanWeatherTime > 0) {
                this.worldInfo.cleanWeatherTime--;
            }
            info.setElapsedWeatherDuration(elapsedDuration);
        }
        if (this.rainStrength != this.rainStrengthTarget) {
            if (Math.abs(this.rainStrength - this.rainStrengthTarget) < FADE_SPEED) {
                this.rainStrength = this.rainStrengthTarget;
            } else if (this.rainStrength > this.rainStrengthTarget) {
                this.rainStrength -= FADE_SPEED;
            } else {
                this.rainStrength += FADE_SPEED;
            }
            this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(
                    new S2BPacketChangeGameState(7, getRainStrengthValue(this.rainStrength)), this.provider.getDimensionId());
        }
        if (this.darkness != this.darknessTarget) {
            if (Math.abs(this.darkness - this.darknessTarget) < FADE_SPEED) {
                this.darkness = this.darknessTarget;
            } else if (this.darkness > this.darknessTarget) {
                this.darkness -= FADE_SPEED;
            } else {
                this.darkness += FADE_SPEED;
            }
            this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(
                    new S2BPacketChangeGameState(8, getThunderStrengthValue(this.rainStrength, this.darkness)), this.provider.getDimensionId());
        }
    }

    // This will work, but it's a method added by forge.
    // We will try to avoid this to maintain compatible for SpongeVanilla (for the future)
    /*
    @Redirect(method = "updateBlocks()V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/WorldProvider;canDoLightning(Lnet/minecraft/world/chunk/Chunk)Z", ordinal = 0, remap = false))
    private boolean onCanDoLightning(WorldProvider this$0, Chunk chunk) {
        boolean result = this$0.canDoLightning(chunk);
        if (result) {
            float chance = ((IMixinWorldInfo) this.worldInfo).getWeather().getLightningRate();
            if (chance == 0f) {
                result = false;
            }
        }
        return result;
    }
    */

    @ModifyArg(method = "updateBlocks()V", at = @At(value = "INVOKE", target =
            "Ljava/util/Random;nextInt(I)I", ordinal = 0, remap = false))
    private int onCheckLightningChance(int value) {
        float chance = ((IMixinWorldInfo) this.worldInfo).getWeather().getLightningRate();
        // Will be ignored in the next call
        // This is only needed if the above check isn't used
        if (chance == 0f) {
            return 1;
        }
        long result = (long) (1f / chance);
        return (int) Math.min(result, Integer.MAX_VALUE);
    }

    // This is only needed if the other onCanDoLightning check isn't used
    @Redirect(method = "updateBlocks()V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/World;isRaining()Z", ordinal = 0))
    private boolean onCanDoLightning0(World this$0) {
        boolean result = this$0.isRaining();
        if (result) {
            float chance = ((IMixinWorldInfo) this.worldInfo).getWeather().getLightningRate();
            if (chance == 0f) {
                result = false;
            }
        }
        return result;
    }

    @Redirect(method = "updateBlocks()V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/World;isThundering()Z", ordinal = 0))
    private boolean onCanDoLightning1(World this$0) {
        // Just return false, this is already handled by isRaining()
        return true;
    }

    protected Collection<WeatherType> getWeatherTypes() {
        return Sponge.getGame().getRegistry().getAllOf(WeatherType.class);
    }

    private WeatherType getRandomWeather(WeatherType ignore) {
        List<WeatherType> weathers = Lists.newArrayList(this.getWeatherTypes());
        while (weathers.size() > 1) {
            WeatherType next = weathers.remove(this.rand.nextInt(weathers.size()));
            if (next != ignore) {
                return next;
            }
        }
        return weathers.isEmpty() ? ignore : weathers.get(0);
    }

    public long sponge$getRemainingDuration() {
        IMixinWorldInfo info = (IMixinWorldInfo) this.worldInfo;
        return info.getWeatherDuration() - info.getElapsedWeatherDuration();
    }

    public long sponge$getRunningDuration() {
        IMixinWorldInfo info = (IMixinWorldInfo) this.worldInfo;
        return info.getElapsedWeatherDuration();
    }

    public Weather sponge$getWeather() {
        return ((IMixinWorldInfo) this.worldInfo).getWeather();
    }

    public void sponge$forecast(Weather weather) {
        this.forecast(weather, false);
    }

    public void forecast(Weather weather, boolean event) {
        this.forecast(weather, (300 + this.rand.nextInt(600)) * 20, event);
    }

    public void sponge$forecast(Weather weather, long duration) {
        this.forecast(weather, duration, false);
    }

    public void forecast(Weather weather0, long duration0, boolean event) {
        IMixinWorldInfo info = (IMixinWorldInfo) this.worldInfo;

        WeatherType weather = (WeatherType) checkNotNull(weather0, "weather");
        WeatherType current = info.getWeather();

        int duration = (int) Math.min(Integer.MAX_VALUE, duration0);
        if (event) {
            ChangeWorldWeatherEvent weatherEvent = SpongeEventFactory.createChangeWorldWeatherEvent(Sponge.getGame(),
                    Cause.empty(), duration, duration, weather, weather, weather, (org.spongepowered.api.world.World) this);
            Sponge.getGame().getEventManager().post(weatherEvent);
            weather = (WeatherType) weatherEvent.getWeather();
            if (weatherEvent.isCancelled()) {
                weather = current;
            }
            duration = weatherEvent.getDuration();
            duration0 = duration;
        }

        WeathersPlugin.log().debug("Forecast weather: {} with duration: {}", weather.getName(), duration0);

        boolean rain = weather.getRainStrength() > 0f;
        boolean thunder = weather.getThunderRate() > 0f;

        this.worldInfo.raining = rain;
        this.worldInfo.rainTime = rain ? duration : 0;
        this.worldInfo.thundering = thunder;
        this.worldInfo.thunderTime = thunder ? duration : 0;
        this.worldInfo.cleanWeatherTime = (rain || thunder) ? 0 : duration;

        long elapsed = 0;
        if (weather == current) {
            elapsed = info.getElapsedWeatherDuration();
            duration0 = elapsed + duration0;
        } else {
            this.rainStrengthTarget = weather.getRainStrength();
            this.darknessTarget = weather.getDarkness();
        }

        info.setWeather(weather);
        info.setElapsedWeatherDuration(elapsed);
        info.setWeatherDuration(duration0);
    }
}
