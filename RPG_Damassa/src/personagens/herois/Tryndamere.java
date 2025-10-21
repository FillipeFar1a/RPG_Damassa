package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Tryndamere extends Personagem {

    private boolean furiaImortalAtiva = false;
    private int turnosFuria = 0;
    private int turnosDebuff = 0;
    private Random random = new Random();

    public Tryndamere() {
        super("Tryndamere", "Guerreiro", 36, 10, 9, 3, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "A fúria de Tryndamere não encontra descanso.",
                "Pesadelos com um rugido antigo o perseguem.",
                "Hoje, ele decidiu perseguir o trovão até o fim."
        };
    }
    // Atualiza buffs/debuffs por turno
    public void atualizarEfeitos() {
        if (furiaImortalAtiva) {
            turnosFuria--;
            if (turnosFuria <= 0) {
                furiaImortalAtiva = false;
                System.out.println("A Fúria Imortal de Tryndamere se dissipa — a morte volta a ser uma ameaça.");
            }
        }
        if (turnosDebuff > 0) {
            turnosDebuff--;
            if (turnosDebuff == 0) {
                System.out.println("A armadura do inimigo se regenera parcialmente.");
            }
        }
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Tryndamere:");
        System.out.println("1 - Corte Giratório (140% dano, ignora 25% da defesa) - 6 PM");
        System.out.println("2 - Rasgo Implacável (120% dano e reduz defesa inimiga por 3 turnos) - 8 PM");
        System.out.println("3 - Fúria Imortal (não pode morrer por 2 turnos) - 12 PM");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 6;
            case 2 -> 8;
            case 3 -> 12;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        switch (escolha) {
            case 1 -> { // Corte Giratório
                int defesaIgnorada = (int) (alvo.getDef() * 0.75); //
                int dano = (int) (this.getAtk() * 1.4) - defesaIgnorada;
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " executa um Corte Giratório devastador!");
                System.out.println(alvo.getNome() + " sofre " + dano + " de dano!");
            }

            case 2 -> { // Dilacerar
                int dano = (int) (this.getAtk() * 1.2) - alvo.getDef();
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " Dilacera o inimigo, rasgando a armadura inimiga!");
                System.out.println(alvo.getNome() + " sofre " + dano + " de dano!");
                alvo.setDef(Math.max(0, alvo.getDef() - 4));
                turnosDebuff = 3;
                System.out.println(alvo.getNome() + " tem sua defesa reduzida por 3 turnos!");
            }

            case 3 -> { // Fúria Imortal
                if (!furiaImortalAtiva) {
                    furiaImortalAtiva = true;
                    turnosFuria = 2;
                    System.out.println(this.getNome() + " entra em FÚRIA IMORTAL!");
                    System.out.println("Durante 2 turnos, ele desafia a morte em pura raiva!");
                } else {
                    System.out.println("Tryndamere já está em estado de Fúria Imortal!");
                }
            }

            default -> System.out.println("Tryndamere hesita, consumido pela raiva...");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // Sobrescreve receberDano para considerar a Fúria Imortal
    @Override
    public void receberDano(int danoBruto) {
        int pvAntes = this.getPv();
        super.receberDano(danoBruto);
        if (furiaImortalAtiva && this.getPv() <= 0 && pvAntes > 0) {
            this.setPv(1); // mantém 1 ponto de vida
            System.out.println(this.getNome() + " resiste à morte pela força da FÚRIA IMORTAL!");
        }
    }
}

