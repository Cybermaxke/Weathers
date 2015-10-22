package me.cybermaxke.weathers;

import java.io.File;

import org.spongepowered.api.world.weather.Weathers;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import me.cybermaxke.weathers.api.WeatherType;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

public final class WeathersMod extends DummyModContainer {

	public final static String PLUGIN_ID = "Weathers";
	public final static String PLUGIN_VERSION = "dev-SNAPSHOT";

	public WeathersMod() {
		super(MetadataCollection.from(WeathersMod.class.getResourceAsStream("/mcmod.info"), PLUGIN_ID)
				.getMetadataForId(PLUGIN_ID, ImmutableMap.<String, Object>of(
						"modid", PLUGIN_ID, "version", PLUGIN_VERSION)));
	}

	@Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

	@Override
    public File getSource() {
        return WeathersCore.sourceLocation;
    }

	@Subscribe
    public void onPreInitialization(FMLPreInitializationEvent e) {
		System.out.println("Loading " + PLUGIN_ID + "...");
	}

	@Subscribe
    public void onServerStartedEvent(FMLServerStartedEvent e) {
		System.out.println("Weathers.CLEAR: " + ((WeatherType) Weathers.CLEAR).getDarkness());
	}
}
