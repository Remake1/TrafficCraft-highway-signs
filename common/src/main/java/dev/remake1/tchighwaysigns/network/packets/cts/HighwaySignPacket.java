package dev.remake1.tchighwaysigns.network.packets.cts;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.block.HighwaySignBlock;
import dev.remake1.tchighwaysigns.block.HighwaySignBlock.ResizeResult;
import dev.remake1.tchighwaysigns.block.entity.HighwaySignBlockEntity;
import dev.remake1.tchighwaysigns.data.HighwaySignData;
import dev.remake1.tchighwaysigns.network.packets.stc.HighwaySignResultPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Sent by the highway sign editor to apply new content (and size) to a highway sign.
 * The server answers with a {@link HighwaySignResultPacket}.
 * <p>
 * This is deliberately not a request/response packet: the world has to be modified on the server thread, and
 * blocking the network handler while waiting for the server thread can stall the server (and time out).
 * </p>
 */
public record HighwaySignPacket(BlockPos pos, HighwaySignData data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HighwaySignPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(HighwaySigns.MOD_ID, "highway_sign"));
    public static final StreamCodec<FriendlyByteBuf, HighwaySignPacket> STREAM_CODEC = StreamCodec.ofMember(HighwaySignPacket::encode, HighwaySignPacket::decode);

    /** Max distance between the player and the sign anchor. */
    private static final double MAX_DISTANCE = 12.0D;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(data.toNbt());
    }

    private static HighwaySignPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundTag nbt = buf.readNbt();
        return new HighwaySignPacket(pos, nbt == null ? null : HighwaySignData.fromNbt(nbt));
    }

    public static void handle(HighwaySignPacket packet, PacketContext context) {
        if (!(context.getPlayer() instanceof ServerPlayer sender) || packet.pos == null || packet.data == null) {
            return;
        }
        MinecraftServer server = sender.getServer();
        if (server == null) {
            return;
        }

        // Runs on the server thread (immediately, if we are already on it). Never blocks the calling thread.
        server.execute(() -> {
            ResizeResult result = apply(sender, packet.pos, packet.data);
            NetworkManager.sendToPlayer(sender, new HighwaySignResultPacket(packet.pos, result));
        });
    }

    private static ResizeResult apply(ServerPlayer sender, BlockPos pos, HighwaySignData data) {
        if (!(sender.level() instanceof ServerLevel level) || !level.isLoaded(pos) || sender.distanceToSqr(Vec3.atCenterOf(pos)) > MAX_DISTANCE * MAX_DISTANCE) {
            return ResizeResult.INVALID;
        }

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof HighwaySignBlock block) || !HighwaySignBlock.isAnchor(state) || !(level.getBlockEntity(pos) instanceof HighwaySignBlockEntity blockEntity)) {
            return ResizeResult.INVALID;
        }

        data.sanitize();
        HighwaySignData current = blockEntity.getData();
        if (current.getWidth() != data.getWidth() || current.getHeight() != data.getHeight() || current.hasBorder() != data.hasBorder()) {
            ResizeResult result = block.applyLayout(level, pos, data.getWidth(), data.getHeight(), data.hasBorder());
            if (result != ResizeResult.OK) {
                return result;
            }
        }
        blockEntity.setData(data);
        return ResizeResult.OK;
    }
}
