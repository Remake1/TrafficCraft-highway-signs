package dev.remake1.tchighwaysigns.network.packets.stc;

import java.util.function.Supplier;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.remake1.tchighwaysigns.block.HighwaySignBlock.ResizeResult;
import dev.remake1.tchighwaysigns.client.ClientWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Tells the client whether an update of a highway sign was applied.
 */
public class HighwaySignResultPacket {

    private final BlockPos pos;
    private final ResizeResult result;

    public HighwaySignResultPacket(BlockPos pos, ResizeResult result) {
        this.pos = pos;
        this.result = result == null ? ResizeResult.INVALID : result;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeByte(result.ordinal());
    }

    public static HighwaySignResultPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int index = buf.readByte();
        ResizeResult result = index >= 0 && index < ResizeResult.values().length ? ResizeResult.values()[index] : ResizeResult.INVALID;
        return new HighwaySignResultPacket(pos, result);
    }

    public static void handle(HighwaySignResultPacket packet, Supplier<PacketContext> context) {
        // Hops to the client thread itself.
        ClientWrapper.handleHighwaySignResult(packet.pos, packet.result);
    }
}
