package itens.base;

import personagens.Personagem;

@FunctionalInterface
public interface EfeitoPorTurno extends java.io.Serializable {
    /** Chamado no início do turno de 'self'. */
    void aoInicioDoTurno(Personagem self, Personagem adversario);
}
