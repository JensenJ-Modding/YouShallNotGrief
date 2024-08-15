package net.youshallnotsteal.data;

import net.minecraft.core.BlockPos;

import java.sql.Timestamp;

public record BlockInteractionData(BlockPos pos, Timestamp time, String blockName, String dimension, String cause, String causeDesc) { }
