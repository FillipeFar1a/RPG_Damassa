package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class LeeSin extends Personagem {

    private boolean escudoAtivo = false;
    private int turnosEscudo = 0;
    private boolean buffAtivo = false;
    private double curaPorDano = 0.0;
    private int turnosBuff = 0;
    private boolean marcouAlvo = false; // para combo da Onda Sônica
    private Random random = new Random();

    public LeeSin() {
        super("Lee Sin", "Monge Cego", 32, 16, 8, 4, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Lee Sin não vê o gelo, mas sente o peso do silêncio.",
                "O mundo segura a respiração antes do trovão.",
                "Ele caminha em fé, para quebrar o ciclo da fúria."
        };
    }

    public void atualizarEfeitos() {
        if (escudoAtivo) {
            turnosEscudo--;
            if (turnosEscudo <= 0) {
                escudoAtivo = false;
                System.out.println("A energia protetora de Lee Sin se dissipa.");
            }
        }

        if (buffAtivo) {
            turnosBuff--;
            if (turnosBuff <= 0) {
                buffAtivo = false;
                this.setAtk(this.getAtk() - 3);
                this.setDef(this.getDef() - 2);
                System.out.println("O foco interior de Lee Sin se desfaz.");
            }
        }

        if (marcouAlvo) {
            System.out.println("Lee Sin ainda sente o eco do som do inimigo...");
        }
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Lee Sin:");
        System.out.println("1 - Onda Sônica / Golpe Resonante (combo de 2 etapas) - Custo: 6 PM");
        System.out.println("2 - Safeguard (escudo e cura) - Custo: 8 PM");
        System.out.println("3 - Fúria do Dragão (chute devastador, 180% dano e atordoa) - Custo: 10 PM");
        System.out.println("4 - Foco Interior (buff +3 ATK e +2 DEF por 3 turnos) - Custo: 7 PM");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 6;
            case 2 -> 8;
            case 3 -> 10;
            case 4 -> 7;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Energia espiritual insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        switch (escolha) {
            case 1 -> { // Onda Sônica / Golpe Resonante
                int dano; // ✅ declarado antes do if

                if (!marcouAlvo) {
                    dano = (int) (this.getAtk() * 1.3) - alvo.getDef();
                    if (dano < 0) dano = 0;
                    marcouAlvo = true;
                    System.out.println(this.getNome() + " lança uma ONDA SÔNICA!");
                    System.out.println(alvo.getNome() + " sofre " + dano + " de dano!");
                    System.out.println("O som do inimigo ecoa na mente de Lee Sin...");
                } else {
                    dano = (int) (this.getAtk() * 2.0) - alvo.getDef();
                    if (dano < 0) dano = 0;
                    marcouAlvo = false;
                    System.out.println(this.getNome() + " segue o som e executa o GOLPE RESONANTE!");
                    System.out.println(alvo.getNome() + " sofre " + dano + " de dano crítico!");
                }
                alvo.setPv(alvo.getPv() - dano);
                if (escudoAtivo && dano > 0) {
                    int cura = (int) (dano * curaPorDano);
                    this.setPv(this.getPv() + cura);
                    System.out.println("Safeguard: Lee Sin canaliza a energia do impacto e recupera " + cura + " PV!");
                }
            }

            case 2 -> { // Safeguard
                if (!escudoAtivo) {
                    escudoAtivo = true;
                    turnosEscudo = 2;
                    int cura = (int) (this.getPvMax() * 0.1);
                    this.setPv(this.getPv() + cura);
                    System.out.println(this.getNome() + " canaliza o SAFEGUARD! Reduz dano e cura " + cura + " PV.");
                } else {
                    System.out.println("O Safeguard já está ativo!");
                }
            }

            case 3 -> { // Foco Interior
                if (!buffAtivo) {
                    buffAtivo = true;
                    turnosBuff = 3;
                    this.setAtk(this.getAtk() + 3);
                    this.setDef(this.getDef() + 2);
                    System.out.println(this.getNome() + " entra em estado de FOCO INTERIOR!");
                    System.out.println("Seu corpo e mente estão em perfeita harmonia.");
                } else {
                    System.out.println("Lee Sin já está focado.");
                }
            }

            default -> System.out.println("Lee Sin respira fundo e aguarda o momento certo...");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    @Override
    public void receberDano(int danoBruto) {
        if (escudoAtivo) {
            danoBruto /= 2;
        }
        super.receberDano(danoBruto);
    }
}

