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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

/**
 * For now, just testing if we can keep the world dark.
 */
@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World implements IThreadListener {

    private float darkness = 1.5f;

    protected MixinWorldServer(ISaveHandler saveHandler, WorldInfo worldInfo, WorldProvider worldProvider,
            Profiler profiler, boolean client) {
        super(saveHandler, worldInfo, worldProvider, profiler, client);
    }

    @ModifyArg(method = "updateWeather()V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/network/play/server/S2BPacketChangeGameState;<init>(IF)V", ordinal = 0, remap = false))
    private float onGetValue0(int type, float value) {
        return value < 0.01f ? 0.01f : value;
    }

    @ModifyArg(method = "updateWeather()V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/network/play/server/S2BPacketChangeGameState;<init>(IF)V", ordinal = 1, remap = false))
    private float onGetValue1(int type, float value) {
        return this.darkness / (value < 0.01f ? 0.01f : value);
    }

    @ModifyArg(method = "updateWeather()V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/network/play/server/S2BPacketChangeGameState;<init>(IF)V", ordinal = 4, remap = false))
    private float onGetValue4(int type, float value) {
        return value < 0.01f ? 0.01f : value;
    }

    @ModifyArg(method = "updateWeather()V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/network/play/server/S2BPacketChangeGameState;<init>(IF)V", ordinal = 5, remap = false))
    private float onGetValue5(int type, float value) {
        return this.darkness / (value < 0.01f ? 0.01f : value);
    }
}
