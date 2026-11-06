package com.spearforge.sBank.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ModalityTest {
    @Test
    void mapeaMundosT2() {
        assertEquals("oneblock", Modality.of("oneblock_world_nether"));
        assertEquals("skyblock", Modality.of("bskyblock_world"));
        assertEquals("onechunk", Modality.of("onechunk_world_the_end"));
        assertEquals("clasico", Modality.of("clasico_nether"));
        assertEquals("clasico", Modality.of("world_clasico"));
        assertEquals("laboratorio", Modality.of("world_lab_flat"));
        assertEquals("lobby", Modality.of("spawnwarps"));
        assertEquals("survival", Modality.of("world"));
        assertEquals("survival", Modality.of("galactifun_space"));
        assertEquals("survival", Modality.of("boss_dimension"));
    }
}
