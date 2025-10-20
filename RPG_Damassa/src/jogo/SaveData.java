package jogo;

import java.io.Serializable;
import mundo.WorldProgress;
import personagens.Personagem;

/** Pacote único de save (jogador + mundo). */
public class SaveData implements Serializable {
    private final Personagem jogador;
    private final WorldProgress mundo;

    public SaveData(Personagem jogador, WorldProgress mundo) {
        this.jogador = jogador;
        this.mundo = mundo;
    }

    public Personagem getJogador() { return jogador; }
    public WorldProgress getMundo() { return mundo; }
}
