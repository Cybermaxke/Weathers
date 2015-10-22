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
