package dev.remake1.tchighwaysigns.registry;

import dev.architectury.networking.NetworkChannel;
import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.network.packets.cts.HighwaySignPacket;
import dev.remake1.tchighwaysigns.network.packets.stc.HighwaySignResultPacket;
import net.minecraft.resources.ResourceLocation;

public final class ModNetworkManager {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(HighwaySigns.MOD_ID, "network"));

    private ModNetworkManager() {}

    public static void init() {
        CHANNEL.register(HighwaySignPacket.class, HighwaySignPacket::encode, HighwaySignPacket::decode, HighwaySignPacket::handle);
        CHANNEL.register(HighwaySignResultPacket.class, HighwaySignResultPacket::encode, HighwaySignResultPacket::decode, HighwaySignResultPacket::handle);
    }
}
