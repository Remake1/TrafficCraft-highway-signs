package dev.remake1.tchighwaysigns.block.entity;

import java.util.function.ToIntFunction;

import dev.remake1.tchighwaysigns.block.HighwaySignBlock;
import dev.remake1.tchighwaysigns.client.ClientWrapper;
import dev.remake1.tchighwaysigns.data.HighwaySignData;
import dev.remake1.tchighwaysigns.data.HighwaySignLayout;
import dev.remake1.tchighwaysigns.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Stores the content of a highway sign. Only exists on the anchor cell of a sign.
 */
public class HighwaySignBlockEntity extends BlockEntity {

    private static final String NBT_DATA = "HighwaySign";

    private HighwaySignData data = new HighwaySignData();

    /** Client-side render cache. Reset whenever the data changes. */
    private HighwaySignLayout layout;

    public HighwaySignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HIGHWAY_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    public HighwaySignData getData() {
        return data;
    }

    /** Replaces the content of this sign and syncs it to all clients. Server side only. */
    public void setData(HighwaySignData data) {
        this.data = data;
        this.layout = null;
        notifyUpdate();
    }

    /** @return The cached layout of the sign content. Recomputed only after the data has changed. */
    public HighwaySignLayout getLayout(ToIntFunction<String> textWidth, int lineHeight) {
        if (layout == null) {
            layout = HighwaySignLayout.compute(data, textWidth, lineHeight);
        }
        return layout;
    }

    /** Saves the change and sends the block entity to all clients tracking the chunk. */
    private void notifyUpdate() {
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(getBlockPos());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains(NBT_DATA)) {
            data = HighwaySignData.fromNbt(compound.getCompound(NBT_DATA));
        }
        layout = null;

        if (level != null && level.isClientSide) {
            // The background color is part of the baked block model (tinted), so the chunk has to be rebuilt.
            ClientWrapper.markHighwaySignDirty(level, getBlockPos(), getFacing(), data.getWidth(), data.getHeight());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(NBT_DATA, data.toNbt());
        super.saveAdditional(tag, registries);
    }

    public Direction getFacing() {
        BlockState state = getBlockState();
        return state.hasProperty(HighwaySignBlock.FACING) ? state.getValue(HighwaySignBlock.FACING) : Direction.NORTH;
    }

    /** @return A box containing all cells of this sign. Used to frustum-cull the renderer on NeoForge. */
    public AABB getSignBounds() {
        BlockPos far = HighwaySignBlock.cellPos(getBlockPos(), getFacing(), data.getWidth() - 1, data.getHeight() - 1);
        return new AABB(getBlockPos()).minmax(new AABB(far));
    }
}
