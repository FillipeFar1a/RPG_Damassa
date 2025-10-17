package itens.base;

import personagens.Personagem;

/** Itens de equipamento com efeitos persistentes enquanto equipados. */
public interface Equipavel {
    void equipar(Personagem p);
    void desequipar(Personagem p);
    String slot(); // ex.: "peitoral", "cabeca", "maos"...
}
