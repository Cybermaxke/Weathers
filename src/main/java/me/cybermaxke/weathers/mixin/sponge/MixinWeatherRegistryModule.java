package me.cybermaxke.weathers.mixin.sponge;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import me.cybermaxke.weathers.api.WeatherType;
import me.cybermaxke.weathers.interfaces.IMixinWeather;
import me.cybermaxke.weathers.interfaces.IMixinWeatherRegistryModule;

import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.registry.type.WeatherRegistryModule;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Mixin(WeatherRegistryModule.class)
public abstract class MixinWeatherRegistryModule implements IMixinWeatherRegistryModule {

    @Shadow(remap = false) private Map<String, Weather> weatherMappings;

    private Map<String, WeatherType> weathers;
    private Map<String, WeatherType> weatherAliases;

    @Inject(method = "registerDefaults", at = @At("RETURN"), remap = false)
    private void setWeathers(CallbackInfo ci) {
        // The weathers
        this.weathers = Maps.newHashMap();
        // Sponge has only a lookup by the name of the weather,
        // we will also add the by identifier
        for (Weather weather : this.weatherMappings.values()) {
            this.weathers.put(weather.getId().toLowerCase(), (WeatherType) weather);
            this.weathers.put(weather.getName().toLowerCase(), (WeatherType) weather);
        }
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

    @Override
    @Overwrite
    public Optional<Weather> getById(String id) {
        return Optional.<Weather>ofNullable(this.weathers.get(id));
    }

    @Override
    @Overwrite
    public Collection<Weather> getAll() {
        return ImmutableSet.<Weather>copyOf(this.weathers.values());
    }

    @Override
    public void registerAdditionalCatalog(Weather weatherType) {
        final WeatherType weatherType0 = (WeatherType) checkNotNull(weatherType, "weatherType");
        final String id = weatherType0.getId().toLowerCase();
        checkState(!this.weathers.containsKey(id),
                "identifier is already used: " + weatherType0.getId());
        checkState(!this.weathers.containsKey(weatherType0.getName().toLowerCase()),
                "name is already used: " + weatherType0.getName());
        String name = weatherType.getName().toLowerCase();
        this.weathers.put(id, weatherType0);
        this.weathers.put(name, weatherType0);
        this.weatherAliases.put(id, weatherType0);
        this.weatherAliases.put(name, weatherType0);
        this.addAliases(weatherType0);
    }

    private void addAliases(Weather weather) {
        IMixinWeather weather0 = (IMixinWeather) weather;
        for (String alias : weather0.getAliases()) {
            this.weatherAliases.putIfAbsent(alias.toLowerCase(), weather0);
        }
    }

    @Override
    public Map<String, WeatherType> getWeathers() {
        return ImmutableMap.copyOf(this.weathers);
    }

    @Override
    public Map<String, WeatherType> getWeatherAliases() {
        return ImmutableMap.copyOf(this.weatherAliases);
    }

    @Override
    public WeatherType findWeather(String name) {
        return this.weatherAliases.get(name.toLowerCase());
    }
}
