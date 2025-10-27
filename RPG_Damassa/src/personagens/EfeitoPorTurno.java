package personagens;

import java.io.Serializable;

/** Efeito chamado no INÍCIO do turno de um personagem. */
@FunctionalInterface
public interface EfeitoPorTurno extends Serializable {
    void aoInicioDoTurno(Personagem self, Personagem adversario);
}
