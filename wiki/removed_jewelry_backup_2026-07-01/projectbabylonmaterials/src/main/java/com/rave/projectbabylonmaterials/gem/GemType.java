package com.rave.projectbabylonmaterials.gem;

import com.rave.projectbabylonmaterials.item.gem.GemItem;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum GemType {
    RUBY("ruby_stone", "ruby_stone", "broken_ruby_stone", EnumSet.of(GemApplication.MELEE), 1.5, 2.0, 3.5, 4.5, 5.0),
    SAPPHIRE("sapphire_stone", "sapphire_stone", "broken_sapphire_stone", EnumSet.of(GemApplication.MELEE), 0.5, 1.0, 2.5, 3.5, 4.0),
    TOPAZ("topaz_stone", "topaz_stone", "broken_topaz_stone", EnumSet.of(GemApplication.MELEE), 1.5, 2.0, 3.5, 4.5, 5.0),
    WHITE("white_stone", "white_stone", "broken_white_stone", EnumSet.of(GemApplication.MELEE), 0.5, 1.0, 2.5, 3.5, 4.0),
    BLACK("black_stone", "black_stone", "broken_black_stone", EnumSet.of(GemApplication.MELEE), 1.0, 2.0, 3.0, 4.5, 6.0),
    CHRIZOLITE("chrizolite_stone", "chrizolite_stone", "broken_chrizolite_stone", EnumSet.of(GemApplication.MELEE), 1.5, 2.0, 3.5, 4.5, 6.0),
    MALACHITE("malachite_stone", "malachite_stone", "broken_malachite_stone", EnumSet.of(GemApplication.RANGED), 1.5, 3.0, 4.5, 5.5, 6.0),
    GARNET("garnet_stone", "garnet_stone", "broken_garnet_stone", EnumSet.of(GemApplication.RANGED), 1.5, 2.0, 2.5, 3.5, 4.0),
    LAPIS("lapis_stone", "lapis_stone", "broken_lapis_stone", EnumSet.of(GemApplication.RANGED), 3.0, 5.0, 7.0, 8.0, 10.0),
    MANA("mana_stone", "mana_stone", "broken_mana_stone", EnumSet.of(GemApplication.ARMOR), 2.0, 3.0, 4.0, 6.0, 8.0),
    END("end_stone", "end_stone", "broken_end_stone", EnumSet.of(GemApplication.MELEE, GemApplication.MAGIC), 1.5, 3.0, 4.5, 5.5, 6.0),
    BLOOD_PEARL("blood_pearl", "blood_pearl", "broken_blood_pearl", EnumSet.of(GemApplication.MELEE, GemApplication.MAGIC), 1.5, 3.0, 4.5, 5.5, 6.0),
    NORTHERN("northern_stone", "north_stone", "broken_north_stone", EnumSet.of(GemApplication.MELEE, GemApplication.MAGIC), 1.5, 3.0, 4.5, 5.5, 6.0),
    PYRITE("pyrite_stone", "pyrite_stone", "broken_pyrite_stone", EnumSet.of(GemApplication.MELEE, GemApplication.MAGIC), 1.5, 3.0, 4.5, 5.5, 6.0),
    MOON_PEARL("moon_pearl", "moon_pearl", "broken_moon_pearl", EnumSet.of(GemApplication.MELEE, GemApplication.MAGIC), 1.5, 3.0, 4.5, 5.5, 6.0),
    DRAGON("dragon_stone", "dragon_stone", "broken_dragon_stone", EnumSet.of(GemApplication.MELEE, GemApplication.MAGIC), 1.5, 3.0, 4.5, 5.5, 6.0),
    NATURE("nature_stone", "nature_stone", "broken_nature_stone", EnumSet.of(GemApplication.MELEE, GemApplication.MAGIC), 1.5, 3.0, 4.5, 5.5, 6.0),
    DIAMOND("diamond_stone", "diamond_stone", "broken_diamond_stone", EnumSet.of(GemApplication.MAGIC), 1.5, 2.0, 2.5, 3.5, 4.0),
    AMETHYST("amethyst_stone", "amethyst_stone", "broken_amethyst_stone", EnumSet.of(GemApplication.MAGIC), 1.5, 2.0, 2.5, 3.5, 4.0),
    HEALTH("health_stone", "health_stone", "broken_health_stone", EnumSet.of(GemApplication.ARMOR), 1.5, 2.0, 3.5, 4.0, 5.0),
    EMERALD("emerald_stone", "emerald_stone", "broken_emerald_stone", EnumSet.of(GemApplication.ARMOR), 2.0, 3.0, 4.0, 5.0, 6.0),
    AQUAMARINE("aquamarine_stone", "aquamarine_stone", "broken_aquamarine_stone", EnumSet.of(GemApplication.ARMOR), 1.0, 2.0, 3.5, 4.0, 5.0);

    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat("0.0#", DecimalFormatSymbols.getInstance(Locale.ROOT));

    private final String registryName;
    private final String textureName;
    private final String brokenTextureName;
    private final EnumSet<GemApplication> applications;
    private final double commonValue;
    private final double uncommonValue;
    private final double rareValue;
    private final double epicValue;
    private final double legendaryValue;

    GemType(String registryName, String textureName, String brokenTextureName, EnumSet<GemApplication> applications,
            double commonValue, double uncommonValue, double rareValue, double epicValue, double legendaryValue) {
        this.registryName = registryName;
        this.textureName = textureName;
        this.brokenTextureName = brokenTextureName;
        this.applications = applications.clone();
        this.commonValue = commonValue;
        this.uncommonValue = uncommonValue;
        this.rareValue = rareValue;
        this.epicValue = epicValue;
        this.legendaryValue = legendaryValue;
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getTextureName() {
        return textureName;
    }

    public String getBrokenTextureName() {
        return brokenTextureName;
    }

    public EnumSet<GemApplication> getApplications() {
        return applications.clone();
    }

    public double getValue(ItemRarityTier rarity) {
        return switch (rarity) {
            case COMMON -> commonValue;
            case UNCOMMON -> uncommonValue;
            case RARE -> rareValue;
            case EPIC -> epicValue;
            case LEGENDARY -> legendaryValue;
        };
    }

    public Component createDescription(ItemRarityTier rarity) {
        return Component.translatable(getDescriptionKey(), formatPercent(getValue(rarity)));
    }

    public List<ItemStack> createApplicablePreviewItems() {
        return GemApplication.createPreviewItems(applications);
    }

    public Component createApplicationsText() {
        MutableComponent component = Component.empty();
        int index = 0;
        for (GemApplication application : applications) {
            if (index++ > 0) {
                component.append(Component.literal(",")).append(CommonComponents.SPACE);
            }
            component.append(Component.translatable(application.getTranslationKey()));
        }
        return component;
    }

    public String getDescriptionKey() {
        return "item.project_babylon_materials." + registryName + ".desc";
    }

    public static Optional<GemType> fromStack(ItemStack stack) {
        if (stack.getItem() instanceof GemItem gemItem) {
            return Optional.of(gemItem.getGemType());
        }
        return Optional.empty();
    }

    private static String formatPercent(double value) {
        return VALUE_FORMAT.format(value) + "%";
    }
}