package dev.remake1.tchighwaysigns.block;

import de.mrjulsen.trafficcraft.block.TrafficSignPostBlock;
import de.mrjulsen.trafficcraft.block.data.ITrafficPostLike;
import dev.remake1.tchighwaysigns.registry.ModTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Decides which blocks a highway sign panel can be mounted on, without requiring any API in TrafficCraft.
 */
public final class PanelMounts {

    private PanelMounts() {}

    /**
     * @param behind The block directly behind the panel.
     * @param panelFacing The direction the front of the panel faces. The panel is on the {@code panelFacing} side of {@code behind}.
     * @return {@code true} if a panel facing {@code panelFacing} can be mounted on {@code behind}.
     */
    public static boolean canMount(BlockState behind, Direction panelFacing) {
        if (!panelFacing.getAxis().isHorizontal()) {
            return false;
        }
        if (behind.is(ModTags.PANEL_MOUNTS)) {
            return true;
        }
        if (behind.getBlock() instanceof TrafficSignPostBlock) {
            // Vertical posts accept panels on every horizontal side, horizontal posts (gantries) on the two sides perpendicular to their axis.
            return panelFacing.getAxis() != behind.getValue(TrafficSignPostBlock.AXIS);
        }
        return behind.getBlock() instanceof ITrafficPostLike postLike && postLike.canAttach(behind, null, panelFacing);
    }
}
