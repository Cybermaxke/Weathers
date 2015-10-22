package me.cybermaxke.weathers.mixin.vanilla;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld implements IBlockAccess {

}
