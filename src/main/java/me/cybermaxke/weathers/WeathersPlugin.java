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

import static me.cybermaxke.weathers.WeathersInfo.NAME;
import static me.cybermaxke.weathers.WeathersInfo.VERSION;
import me.cybermaxke.weathers.api.WeatherService;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;

import com.google.inject.Inject;

@Plugin(id = NAME, name = NAME, version = VERSION)
public final class WeathersPlugin {

    private static WeathersPlugin instance;

    /**
     * Gets the {@link Logger} of the plugin.
     * 
     * @return the logger
     */
    public static Logger log() {
        return instance.logger;
    }

    /**
     * Gets the {@link Game}.
     * 
     * @return the game
     */
    public static Game game() {
        return instance.game;
    }

    @Inject private Logger logger;
    @Inject private Game game;

    private WeatherService weatherService;

    @Inject
    private WeathersPlugin() {
        instance = this;
    }

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent e) {
        this.logger.info("Loading...");

        // Create the weather service
        this.weatherService = new SimpleWeatherService();

        // Set the weather service
        try {
            this.game.getServiceManager().setProvider(this, WeatherService.class, this.weatherService);
        } catch (ProviderExistsException ex) {
            throw new IllegalStateException("The WeatherService is overriden by an unknown source.", ex);
        }
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent e) {
        // Register some test weathers
        this.weatherService.createBuilder().plugin(this).name("mizzle")
                .darkness(0.1f).rainStrength(0.2f).buildAndRegister();
        this.weatherService.createBuilder().plugin(this).name("drizzle")
                .darkness(0.13f).rainStrength(0.3f).buildAndRegister();
        this.weatherService.createBuilder().plugin(this).name("light_rain")
                .darkness(0.2f).rainStrength(0.6f).buildAndRegister();
        this.weatherService.createBuilder().plugin(this).name("medium_rain")
                .darkness(0.4f).rainStrength(1.0f).buildAndRegister();
        this.weatherService.createBuilder().plugin(this).name("heavy_rain")
                .darkness(0.5f).rainStrength(1.3f).buildAndRegister();
        this.weatherService.createBuilder().plugin(this).name("storm")
                .darkness(1.0f).rainStrength(1.0f).lightningRate(0.00003f).buildAndRegister();
        this.weatherService.createBuilder().plugin(this).name("heavy_storm")
                .darkness(1.3f).rainStrength(1.3f).lightningRate(0.0001f).buildAndRegister();
    }

    @Listener
    public void onGamePostInitialization(GamePostInitializationEvent e) {
    }
}
