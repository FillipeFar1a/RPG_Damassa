package itens.consumiveis;

import itens.Item;
import itens.base.Usavel;
import personagens.Personagem;

public class PocaoCuraGrande extends Item implements Usavel {
    public static final int CURA = 25;

    public PocaoCuraGrande(int quantidade) {
        super("Poção de Cura Grande", "Restaura " + CURA + " PV", "cura", quantidade);
    }

    @Override
    public boolean usarEm(Personagem alvo) {
        alvo.curar(CURA);
        return true;
    }
}
