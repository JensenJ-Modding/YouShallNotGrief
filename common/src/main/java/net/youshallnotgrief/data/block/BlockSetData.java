package net.youshallnotgrief.data.block;

import net.minecraft.core.BlockPos;

import java.sql.Timestamp;

public record BlockSetData(BlockPos pos, String dimension, Timestamp time, String oldBlock, String newBlock, String cause, String source, String sourceDesc) { }
