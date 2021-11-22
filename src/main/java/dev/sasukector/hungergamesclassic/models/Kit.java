package dev.sasukector.hungergamesclassic.models;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    public enum KitID {
        BUFFER, CAMPER, ENCHANTER, INFECTED, TANK,
        DISGUISER, ROBBER, THOR, KATNISS, ENDERMAN,
        MINER, HUNTER, NINJA, LUST, GLADIATOR,
        LUMBERJACK, VAMPIRE, BACON_KNIGHT, TRANSPORTER, CAVEMAN,
        DYNARCHER, CUPID, SPY, GHOST_BUSTER, WARRIOR,
        STOMPER, MOTHER_NATURE, MILKMAN, BOXER, MONSTER,
        HERMES, NIGHT_WARRIOR, FLOWER_STEVE, MOB_HUNTER, PROTEGO,
        SNOW_FIGHTER
    }

    private final @Getter KitID id;
    private final @Getter String name;
    private final @Getter String color;
    private final @Getter ItemStack icon;
    private final @Getter ItemStack[] armorContents;
    private final @Getter List<ItemStack> kitContents;

    public Kit(KitID id, String name, String color, ItemStack icon) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.armorContents = new ItemStack[4];
        this.kitContents = new ArrayList<>();
    }

    public void addArmor(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack foot) {
        this.armorContents[0] = helmet;
        this.armorContents[1] = chestplate;
        this.armorContents[2] = leggings;
        this.armorContents[3] = foot;
    }

    public void addItem(ItemStack itemStack) {
        this.kitContents.add(itemStack);
    }

}
