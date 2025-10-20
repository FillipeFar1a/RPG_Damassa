package mundo;

import java.io.Serializable;

/** Estado do jogador em UMA área. */
public class AreaProgress implements Serializable {
    private final AreaDef def;
    private int exploracoes;        // quantas salas já explorou
    private final int minExplorar;  // mínimo p/ liberar avanço
    private final int maxSalas;     // teto de salas desta área

    public AreaProgress(AreaDef def) {
        this.def = def;
        this.minExplorar = def.getMinExplorar();
        this.maxSalas = def.getMaxSalas();
        this.exploracoes = 0;
    }

    public AreaDef def() { return def; }
    public int getExploracoes() { return exploracoes; }
    public int getMinExplorar() { return minExplorar; }
    public int getMaxSalas() { return maxSalas; }

    public boolean podeExplorar() {
        return exploracoes < maxSalas;
    }

    /** Registra uma exploração (sala). Retorna false se já bateu limite. */
    public boolean explorarUmaSala() {
        if (!podeExplorar()) return false;
        exploracoes++;
        return true;
    }

    /** Já cumpriu o mínimo p/ poder avançar de área? */
    public boolean liberouAvanco() {
        return exploracoes >= minExplorar;
    }

    /** Área completamente esgotada (todas as salas)? */
    public boolean esgotada() {
        return exploracoes >= maxSalas;
    }

    @Override
    public String toString() {
        return def.getNome() + " — " + exploracoes + "/" + maxSalas + " salas (min p/ avançar: " + minExplorar + ")";
    }
}
