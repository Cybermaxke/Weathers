package me.cybermaxke.weathers.transformer;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public final class WeathersAccessTransformer extends AccessTransformer {

    public WeathersAccessTransformer() throws IOException {
        super("weathers_at.cfg");
    }
}
