package dev.sasukector.hungergamesclassic.models;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    public enum KitID {
        POISONED, JASON, HAWKEYE, BOMBARDER, PRO_MINER,
        BUFFER, CAMPER, ENCHANTER, INFECTED, TANK,
        DISGUISER, ROBBER, THOR, KATNISS, ENDERMAN,
        MINER, HUNTER, NINJA, GLADIATOR, TREASURE_HUNTER,
        LUMBERJACK, VAMPIRE, BACON_KNIGHT, TRANSPORTER, CAVEMAN,
        DYNARCHER, CUPID, SPY, GHOST_BUSTER, WARRIOR,
        STOMPER, MOTHER_NATURE, MILKMAN, BOXER, MONSTER,
        HERMES, NIGHT_WARRIOR, MOB_HUNTER, PROTEGO, PYRO,
        SNOW_FIGHTER, SONIC, CHEMIST
    }

    private final @Getter KitID id;
    private final @Getter String name;
    private final @Getter String color;
    private final @Getter ItemStack icon;
    private final @Getter ItemStack[] armorContents;
    private final @Getter List<ItemStack> kitContents;
    private final @Getter List<PotionEffectType> effects;

    public Kit(KitID id, String name, String color, ItemStack icon) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.armorContents = new ItemStack[4];
        this.kitContents = new ArrayList<>();
        this.effects = new ArrayList<>();
    }

    public void addArmor(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack foot) {
        this.armorContents[3] = helmet;
        this.armorContents[2] = chestplate;
        this.armorContents[1] = leggings;
        this.armorContents[0] = foot;
    }

    public void addItem(ItemStack itemStack) {
        this.kitContents.add(itemStack);
    }

}
