package t.me.tom8hawk.config;

import com.github.groundbreakingmc.gikymessage.Text;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import t.me.tom8hawk.RPplugin;

import java.io.File;

@RequiredArgsConstructor@Getter
public final class ConfigValues {

    @Getter(AccessLevel.NONE)
    private final RPplugin plugin;

    private boolean tryPermission;
    private int tryDistance;
    private Text trySuccess;
    private Text tryFailed;

    private boolean mePermission;
    private int meDistance;
    private Text meMessage;

    private boolean onlineBookEnabled;
    private Component onlineBookOnline;
    private Component onlineBookOffline;

    private boolean hideTagsEnabled;
    private boolean defaultHidden;
    private Text hideTagsActionbar;

    private Component hiddenTagMessage;
    private Component shownTagMessage;

    private Component noPermissionMessage;
    private Component onlyPlayersMessage;
    private Component unknownCommandMessage;

    public void setup() {
        final FileConfiguration config = this.getConfig();
        this.setupTry(config);
        this.setupMe(config);
        this.setupOnlineBook(config);
        this.setupHideTags(config);
        this.setupMessages(config);
    }

    private void setupTry(final FileConfiguration config) {
        final ConfigurationSection trySection = config.getConfigurationSection("TRY");
        this.tryPermission = trySection.getBoolean("permission");
        this.tryDistance = trySection.getInt("distance");
        this.trySuccess = this.prepareText(trySection, "success");
        this.tryFailed = this.prepareText(trySection, "failed");
    }

    private void setupMe(final FileConfiguration config) {
        final ConfigurationSection meSection = config.getConfigurationSection("ME");
        this.mePermission = meSection.getBoolean("permission");
        this.meDistance = meSection.getInt("distance");
        this.meMessage = this.prepareText(meSection, "message");
    }

    private void setupOnlineBook(final FileConfiguration config) {
        final ConfigurationSection onlineBookSection = config.getConfigurationSection("ONLINE-BOOK");
        this.onlineBookEnabled = onlineBookSection.getBoolean("enabled");
        this.onlineBookOnline = this.onlineBookEnabled ? this.colorize(onlineBookSection, "online") : null;
        this.onlineBookOffline = this.onlineBookEnabled ? this.colorize(onlineBookSection, "offline") : null;
    }

    private void setupHideTags(final FileConfiguration config) {
        final ConfigurationSection onlineBookSection = config.getConfigurationSection("HIDE-TAGS");
        this.defaultHidden = onlineBookSection.getBoolean("default-hidden");
        this.hideTagsEnabled = onlineBookSection.getBoolean("enabled");
        this.hideTagsActionbar = this.hideTagsEnabled ? this.prepareText(onlineBookSection, "actionbar") : null;
        this.hiddenTagMessage = this.colorize(onlineBookSection, "messages.hidden");
        this.shownTagMessage = this.colorize(onlineBookSection, "messages.shown");
    }

    private void setupMessages(final FileConfiguration config) {
        final ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        this.noPermissionMessage = this.colorize(messagesSection, "no-permission");
        this.onlyPlayersMessage = this.colorize(messagesSection, "only-players");
        this.unknownCommandMessage = this.colorize(messagesSection, "unknown");
    }

    private FileConfiguration getConfig() {
        final File file = new File(this.plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            this.plugin.saveResource("config.yml", false);
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    private Component colorize(final ConfigurationSection section, final String path) {
        Text value = prepareText(section, path);
        return value != null ? value.render() : null;
    }

    private Text prepareText(final ConfigurationSection section, final String path) {
        String value = section.getString(path);
        return value != null ? Text.of(value) : null;
    }
}
