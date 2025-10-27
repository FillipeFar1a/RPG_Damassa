package mundo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorldProgress implements Serializable {

    // Definição “leve” de uma área
    public static class AreaDef implements Serializable {
        private final String nome;
        private final int salasMax;
        private final int minExplorarParaAvancar;

        public AreaDef(String nome, int salasMax, int minExplorarParaAvancar) {
            this.nome = nome;
            this.salasMax = Math.max(1, salasMax);
            this.minExplorarParaAvancar = Math.max(1, minExplorarParaAvancar);
        }
        public String getNome() { return nome; }
        public int getSalasMax() { return salasMax; }
        public int getMinExplorarParaAvancar() { return minExplorarParaAvancar; }
    }

    // “Wrapper” com estado de progresso por área
    public static class Area implements Serializable {
        private final AreaDef def;
        private int exploradas = 0;

        public Area(AreaDef def) { this.def = def; }
        public AreaDef def() { return def; }
        public int getExploradas() { return exploradas; }
        public void incrementarExploradas() { exploradas = Math.min(def.getSalasMax(), exploradas + 1); }
        public boolean esgotada() { return exploradas >= def.getSalasMax(); }
    }

    private final List<Area> areas = new ArrayList<>();
    private int unlockedCount = 1; // quantas áreas estão liberadas (começa com 1)
    private int areaAtualIndex = 0;

    public WorldProgress() {
        // Monte aqui os nomes das áreas do teu jogo
        areas.add(new Area(new AreaDef("Campos Gelados",        6, 3)));
        areas.add(new Area(new AreaDef("Fendas do Gelo",        6, 3)));
        areas.add(new Area(new AreaDef("Garganta dos Ventos",   7, 4)));
        areas.add(new Area(new AreaDef("Ruínas Ancestrais",     7, 4)));
        areas.add(new Area(new AreaDef("Montanhas Tempestuosas",8, 5))); // última → Volibear/Boss final
    }

    // ===== Consultas básicas =====
    public int getAreaAtualIndex() { return areaAtualIndex; }
    public Area getAreaAtual() { return areas.get(areaAtualIndex); }
    public List<Area> getAreas() { return areas; }
    public int getUnlockedCount() { return unlockedCount; }

    // ===== Trocar área atual (apenas entre as desbloqueadas) =====
    public void setAreaAtual(int idx) {
        if (idx >= 0 && idx < unlockedCount) {
            areaAtualIndex = idx;
        }
    }

    /**
     * Explorar consome “uma sala” da área, respeitando o limite.
     * @return true se conseguiu explorar, false se já esgotou.
     */
    public boolean explorarNaArea(int idx) {
        if (idx < 0 || idx >= unlockedCount) return false;
        Area a = areas.get(idx);
        if (a.esgotada()) return false;
        a.incrementarExploradas();
        return true;
    }

    /**
     * Pode avançar se já explorou o mínimo exigido da área atual.
     */
    public boolean podeAvancar() {
        Area a = getAreaAtual();
        return a.getExploradas() >= a.def().getMinExplorarParaAvancar();
    }

    /**
     * Avança a área:
     * - se já houver outra área liberada à frente, apenas muda o foco
     * - senão, libera a próxima (se existir) e vai pra ela
     */
    public void avancarArea() {
        // se já estamos na última área liberada
        if (areaAtualIndex == unlockedCount - 1) {
            // tenta liberar a próxima
            if (unlockedCount < areas.size()) {
                unlockedCount++;
                areaAtualIndex = unlockedCount - 1;
            } else {
                // já é a última do mundo; fica onde está
            }
        } else {
            // ainda havia área liberada à frente — só navega
            areaAtualIndex = Math.min(areaAtualIndex + 1, unlockedCount - 1);
        }
    }

    /**
     * Retorna uma string com o mapa das áreas liberadas e progresso.
     */
    public String mapa() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MAPA DE FRELJORD ===\n");
        for (int i = 0; i < unlockedCount; i++) {
            Area a = areas.get(i);
            sb.append(String.format("%d) %s  [%d/%d exploradas]%s\n",
                    i + 1,
                    a.def().getNome(),
                    a.getExploradas(),
                    a.def().getSalasMax(),
                    (i == areaAtualIndex ? "  <- atual" : "")
            ));
        }
        if (unlockedCount < areas.size()) {
            sb.append(String.format("\n(Áreas bloqueadas: %d)\n", areas.size() - unlockedCount));
        }
        return sb.toString();
    }
}
