package itens.consumiveis;

import itens.Item;
import itens.base.Usavel;
import personagens.Personagem;

public class PocaoCuraMedia extends Item implements Usavel {
    public static final int CURA = 15;

    public PocaoCuraMedia(int quantidade) {
        super("Poção de Cura Média", "Restaura " + CURA + " PV", "cura", quantidade);
    }

    @Override
    public boolean usarEm(Personagem alvo) {
        alvo.curar(CURA);
        return true;
    }
}
