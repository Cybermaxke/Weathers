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

import java.util.List;

import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.common.Sponge;
import org.spongepowered.common.registry.CatalogRegistryModule;

import me.cybermaxke.weathers.interfaces.IMixinWeatherRegistryModule;
import me.cybermaxke.weathers.interfaces.IMixinWeather;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandWeather;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;

@Mixin(CommandWeather.class)
public abstract class MixinCommandWeather extends CommandBase {

    private final static String USAGE = "/weather <clear|rain|thunder|...> [duration in seconds]";

    @Overwrite
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return USAGE;
    }

    @Overwrite
    @Override
    public void processCommand(ICommandSender sender, String[] args)
            throws WrongUsageException, NumberInvalidException {
        if (args.length < 1 || args.length > 2) {
            throw new WrongUsageException(USAGE);
        }

        final CatalogRegistryModule<Weather> module = Sponge.getGame().getRegistry()
                .getRegistryModuleFor(Weather.class);
        final IMixinWeatherRegistryModule module0 = (IMixinWeatherRegistryModule) module;
        final IMixinWeather weather = (IMixinWeather) module0.findWeather(args[0]);
        if (weather == null) {
            throw new WrongUsageException(USAGE);
        }

        World world = (World) sender.getEntityWorld();
        if (args.length > 1) {
            world.forecast(weather, parseInt(args[1], 1, 1000000) * 20);
        } else {
            world.forecast(weather);
        }

        notifyOperators(sender, this, weather.getCommandMessage());
    }

    @SuppressWarnings("unchecked")
    @Overwrite
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            final CatalogRegistryModule<Weather> module = Sponge.getGame().getRegistry()
                    .getRegistryModuleFor(Weather.class);
            final IMixinWeatherRegistryModule module0 = (IMixinWeatherRegistryModule) module;
            return getListOfStringsMatchingLastWord(args, module0.getWeatherAliases().keySet());
        }
        return null;
    }
}
