package mundo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Mundo com desbloqueio progressivo:
 * - Começa com 1 área desbloqueada.
 * - Ao cumprir o mínimo na área atual, desbloqueia a próxima (até a 3ª).
 * - “Salão dos Flagelos” desbloqueia quando as 3 primeiras estiverem completas.
 */
public class WorldProgress implements Serializable {

    // Se true, exige esgotar TODAS as salas das 3 primeiras áreas para liberar o Salão.
    // Se false, libera ao cumprir o mínimo de cada uma.
    private static final boolean REQUIRE_FULL_CLEAR = true;

    private final List<AreaProgress> areas = new ArrayList<>();
    private int areaAtualIndex = 0;
    private int unlockedCount = 1; // quantas áreas estão liberadas para exploração (>=1)

    public WorldProgress() {
        areas.add(new AreaProgress(AreaDef.BASE_MONTANHA));
        areas.add(new AreaProgress(AreaDef.MASMORRA));
        areas.add(new AreaProgress(AreaDef.TOPO_MONTANHA));
        areas.add(new AreaProgress(AreaDef.SALAO_DOS_FLAGELOS));
    }

    public List<AreaProgress> getAreas() { return areas; }
    public int getAreaAtualIndex() { return areaAtualIndex; }
    public AreaProgress getAreaAtual() { return areas.get(areaAtualIndex); }

    public boolean isUnlocked(int index) { return index >= 0 && index < unlockedCount; }
    public int getUnlockedCount() { return unlockedCount; }

    /** Explorar sala em uma área (somente se desbloqueada). */
    public boolean explorarNaArea(int index) {
        if (!isUnlocked(index)) return false;
        boolean ok = areas.get(index).explorarUmaSala();
        if (ok) checarDesbloqueios(); // toda exploração pode destravar algo
        return ok;
    }

    /** Pode avançar área atual -> próxima (se já estiver desbloqueada). */
    public boolean podeAvancar() {
        // avançar o foco só faz sentido se existir próxima já desbloqueada
        return getAreaAtual().liberouAvanco() && (areaAtualIndex + 1) < unlockedCount;
    }

    /** Move o foco para a próxima área desbloqueada. */
    public boolean avancarArea() {
        if (!podeAvancar()) return false;
        areaAtualIndex++;
        return true;
    }

    /** Mapa textual com bloqueios visíveis. */
    public String mapa() {
        StringBuilder sb = new StringBuilder("=== MAPA / ÁREAS (desbloqueadas) ===\n");
        for (int i = 0; i < unlockedCount; i++) {
            AreaProgress ap = areas.get(i);
            String marcador = (i == areaAtualIndex) ? " [ATUAL]" : "";
            sb.append(String.format("%d) %s%s\n", i + 1, ap.toString(), marcador));
        }
        return sb.toString();
    }


    /** Checa condições de desbloqueio após cada exploração. */
    public void checarDesbloqueios() {
        // 1) Desbloqueio linear das áreas 1->2->3 ao cumprir MIN da área atual
        // Só tenta desbloquear a próxima se ainda não chegou na 3ª
        if (unlockedCount < 3) {
            AreaProgress atual = areas.get(unlockedCount - 1); // última desbloqueada
            if (atual.liberouAvanco()) {
                unlockedCount = Math.min(3, unlockedCount + 1);
            }
        }

        // 2) Desbloqueio do Salão dos Flagelos
        if (unlockedCount < 4 && condicaoParaSalao()) {
            unlockedCount = 4;
        }
    }

    private boolean condicaoParaSalao() {
        // verifica as três primeiras áreas
        for (int i = 0; i < 3; i++) {
            AreaProgress ap = areas.get(i);
            if (REQUIRE_FULL_CLEAR) {
                if (!ap.esgotada()) return false; // precisa 100%
            } else {
                if (!ap.liberouAvanco()) return false; // basta o mínimo
            }
        }
        return true;
    }

    /** Mundo totalmente esgotado (todas as áreas 100%). */
    public boolean mundoEsgotado() {
        for (AreaProgress ap : areas) if (!ap.esgotada()) return false;
        return true;
    }

    public boolean setAreaAtual(int index) {
        if (!isUnlocked(index)) return false;
        if (index < 0 || index >= areas.size()) return false;
        this.areaAtualIndex = index;
        return true;
    }

}
