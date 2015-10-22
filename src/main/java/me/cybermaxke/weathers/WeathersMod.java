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
