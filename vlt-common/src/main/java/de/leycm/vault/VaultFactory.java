/**
 * LECP-LICENSE NOTICE
 * <br><br>
 * This Sourcecode is under the LECP-LICENSE. <br>
 * License at: <a href="https://github.com/leycm/leycm/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) LeyCM <leycm@proton.me> <br>
 * Copyright (c) maintainers <br>
 * Copyright (c) contributors
 */
package de.leycm.vault;

import de.leycm.vault.adapter.ConfigFileAdapter;
import de.leycm.vault.adapter.TypeAdapter;
import de.leycm.vault.adapter.file.*;
import de.leycm.vault.adapter.type.Types;

import lombok.NonNull;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VaultFactory implements ConfigFactory {

    private final File defaultDirectory;
    private final Map<String, ConfigFileAdapter> fileAdapters = new ConcurrentHashMap<>();
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new ConcurrentHashMap<>();
    private final Map<File, Config> configCache = new ConcurrentHashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public VaultFactory(@NotNull File defaultDirectory) {
        this.defaultDirectory = defaultDirectory;

        if (!defaultDirectory.exists())
            defaultDirectory.mkdirs();
    }

    @Override
    public void onInstall() {
        registerDefaultTypeAdapters();
        registerFileAdapter(new JsonConfigAdapter(), "jsn", "json", "jason");
        registerFileAdapter(new YamlConfigAdapter(), "yml", "yaml");
        registerFileAdapter(new TomlConfigAdapter(), "tml", "toml");
    }

    private void registerDefaultTypeAdapters() {
        registerTypeAdapter(new Types.IntegerAdapter(), Integer.class);
        registerTypeAdapter(new Types.IntegerAdapter(), int.class);
        registerTypeAdapter(new Types.LongAdapter(), Long.class);
        registerTypeAdapter(new Types.LongAdapter(), long.class);
        registerTypeAdapter(new Types.DoubleAdapter(), Double.class);
        registerTypeAdapter(new Types.DoubleAdapter(), double.class);
        registerTypeAdapter(new Types.FloatAdapter(), Float.class);
        registerTypeAdapter(new Types.FloatAdapter(), float.class);
        registerTypeAdapter(new Types.BooleanAdapter(), Boolean.class);
        registerTypeAdapter(new Types.BooleanAdapter(), boolean.class);
        registerTypeAdapter(new Types.StringAdapter(), String.class);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public Config create(@NonNull File file) {
        if (configCache.containsKey(file)) {
            return configCache.get(file);
        }

        Map<String, Object> data = new LinkedHashMap<>();

        if (file.exists() || copyFromResources(file)) {
            try {
                String content = Files.readString(file.toPath());
                ConfigFileAdapter adapter = getAdapter(file);

                if (adapter != null) data = adapter.read(content);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config from " + file.getAbsolutePath(), e);
            }
        } else {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create config file " + file.getAbsolutePath(), e);
            }
        }

        VaultConfig config = new VaultConfig(file, data, this);
        configCache.put(file, config);
        return config;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean copyFromResources(@NotNull File file) {
        String resourcePath = "/vault/" + file.getName();
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) return false;

            file.getParentFile().mkdirs();
            Files.copy(in, file.toPath());
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy resource to " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public void reload(@NonNull File file) {
        configCache.remove(file);
        create(file);
    }

    @Override
    public void save(@NonNull File file) {
        Config config = configCache.get(file);

        if (config == null)
            throw new IllegalStateException("Config not loaded: " + file.getAbsolutePath());


        if (!(config instanceof VaultConfig vaultConfig))
            throw new IllegalStateException("Config is not a VaultConfig instance");

        ConfigFileAdapter adapter = getAdapter(file);

        if (adapter == null)
            throw new IllegalStateException("No adapter found for file: " + file.getAbsolutePath());


        try {
            String current = file.exists()
                    ? Files.readString(file.toPath())
                    : "";

            String content = adapter.write(current, vaultConfig.data());
            Files.writeString(file.toPath(), content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config to " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public File defDir() {
        return defaultDirectory;
    }

    @Override
    public void registerFileAdapter(ConfigFileAdapter adapter,
                                    String @NotNull ... endings) {
        for (String ending : endings) {
            fileAdapters.put(ending.toLowerCase(), adapter);
        }
    }

    @Override
    public <T> void registerTypeAdapter(TypeAdapter<T> adapter,
                                        Class<T> clazz) {
        typeAdapters.put(clazz, adapter);
    }

    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> getTypeAdapter(Class<T> clazz) {
        return (TypeAdapter<T>) typeAdapters.get(clazz);
    }

    private @Nullable ConfigFileAdapter getAdapter(@NotNull File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');

        if (dotIndex == -1) {
            return null;
        }

        String extension = name.substring(dotIndex + 1).toLowerCase();
        return fileAdapters.get(extension);
    }

}