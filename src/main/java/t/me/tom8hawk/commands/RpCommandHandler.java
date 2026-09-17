package t.me.tom8hawk.commands;

import com.github.groundbreakingmc.gikymessage.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import t.me.tom8hawk.RPplugin;
import t.me.tom8hawk.config.ConfigValues;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class RpCommandHandler extends CommandHandler {

    private static final String TRY_PERMISSION = "rppl.try";

    private static final String ME_PERMISSION = "rppl.me";

    private final ConfigValues configValues;

    private final ThreadLocalRandom random;

    public RpCommandHandler(RPplugin plugin) {
        super(plugin);
        this.configValues = plugin.getConfigValues();
        this.random = ThreadLocalRandom.current();
    }

    @Override
    protected List<String> getHandledCommands() {
        return List.of("me", "try");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.configValues.getOnlyPlayersMessage());
            return true;
        }

        final Player playerSender = (Player) sender;

        final int distance;
        Text placeholdersText;

        if (label.equalsIgnoreCase("try")) {

            if (configValues.isTryPermission() && !sender.hasPermission(TRY_PERMISSION)) {
                sender.sendMessage(this.configValues.getNoPermissionMessage());
                return true;
            }

            distance = this.configValues.getTryDistance();
            placeholdersText = this.random.nextBoolean()
                    ? this.configValues.getTrySuccess()
                    : this.configValues.getTryFailed();
        } else {

            if (configValues.isMePermission() && !sender.hasPermission(ME_PERMISSION)) {
                sender.sendMessage(this.configValues.getNoPermissionMessage());
                return true;
            }

            distance = this.configValues.getMeDistance();
            placeholdersText = this.configValues.getMeMessage();
        }

        final Component message = placeholdersText.render(
                "player", playerSender.displayName(),
                "message", Component.text(String.join(" ", args))
        );

        if (distance == -1) {
            for (final Player target : Bukkit.getOnlinePlayers()) {
                target.sendMessage(message);
            }
        } else {
            final Location location = playerSender.getLocation();
            final Area area = new Area(location, distance);

            for (final Player target : location.getWorld().getPlayers()) {
                if (area.contains(target.getLocation())) {
                    target.sendMessage(message);
                }
            }
        }

        return false;
    }

    private static class Area {
        private final World world;
        private final Vector min;
        private final Vector max;

        private Area(Location center, double distance) {
            this.world = center.getWorld();

            int x = center.getBlockX();
            int y = center.getBlockY();
            int z = center.getBlockZ();

            double increased = distance + 1;
            this.min = new Vector(x - distance, y - distance, z - distance);
            this.max = new Vector(x + increased, y + distance, z + increased);
        }

        public boolean contains(Location other) {
            return Objects.equals(this.world, other.getWorld()) && other.toVector().isInAABB(this.min, this.max);
        }
    }
}
