package t.me.tom8hawk.hook;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@UtilityClass
public class HookFactory {

    public static @Nullable RassvetCitiesHook rassvetCities() {
        return createIfPluginEnabled("TempDynmapCities", RassvetCitiesHook::new);
    }

    private static @Nullable <T> T createIfPluginEnabled(String name, Supplier<T> object) {
        return Bukkit.getPluginManager().isPluginEnabled(name) ? object.get() : null;
    }

}
