package dev.remake1.tchighwaysigns.network.packets.stc;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.block.HighwaySignBlock.ResizeResult;
import dev.remake1.tchighwaysigns.client.ClientWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Tells the client whether an update of a highway sign was applied.
 */
public record HighwaySignResultPacket(BlockPos pos, ResizeResult result) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HighwaySignResultPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(HighwaySigns.MOD_ID, "highway_sign_result"));
    public static final StreamCodec<FriendlyByteBuf, HighwaySignResultPacket> STREAM_CODEC = StreamCodec.ofMember(HighwaySignResultPacket::encode, HighwaySignResultPacket::decode);

    public HighwaySignResultPacket {
        result = result == null ? ResizeResult.INVALID : result;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeByte(result.ordinal());
    }

    private static HighwaySignResultPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int index = buf.readByte();
        ResizeResult result = index >= 0 && index < ResizeResult.values().length ? ResizeResult.values()[index] : ResizeResult.INVALID;
        return new HighwaySignResultPacket(pos, result);
    }

    public static void handle(HighwaySignResultPacket packet, PacketContext context) {
        // Hops to the client thread itself.
        ClientWrapper.handleHighwaySignResult(packet.pos, packet.result);
    }
}
