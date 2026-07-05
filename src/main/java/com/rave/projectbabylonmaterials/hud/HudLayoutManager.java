package com.rave.projectbabylonmaterials.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Server-side datapack loader for the HUD layout. Reads every JSON under {@code data/<ns>/hud_layout/} (each
 * an object of {@code element id -> placement}), merges them over the hardcoded {@link HudElements#DEFAULT}
 * (so a partial file only changes the fields it lists), and syncs the result to clients via {@link HudNetwork}.
 *
 * <p>Registered on the game bus (see the mod's main class). Kept independent of the gem systems.
 */
public final class HudLayoutManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "hud_layout";

    private static volatile HudLayout current = HudElements.DEFAULT;

    public HudLayoutManager() {
        super(GSON, DIRECTORY);
    }

    public static HudLayout current() {
        return current;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, HudElementLayout> merged = new HashMap<>(HudElements.DEFAULT.elements());

        // Deterministic order: later resource ids win.
        for (Map.Entry<ResourceLocation, JsonElement> file : new TreeMap<>(files).entrySet()) {
            if (!file.getValue().isJsonObject()) {
                ProjectBabylonMaterials.LOGGER.error("HUD layout {} is not a JSON object, skipping", file.getKey());
                continue;
            }

            for (Map.Entry<String, JsonElement> element : file.getValue().getAsJsonObject().entrySet()) {
                HudElementLayout.CODEC.parse(JsonOps.INSTANCE, element.getValue())
                        .resultOrPartial(error -> ProjectBabylonMaterials.LOGGER.error(
                                "Invalid HUD layout for '{}' in {}: {}", element.getKey(), file.getKey(), error))
                        .ifPresent(layout -> merged.put(element.getKey(), layout));
            }
        }

        current = new HudLayout(Map.copyOf(merged));
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new HudLayoutManager());
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ClientboundHudLayoutPacket packet = new ClientboundHudLayoutPacket(current);
        ServerPlayer player = event.getPlayer();
        if (player != null) {
            PacketDistributor.sendToPlayer(player, packet);
        } else {
            PacketDistributor.sendToAllPlayers(packet);
        }
    }
}
