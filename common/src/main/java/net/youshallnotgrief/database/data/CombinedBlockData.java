package net.youshallnotgrief.database.data;

import net.minecraft.core.BlockPos;

public class CombinedBlockData {

    public BlockData blockData;
    public BlockItemTransactionData blockItemTransactionData;

    public CombinedBlockData(BlockData blockData, BlockItemTransactionData blockItemTransactionData) {
        this.blockData = blockData;
        this.blockItemTransactionData = blockItemTransactionData;
    }

    public CombinedBlockData(BlockPos pos, String dimensionID) {
        this.blockData = new BlockData(pos, dimensionID);
        this.blockItemTransactionData = new BlockItemTransactionData(pos, dimensionID);
    }
}
