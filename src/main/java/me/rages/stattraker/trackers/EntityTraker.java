package me.rages.stattraker.trackers;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.text3.Text;
import me.rages.stattraker.StatTrakPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * @author : Michael
 * @since : 8/7/2022, Sunday
 **/
public class EntityTraker {

    private ItemStack itemStack;
    private NamespacedKey itemKey;
    private String dataLore;
    private String prefixLore;

    public EntityTraker(EntityType entityType, StatTrakPlugin plugin) {
        ItemStackBuilder builder = ItemStackBuilder.of(Material.NAME_TAG)
                .name(plugin.getConfig().getString("stat-trak-items." + entityType.name() + ".item-name"))
                .lore(plugin.getConfig().getStringList("stat-trak-items." + entityType.name() + ".item-lore"))
                .transformMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(plugin.getStatTrakItemKey(), PersistentDataType.STRING, entityType.name()));

        this.itemStack = builder.build();
        this.itemKey = new NamespacedKey(plugin, entityType.name());
        this.dataLore = plugin.getConfig().getString("stat-trak-items." + entityType.name() + ".trak-lore");
        this.prefixLore = Text.colorize(dataLore.split("%amount%")[0]);
    }

    public static EntityTraker create(EntityType entityType, StatTrakPlugin plugin) {
        return new EntityTraker(entityType, plugin);
    }

    public ItemStack incrementPlayerLore(ItemStack itemStack, int amount) {
        int total = itemStack.getItemMeta().getPersistentDataContainer().get(getItemKey(), PersistentDataType.INTEGER) + amount;
        ItemStackBuilder builder = ItemStackBuilder.of(itemStack);
        builder.transformMeta(meta -> meta.getPersistentDataContainer().set(getItemKey(), PersistentDataType.INTEGER, total));
        List<String> oldItemLore = itemStack.getItemMeta().getLore();
        builder.clearLore();
        builder.unflag(ItemFlag.HIDE_ENCHANTS);
        oldItemLore.stream().filter(currLore -> !currLore.startsWith(this.prefixLore)).forEach(builder::lore);
        builder.lore(getDataLore().replace("%amount%", String.format("%,d", total)));
        return builder.build();
    }

    public ItemStack incrementLore(ItemStack itemStack, int amount) {
        int total = itemStack.getItemMeta().getPersistentDataContainer().get(getItemKey(), PersistentDataType.INTEGER) + amount;
        ItemStackBuilder builder = ItemStackBuilder.of(itemStack);
        builder.transformMeta(meta -> meta.getPersistentDataContainer().set(getItemKey(), PersistentDataType.INTEGER, total));
        List<String> oldItemLore = itemStack.getItemMeta().getLore();
        builder.clearLore();
        builder.unflag(ItemFlag.HIDE_ENCHANTS);
        oldItemLore.forEach(currLore -> {
            if (currLore.startsWith(this.prefixLore)) {
                builder.lore(getDataLore().replace("%amount%", String.format("%,d", total)));
            } else {
                builder.lore(currLore);
            }
        });
        return builder.build();
    }

    public String getDataLore() {
        return dataLore;
    }

    public NamespacedKey getItemKey() {
        return itemKey;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
