package dev.remake1.tchighwaysigns.client;

import dev.remake1.tchighwaysigns.block.HighwaySignBlock;
import dev.remake1.tchighwaysigns.block.entity.HighwaySignBlockEntity;
import dev.remake1.tchighwaysigns.client.screen.HighwaySignEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/** Client-only entry points. Only call from code that is guarded by a client side check. */
public class ClientWrapper {

    public static void showHighwaySignEditor(HighwaySignBlockEntity blockEntity) {
        Minecraft.getInstance().setScreen(new HighwaySignEditorScreen(blockEntity));
    }

    /** Called when the server answered an update request of the highway sign editor. May be called from any thread. */
    public static void handleHighwaySignResult(BlockPos anchor, HighwaySignBlock.ResizeResult result) {
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().screen instanceof HighwaySignEditorScreen screen && screen.getAnchor().equals(anchor)) {
                screen.onServerResult(result);
            }
        });
    }

    /** Rebuilds the chunk meshes of all cells of a highway sign, e.g. after its colors changed. */
    @SuppressWarnings("resource")
    public static void markHighwaySignDirty(Level level, BlockPos anchor, Direction facing, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.levelRenderer == null || mc.level == null || mc.level != level) {
            return;
        }
        BlockPos far = HighwaySignBlock.cellPos(anchor, facing, width - 1, height - 1);
        mc.levelRenderer.setBlocksDirty(
            Math.min(anchor.getX(), far.getX()), anchor.getY(), Math.min(anchor.getZ(), far.getZ()),
            Math.max(anchor.getX(), far.getX()), far.getY(), Math.max(anchor.getZ(), far.getZ())
        );
    }
}
