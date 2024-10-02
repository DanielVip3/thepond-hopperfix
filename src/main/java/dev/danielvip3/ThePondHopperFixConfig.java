package dev.danielvip3;

import net.fabricmc.loader.api.FabricLoader;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThePondHopperFixConfig {
    private static final String CONFIG_FILE_NAME = "thepond-hopperfix.toml";
    private final File configFile;
    private List<String> disabledBlocks;

    public ThePondHopperFixConfig() {
        this.configFile = new File(FabricLoader.getInstance().getConfigDir().toString(), CONFIG_FILE_NAME);
        loadConfig();
    }

    private void loadConfig() {
        if (!configFile.exists()) saveDefaultConfig();

        try (FileReader reader = new FileReader(configFile)) {
            Toml parseResult = new Toml().read(reader);
            Toml table = parseResult.getTable("disable_hopper");

            disabledBlocks = table.getList("blocks");
            if (disabledBlocks == null) {
                disabledBlocks = new ArrayList<>(); // Initialize with an empty list if null
            }
        } catch (IOException e) {
            e.printStackTrace();
            disabledBlocks = new ArrayList<>(); // Initialize with an empty list on error
        }
    }

    private void saveDefaultConfig() {
        disabledBlocks = new ArrayList<>();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("[disable_hopper]\nblocks = []\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getDisabledBlocks() {
        return disabledBlocks;
    }
}