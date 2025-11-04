package personagens.inimigos;

import personagens.Personagem;

public class Kindred extends Personagem {

    // ✔ Construtor sem argumentos (para spawns globais)
    public Kindred() {
        this(5); // nível/escala padrão de spawn; ajuste se quiser
    }

    // ✔ Construtor com escala (usado pelo Ryze ao teletransportar)
    public Kindred(int escala) {
        super(
                "Kindred",
                "Aspectos da Morte",
                80 + 10 * Math.max(1, escala), // PV
                50,                             // PM (não usa muito, mas ok)
                25 + 2 * Math.max(1, escala),   // ATK
                8 + Math.max(0, escala / 2),    // DEF
                Math.max(1, escala),
                50
        );
    }

    @Override
    public String[] intro() {
        return new String[]{
                "— O Caçador ri. O Cordeiro sussurra: corra.",
                "Você sente que a morte já decidiu."
        };
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        System.out.println(getNome() + " marca " + alvo.getNome() + " com a Flecha da Morte.");
        System.out.println("Não há esquiva, não há refúgio...");
        alvo.setPv(0); // IK
    }
}
