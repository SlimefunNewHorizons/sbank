package com.spearforge.sBank.model;

import java.util.Locale;

/** Modalidad economica de un mundo. Cada jugador tiene una cuenta y una deuda por modalidad. */
public final class Modality {
    public static final String SURVIVAL = "survival";

    private Modality() {
    }

    public static String of(String world) {
        String w = world == null ? "" : world.toLowerCase(Locale.ROOT);
        if (w.startsWith("oneblock")) return "oneblock";
        if (w.startsWith("bskyblock")) return "skyblock";
        if (w.startsWith("onechunk")) return "onechunk";
        if (w.startsWith("clasico") || w.startsWith("world_clasico")) return "clasico";
        if (w.startsWith("laboratorio") || w.startsWith("world_lab")) return "laboratorio";
        if (w.equals("spawnwarps")) return "lobby";
        return SURVIVAL;
    }

    public static String key(String username, String modality) {
        return username + "@" + modality;
    }
}
