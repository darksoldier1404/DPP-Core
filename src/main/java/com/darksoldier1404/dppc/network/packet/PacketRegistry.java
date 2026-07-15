package com.darksoldier1404.dppc.network.packet;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Maps packet ids to a decoder and a handler, so an incoming payload is routed by table lookup
 * instead of a growing {@code if/else} or {@code switch}. Adding a packet type is one
 * {@link #register} call and touches no existing code.
 *
 * <p>Each platform network (e.g. {@code BukkitNetwork}) owns one registry. Instances are safe to
 * register on and dispatch through from multiple threads.
 */
@DPPCoreVersion(since = "5.5.0")
public final class PacketRegistry {

    private final Map<String, Registration> registrations = new ConcurrentHashMap<>();

    /**
     * Registers a packet type.
     *
     * @param id      the wire id; must be unique within this registry and match the sender's id
     * @param decoder reconstructs the packet from a reader positioned just after the id
     *                (conventionally a {@code MyPacket::read} method reference)
     * @param handler the logic run for a received packet of this type
     * @param <T>     the packet type
     * @throws IllegalStateException if {@code id} is already registered
     */
    public <T extends Packet> void register(String id, Function<PacketReader, T> decoder, PacketHandler<T> handler) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(decoder, "decoder");
        Objects.requireNonNull(handler, "handler");
        if (registrations.putIfAbsent(id, new Registration(decoder, handler)) != null) {
            throw new IllegalStateException("Packet id already registered: " + id);
        }
    }

    /** Whether a packet type is registered under {@code id}. */
    public boolean isRegistered(String id) {
        return registrations.containsKey(id);
    }

    /**
     * Reads a packet id from {@code reader}, decodes the packet, and dispatches it to its handler.
     *
     * @param context the environment to hand the handler
     * @param reader  positioned at the start of a payload (id first)
     * @return {@code true} if a matching handler ran, {@code false} if the id was unknown
     *         (the payload is then left partially read and should be discarded)
     */
    public boolean handle(PacketContext context, PacketReader reader) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(reader, "reader");
        String id = reader.readString();
        Registration registration = registrations.get(id);
        if (registration == null) {
            return false;
        }
        Packet packet = registration.decoder.apply(reader);
        registration.handler.handle(context, packet);
        return true;
    }

    /** One id's decoder + handler pair. The unchecked cast is safe: {@link #register} pairs matching types. */
    private static final class Registration {
        private final Function<PacketReader, ? extends Packet> decoder;
        private final PacketHandler<Packet> handler;

        @SuppressWarnings("unchecked")
        Registration(Function<PacketReader, ? extends Packet> decoder, PacketHandler<? extends Packet> handler) {
            this.decoder = decoder;
            this.handler = (PacketHandler<Packet>) handler;
        }
    }
}
