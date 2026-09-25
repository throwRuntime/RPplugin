package t.me.tom8hawk.hook;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.tom8hawk.tempdynmapcities.TempDynmapCities;
import ru.tom8hawk.tempdynmapcities.town.TownInfo;
import ru.tom8hawk.tempdynmapcities.town.TownPlayerManager;

public class RassvetCitiesHook {

    private final TownPlayerManager townPlayerManager;

    RassvetCitiesHook() {
        townPlayerManager = JavaPlugin.getPlugin(TempDynmapCities.class).getTownPlayerManager();
    }

    public @NotNull Component getPlayerCityPrefix(Player player) {
        TownInfo townInfo = townPlayerManager.getCachedPlayerTown(player.getUniqueId());
        return townInfo != null ? townInfo.prefix() : Component.empty();
    }

}
