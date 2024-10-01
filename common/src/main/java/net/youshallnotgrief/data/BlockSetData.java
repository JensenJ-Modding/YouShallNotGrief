package net.youshallnotgrief.data;

import net.minecraft.core.BlockPos;

import java.sql.Timestamp;

public record BlockSetData(BlockPos pos, String dimension, Timestamp time, String blockName, String blockID, String action, String source, String sourceDesc) { }
