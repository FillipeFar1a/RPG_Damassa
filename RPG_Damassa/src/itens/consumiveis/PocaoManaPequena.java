package itens.consumiveis;

import itens.Item;
import itens.base.Usavel;
import personagens.Personagem;

public class PocaoManaPequena extends Item implements Usavel {
    public static final int MANA = 5;

    public PocaoManaPequena(int quantidade) {
        super("Poção de Mana Pequena", "Recupera " + MANA + " PM", "mana", quantidade);
    }

    @Override
    public boolean usarEm(Personagem alvo) {
        // Requer em Personagem: recuperarMana(int qtd)
        //alvo.recuperarMana(MANA);
        return true;
    }
}
