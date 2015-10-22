package me.cybermaxke.weathers.interfaces;

import me.cybermaxke.weathers.api.WeatherType;

public interface IMixinWeather extends WeatherType {

	void setLightningRate(float rate);

	void setThunderRate(float rate);

	void setDarkness(float darkness);

	void setRainStrength(float rainStrength);
}
