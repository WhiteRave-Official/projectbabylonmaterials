# Project Babylon Materials — Example Balance Datapack

A **complete, ready-to-edit** datapack that overrides every gem and station value the mod exposes.
All values here equal the mod's built-in defaults, so dropping it in **changes nothing** until you edit a number.

## Install

Copy the **contents** of this folder (the `pack.mcmeta` and the `data/` folder) into a datapack:

- Single world: `<world>/datapacks/pbm_balance_example/`
- All worlds (global): a normal datapack in your packs folder.

Then in-game run `/reload` (or re-enter the world). On a multiplayer server the synced
values reach clients automatically; an already-connected client refreshes its tooltips on relog.

> **Important — full-object replacement.** Like every Minecraft datapack registry, each JSON file
> *replaces* an entry, it does **not** merge fields. If a file is present, any field you leave out
> falls back to the codec default (mostly `0`), **not** the mod's hardcoded value. So always keep the
> whole object and only change the numbers you want. If you don't want to touch a tier/gem at all,
> just delete that file from the pack.

## Folder layout

```
pack.mcmeta
data/project_babylon_materials/project_babylon_materials/
    rarity_balance/   common.json uncommon.json rare.json epic.json legendary.json
    gem_balance/      ruby_stone.json ... (22 gems)
```

The doubled `project_babylon_materials/project_babylon_materials/` is required: modid-namespaced
datapack registries load from `data/<pack_namespace>/<registry_namespace>/<registry_path>/`.

## `rarity_balance/<tier>.json` — every field

One file per rarity tier (`common`, `uncommon`, `rare`, `epic`, `legendary`). Each field describes the
tier the file is named after ("T").

| Field | Type | Meaning |
|---|---|---|
| `roll_weight` | int | Weight when a fresh item is randomly assigned rarity T (relative to other tiers). |
| `base_enchant_slots` | int | Enchant slots an item rolls when it becomes T. |
| `enchant_bonus_chance` | int (0–100) | % chance of **+1** extra enchant slot on top of the base. |
| `base_gem_slots` | int | Gem slots an item rolls when it becomes T. |
| `gem_slot_bonus_chance` | int (0–100) | % chance of **+1** extra gem slot on top of the base. |
| `material` | item id | Item required to **upgrade/reforge INTO** T (omit for `common` — nothing upgrades to common). |
| `reforge_material_count` | int | How many `material` the Reforge table consumes to reach T. |
| `required_dust` | int | `gem_dust` the Jewelry table consumes to upgrade a gem INTO T. |
| `required_xp` | int | XP levels to upgrade/reforge INTO T (Reforge and Jewelry share this). |
| `success_chance` | int (0–100) | % success when upgrading/reforging **FROM** T to the next tier. |
| `socket_xp` | int | XP levels to socket a gem **whose own rarity is** T (Reforge table). |
| `extraction_xp` | int | XP levels to extract a socketed gem **whose own rarity is** T (Refinement table). |
| `max_upgrade_attempts` | int | Jewelry upgrade attempts a gem at rarity T starts with (`0` = cannot be upgraded). |

Notes:
- `material` accepts any item id, e.g. `"minecraft:diamond"` or `"project_babylon_materials:fate_orb"`.
- Mind the difference between *into-tier* costs (`material`, `required_dust`, `required_xp`) and
  *from-tier* values (`success_chance`, `max_upgrade_attempts`): e.g. `epic.json`'s `success_chance`
  is the chance of an **epic→legendary** upgrade succeeding.
- `gem_slot_bonus_chance` also affects the gem-slot cap: the largest `base_gem_slots + (bonus>0 ? 1 : 0)`
  across all tiers is the maximum number of gems any item can hold.

## `gem_balance/<gem>.json` — every field

One file per gem type. The five numbers are the gem's effect value at each rarity. What the value
means depends on the gem (e.g. Ruby = attack-damage %, Sapphire = armor-negation %); the gem→effect
mapping is fixed in code, only the magnitudes are data-driven.

```json
{ "common": 1.5, "uncommon": 2.0, "rare": 3.5, "epic": 4.5, "legendary": 5.0 }
```

Gem file names (use the gem's registry name):

```
ruby_stone, sapphire_stone, topaz_stone, white_stone, black_stone, chrizolite_stone,
malachite_stone, garnet_stone, lapis_stone, mana_stone, end_stone, blood_pearl,
northern_stone, pyrite_stone, moon_pearl, dragon_stone, nature_stone, diamond_stone,
amethyst_stone, health_stone, emerald_stone, aquamarine_stone
```

## HUD layout — `hud_layout/<file>.json`

Repositions the on-screen combat HUD elements. Files live at `data/<pack>/hud_layout/*.json` — note this is a
*single* namespace path (it uses a reload listener, not a datapack registry) and is **field-merged** over the
defaults, so a partial file only changes the fields it lists. Each file maps an element id to a placement.

Elements: `stats_box` (armor / toughness / magic-armor panel) and `dragonsteel_passive` (set passive icon).

| Field | Type | Meaning |
|---|---|---|
| `anchor` | enum | Screen anchor: `top_left` `top_center` `top_right` `center_left` `center` `center_right` `bottom_left` `bottom_center` `bottom_right`. |
| `align_x` | enum | `start` / `center` / `end` — which edge of the element sits on the anchor's X point. |
| `align_y` | enum | `start` / `center` / `end` — which edge sits on the anchor's Y point. |
| `offset_x` / `offset_y` | int | Pixel offset from the anchor. |
| `scale` | float | Size multiplier. |
| `visible` | bool | Whether the element is drawn at all. |
| `shift_for_offhand` | bool | (`stats_box`) nudge aside when an offhand / extra hotbar slot is shown. |

The shipped values reproduce the default positions. Players can also override placement locally without a datapack
via the mod's client config (`hud.layout.<element>`, set `override = true`). The HUD syncs on its own network
channel, kept separate from the gem systems.