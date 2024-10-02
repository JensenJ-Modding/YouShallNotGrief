package net.youshallnotgrief.data.block;

import java.sql.Timestamp;

public record BlockSetData(BlockSetPosData blockSetPosData, Timestamp time, BlockSetBlockData blockSetBlockData, BlockSetAction action, BlockSetSourceData blockSetSourceData) { }
