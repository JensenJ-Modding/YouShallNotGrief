package net.youshallnotgrief.data;

import net.minecraft.core.BlockPos;

import java.sql.Timestamp;

public record BlockSetData(BlockPos pos, Timestamp time, String blockName, String dimension, String cause, String causeDesc) { }
