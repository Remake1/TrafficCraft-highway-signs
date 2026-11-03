package dev.remake1.tchighwaysigns.init;

/** Indirection so that the common entrypoint never loads client classes on a dedicated server. */
public final class ClientInitWrapper {
    public static void init() {
        ClientInit.init();
    }
}
