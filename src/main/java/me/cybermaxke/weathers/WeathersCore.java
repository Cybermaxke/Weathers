package me.cybermaxke.weathers;

import java.io.File;
import java.util.Map;

import org.spongepowered.asm.mixin.MixinEnvironment;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public final class WeathersCore implements IFMLLoadingPlugin {

	static File sourceLocation;

	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getAccessTransformerClass() {
		return "me.cybermaxke.weathers.transformer.WeathersAccessTransformer";
	}

	@Override
	public String getModContainerClass() {
		return "me.cybermaxke.weathers.WeathersMod";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
        System.out.println("Loading " + WeathersMod.PLUGIN_ID + " mixins...");

		// Retrieve the source location
        sourceLocation = (File) data.get("coremodLocation");

        // Add default mixins
        // The path is CASE SENSITIVE, so be careful
        MixinEnvironment.getDefaultEnvironment().addConfiguration(
        		"mixins." + WeathersMod.PLUGIN_ID.toLowerCase() + ".json");
	}
}
