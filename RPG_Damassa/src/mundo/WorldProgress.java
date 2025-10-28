package mundo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import personagens.Personagem;

/**
 * WorldProgress — versão “por nível” e sem limite de exploração.
 * - Áreas são liberadas conforme o NÍVEL do jogador (Nv 1 -> 1 área, Nv 2 -> 2 áreas, ...).
 * - Explorar não tem limite (explorarNaArea sempre retorna true).
 * - Rastreia bosses derrotados e libera o Volibear ao cumprir requisitos.
 */
public class WorldProgress implements Serializable {

    // ====== CONFIG ======
    private static final int FINAL_BOSS_LEVEL_REQ = 15;

    // ====== MODELO DE ÁREA ======
    public static class AreaDef implements Serializable {
        private final String nome;
        public AreaDef(String nome) { this.nome = nome; }
        public String getNome() { return nome; }
    }

    public static class Area implements Serializable {
        private final AreaDef def;
        public Area(AreaDef def) { this.def = def; }
        public AreaDef def() { return def; }
        // compat
        public int getExploradas() { return 0; }
    }

    private final List<Area> areas = new ArrayList<>();
    private int areaAtualIndex = 0;

    // ====== PROGRESSO DE BOSSES ======
    private boolean defeatedDarius;
    private boolean defeatedTrundle;
    private boolean defeatedSylas;
    private boolean defeatedLissandra;

    private boolean finalBossUnlocked; // Volibear liberado?

    public WorldProgress() {
        areas.add(new Area(new AreaDef("Campos Gelados")));
        areas.add(new Area(new AreaDef("Fendas do Gelo")));
        areas.add(new Area(new AreaDef("Garganta dos Ventos")));
        areas.add(new Area(new AreaDef("Ruínas Ancestrais")));
        areas.add(new Area(new AreaDef("Montanhas Tempestuosas"))); // topo (Volibear)
    }

    // ===== API usada pelo jogo =====
    public int getAreasCount() { return areas.size(); }
    public int getAreaAtualIndex() { return areaAtualIndex; }
    public String getAreaAtualNome() { return areas.get(areaAtualIndex).def().getNome(); }
    public String getAreaNome(int idx) { return areas.get(idx).def().getNome(); }

    /** Áreas desbloqueadas = min(nível, total), no mínimo 1. */
    public int getUnlockedCount(Personagem jogador) {
        int n = getAreasCount();
        int nv = Math.max(1, jogador.getNivel());
        return Math.min(n, Math.max(1, nv));
    }

    public void setAreaAtual(int idx) {
        if (idx >= 0 && idx < areas.size()) areaAtualIndex = idx;
    }

    public boolean explorarNaArea(int idx) {
        return idx >= 0 && idx < areas.size();
    }

    public boolean podeAvancar(Personagem jogador) {
        int unlocked = getUnlockedCount(jogador);
        return (areaAtualIndex + 1) < unlocked;
    }

    public void avancarArea(Personagem jogador) {
        if (podeAvancar(jogador)) {
            areaAtualIndex = Math.min(areaAtualIndex + 1, getUnlockedCount(jogador) - 1);
        }
    }

    // ===== Boss progress & final unlock =====
    public void marcarBossDerrotado(String nome) {
        if (nome == null) return;
        switch (nome) {
            case "Darius" -> defeatedDarius = true;
            case "Trundle" -> defeatedTrundle = true;
            case "Sylas" -> defeatedSylas = true;
            case "Lissandra" -> defeatedLissandra = true;
            default -> { /* ignorar outros */ }
        }
    }

    public boolean preBossesTodosDerrotados() {
        return defeatedDarius && defeatedTrundle && defeatedSylas && defeatedLissandra;
    }

    public boolean isFinalBossUnlocked() { return finalBossUnlocked; }

    /**
     * Tenta liberar o Volibear. Retorna true se liberou agora (para exibir mensagem uma única vez).
     */
    public boolean tryUnlockFinalBoss(Personagem jogador) {
        if (!finalBossUnlocked
                && jogador != null
                && jogador.getNivel() >= FINAL_BOSS_LEVEL_REQ
                && preBossesTodosDerrotados()) {
            finalBossUnlocked = true;
            return true;
        }
        return false;
    }

    // Opcional: mapa simples textual
    public String mapa(Personagem jogador) {
        StringBuilder sb = new StringBuilder();
        int unlocked = getUnlockedCount(jogador);
        sb.append("=== MAPA DE FRELJORD ===\n");
        for (int i = 0; i < areas.size(); i++) {
            String nome = getAreaNome(i);
            boolean desbloqueada = i < unlocked;
            boolean atual = i == areaAtualIndex;
            String tag = desbloqueada ? (atual ? ">> " + nome + " <<" : nome) : "[BLOQUEADA] " + nome;
            sb.append(String.format("%d) %s%n", i + 1, tag));
        }
        return sb.toString();
    }
}
