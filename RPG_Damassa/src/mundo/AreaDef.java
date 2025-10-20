package mundo;

import java.io.Serializable;

public enum AreaDef implements Serializable {
    BASE_MONTANHA("Base da Montanha",   3, 6),
    MASMORRA("Interior da Montanha",    4, 8),
    TOPO_MONTANHA("Topo da Montanha",   5, 10),
    SALAO_DOS_FLAGELOS("Salão dos Flagelos", 1, 3); // libera só no final

    private final String nome;
    private final int minExplorar;
    private final int maxSalas;

    AreaDef(String nome, int minExplorar, int maxSalas) {
        this.nome = nome;
        this.minExplorar = minExplorar;
        this.maxSalas = maxSalas;
    }

    public String getNome() { return nome; }
    public int getMinExplorar() { return minExplorar; }
    public int getMaxSalas() { return maxSalas; }
}
