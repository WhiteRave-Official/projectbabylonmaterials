package com.rave.projectbabylonmaterials.hud;

import com.mojang.serialization.Codec;

import java.util.Map;
import java.util.Optional;

/**
 * A full HUD layout: a map of element id ({@link HudElements}) to its {@link HudElementLayout}.
 * Loaded from datapacks and synced to clients on its own network channel.
 */
public record HudLayout(Map<String, HudElementLayout> elements) {
    public static final Codec<HudLayout> CODEC = Codec.unboundedMap(Codec.STRING, HudElementLayout.CODEC)
            .xmap(HudLayout::new, HudLayout::elements);

    public Optional<HudElementLayout> get(String id) {
        return Optional.ofNullable(elements.get(id));
    }
}
