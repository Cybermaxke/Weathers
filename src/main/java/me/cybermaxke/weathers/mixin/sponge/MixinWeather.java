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
