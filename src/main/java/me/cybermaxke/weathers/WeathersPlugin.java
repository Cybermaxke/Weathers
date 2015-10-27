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

import org.slf4j.Logger;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

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

    @Inject Logger logger;

    @Inject
    private WeathersPlugin() {
        instance = this;
    }

    @Listener
    public void onGamePreInitialization(GamePreInitializationEvent e) {
        this.logger.info("Loading...");
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent e) {
    }
}
