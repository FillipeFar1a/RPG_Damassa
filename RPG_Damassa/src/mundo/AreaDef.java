package mundo;

import java.io.Serializable;

/** Define os metadados de uma área do mundo. */
public class AreaDef implements Serializable {
    private final String nome;
    private final int maxSalas;      // teto de salas desta área
    private final int minExplorar;   // mínimo para liberar avanço

    public AreaDef(String nome, int maxSalas, int minExplorar) {
        this.nome = nome;
        this.maxSalas = Math.max(1, maxSalas);
        // minExplorar não pode exceder maxSalas e não pode ser negativo
        this.minExplorar = Math.max(0, Math.min(this.maxSalas, minExplorar));
    }

    public String getNome()         { return nome; }
    public int getMaxSalas()        { return maxSalas; }
    public int getMinExplorar()     { return minExplorar; }

    @Override
    public String toString() {
        return nome + " (salas: " + maxSalas + ", mínimo p/ avançar: " + minExplorar + ")";
    }
}
