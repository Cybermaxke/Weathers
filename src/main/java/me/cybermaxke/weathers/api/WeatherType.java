package me.cybermaxke.weathers.api;

import org.spongepowered.api.world.weather.Weather;

public interface WeatherType extends Weather {

	float getLightningRate();

    float getThunderRate();

    float getDarkness();

    float getRainStrength();
}
