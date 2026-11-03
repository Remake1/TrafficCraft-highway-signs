package dev.remake1.tchighwaysigns.registry;

import de.mrjulsen.mcdragonlib.network.DLNetworkManager;
import de.mrjulsen.mcdragonlib.network.NetworkDirection;
import de.mrjulsen.mcdragonlib.network.NetworkPacketType;
import de.mrjulsen.mcdragonlib.util.DLUtils;
import dev.remake1.tchighwaysigns.HighwaySigns;
import dev.remake1.tchighwaysigns.network.packets.cts.HighwaySignPacket;
import dev.remake1.tchighwaysigns.network.packets.stc.HighwaySignResultPacket;

public final class ModNetworkManager {

    public static final DLNetworkManager NETWORK = new DLNetworkManager(DLUtils.resourceLocation(HighwaySigns.MOD_ID, "network"), "1");

    public static final NetworkPacketType.Send<NetworkDirection.C2S, HighwaySignPacket> UPDATE_HIGHWAY_SIGN = NETWORK.registerSendOnlyPacket("update_highway_sign", NetworkDirection.C2S, HighwaySignPacket::handle, HighwaySignPacket::new);

    public static final NetworkPacketType.Send<NetworkDirection.S2C, HighwaySignResultPacket> HIGHWAY_SIGN_RESULT = NETWORK.registerSendOnlyPacket("highway_sign_result", NetworkDirection.S2C, HighwaySignResultPacket::handle, HighwaySignResultPacket::new);

    private ModNetworkManager() {}

    public static void init() {
    }
}
