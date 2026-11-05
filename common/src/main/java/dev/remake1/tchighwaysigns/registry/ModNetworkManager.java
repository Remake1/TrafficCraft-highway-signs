package dev.remake1.tchighwaysigns.registry;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.remake1.tchighwaysigns.network.packets.cts.HighwaySignPacket;
import dev.remake1.tchighwaysigns.network.packets.stc.HighwaySignResultPacket;

public final class ModNetworkManager {

    private ModNetworkManager() {}

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, HighwaySignPacket.TYPE, HighwaySignPacket.STREAM_CODEC, HighwaySignPacket::handle);

        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, HighwaySignResultPacket.TYPE, HighwaySignResultPacket.STREAM_CODEC, HighwaySignResultPacket::handle);
        } else {
            // A dedicated server has no receiver for client bound packets, but still has to know the payload type to send it.
            NetworkManager.registerS2CPayloadType(HighwaySignResultPacket.TYPE, HighwaySignResultPacket.STREAM_CODEC);
        }
    }
}
