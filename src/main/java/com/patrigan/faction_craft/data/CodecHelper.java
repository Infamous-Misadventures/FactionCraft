package com.patrigan.faction_craft.data;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;

import java.util.stream.IntStream;

public class CodecHelper {

    public static final Codec<ChunkPos> CHUNKPOS_CODEC = Codec.INT_STREAM.comapFlatMap((p_121967_) -> {
        return Util.fixedSize(p_121967_, 3).map((p_175270_) -> {
            return new ChunkPos(p_175270_[0], p_175270_[1]);
        });
    }, (p_121924_) -> {
        return IntStream.of(p_121924_.x, p_121924_.z);
    }).stable();
}
