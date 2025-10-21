package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Braum extends Personagem {

    private boolean escudoAtivo = false;
    private int turnosEscudo = 0;
    private int turnosBuff = 0;
    private Random random = new Random();

    public Braum() {
        super("Braum", "Guardião", 40, 14, 6, 9, 1, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Braum ergue seu escudo com um sorriso: 'Atrás de mim, amigos!'.",
                "Nenhum golpe atravessa seu coração de gelo e aço.",
                "O Freljord não cairá enquanto Braum resistir."
        };
    }
    public void atualizarEfeitos() {
        if (escudoAtivo) {
            turnosEscudo--;
            if (turnosEscudo <= 0) {
                escudoAtivo = false;
                System.out.println("A Muralha de Braum se dissipa, ele abaixa o escudo.");
            }
        }
        if (turnosBuff > 0) {
            turnosBuff--;
            if (turnosBuff == 0) {
                this.setDef(this.getDef() - 5);
                System.out.println("O bônus de defesa de Braum desaparece.");
            }
        }
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Braum:");
        System.out.println("1 - Pancada (150% de dano, 20% chance de atordoar)");
        System.out.println("2 - O Escudo de Freljord (reduz 50% do dano recebido por 2 turnos)");
        System.out.println("3 - Coração do Norte (cura e aumenta a defesa por 3 turnos)");
        System.out.print("Digite o número da habilidade: ");
        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 4;
            case 2 -> 8;
            case 3 -> 6;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        switch (escolha) {
            case 1 -> { // Pancada
                int dano = (int) (this.getAtk() * 1.5) - alvo.getDef();
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " desfere uma grande Pancada!");
                System.out.println(alvo.getNome() + " sofre " + dano + " de dano!");

                int chanceAtordoar = random.nextInt(100);
                if (chanceAtordoar < 20) {
                    alvo.setCongelado(true); // reutiliza o boolean 'congelado' como status de "atordoado"
                    System.out.println(alvo.getNome() + " foi ATORDOADO e perderá o próximo turno!");
                }
            }

            case 2 -> { // O coração de freljord
                if (!escudoAtivo) {
                    escudoAtivo = true;
                    turnosEscudo = 2;
                    System.out.println(this.getNome() + " ergue seu Escudo! Dano recebido reduzido por 2 turnos.");
                } else {
                    System.out.println("O escudo já está ativo!");
                }
            }

            case 3 -> { // Coração do Norte
                if (turnosBuff == 0) {
                    int cura = (int) (this.getPvMax() * 0.2);
                    this.setPv(this.getPv() + cura);
                    this.setDef(this.getDef() + 5);
                    turnosBuff = 3;
                    System.out.println(this.getNome() + " invoca os espíritos de Freljord! Recupera " + cura + " PV e aumenta sua defesa.");
                } else {
                    System.out.println("O efeito do Coração do Norte ainda está ativo!");
                }
            }

            default -> System.out.println("Habilidade inválida");
        }
    }
    @Override
    public void receberDano(int danoBruto) {
        if (escudoAtivo) {
            danoBruto /= 2;
        }
        super.receberDano(danoBruto);
    }
}
