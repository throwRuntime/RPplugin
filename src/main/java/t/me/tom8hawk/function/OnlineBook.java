package t.me.tom8hawk.function;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BukkitConverters;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import t.me.tom8hawk.RPplugin;
import t.me.tom8hawk.config.ConfigValues;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class OnlineBook implements RpFunction {

    private final RPplugin plugin;
    private final ConfigValues configValues;
    private final Set<String> online;

    public OnlineBook(final RPplugin plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
        this.online = new HashSet<>();
    }

    @Override
    public void init() {
        if (!this.isFunctionEnabled()) {
            return;
        }

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this.plugin,
                PacketType.Play.Server.SET_SLOT,
                PacketType.Play.Server.WINDOW_ITEMS) {

            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                if (packet.getType() == PacketType.Play.Server.SET_SLOT) {
                    ItemStack item = packet.getItemModifier().readSafely(0);

                    if (item != null) {
                        packet.getItemModifier().write(0, handle(item));
                    }
                } else if (packet.getType() == PacketType.Play.Server.WINDOW_ITEMS) {
                    List<ItemStack> previousItems = packet
                            .getLists(BukkitConverters.getItemStackConverter())
                            .readSafely(0);

                    if (previousItems == null || previousItems.isEmpty()) {
                        return;
                    }

                    List<ItemStack> newItems = new ArrayList<>(previousItems.size());

                    for (ItemStack item : previousItems) {
                        newItems.add(handle(item));
                    }

                    packet.getLists(BukkitConverters.getItemStackConverter()).write(0, newItems);
                }

                event.setPacket(packet);
            }
        });
    }

    @Override
    public boolean isFunctionEnabled() {
        return this.plugin.getConfigValues().isOnlineBookEnabled();
    }

    @Override
    public void disable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
        this.online.clear();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.online.add(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.online.remove(event.getPlayer().getName());
    }

    private ItemStack handle(ItemStack item) {
        if (item != null && item.getType() == Material.WRITTEN_BOOK) {
            BookMeta book = (BookMeta) item.getItemMeta();

            if (book != null) {
                String author = book.getAuthor();

                if (author != null) {
                    int spaceIndex = author.indexOf(' ');

                    if (spaceIndex != -1) {
                        author = author.substring(0, spaceIndex);
                    }

                    Component postfix = this.online.contains(author)
                            ? this.configValues.getOnlineBookOnline()
                            : this.configValues.getOnlineBookOffline();

                    item = item.clone();
                    item.setItemMeta(book.author(Component.text(author).append(postfix)));
                    return item;
                }
            }
        }

        return item;
    }

}