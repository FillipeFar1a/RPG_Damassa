package personagens.herois;

import personagens.Personagem;
import java.util.Random;
import java.util.Scanner;

public class Aurora extends Personagem {

    private int turnosBuff = 0; // controla duração de buff de defesa
    private int ataquesConsecutivos = 0;
    private int turnosDebuff = 0; // controla duração de debuff do inimigo

    public Aurora() {
        super("Aurora", "Coelha Safada", 28, 26, 8, 3, 1, 20);
    }

    @Override
    public String[] intro() {
        return new String[]{
                "Aurora lê sinais nas nevascas — padrões que sussurram um nome proibido.",
                "A magia se agita; algo antigo desperta sob o gelo.",
                "Ela parte para selar o que nunca deveria ter sido lembrado."
        };
    }

    public void atualizarEfeitos() {
        if (turnosBuff > 0) {
            turnosBuff--;
            if (turnosBuff == 0) {
                System.out.println("O escudo de cristal de Aurora se desfaz");
                this.setDef(this.getDef() - 5);
            }
        }
    }

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha a habilidade de Aurora:");
        System.out.println("1 - Disparo Arcano (ataque mágico com 180% do ataque)");
        System.out.println("2 - Gelo Verdadeiro (reduz ataque e defesa do inimigo por 2 turnos)");
        System.out.println("3 - Revestimento Cristalino (aumenta a defesa de Aurora por 3 turnos)");
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


        Random random = new Random();

        switch (escolha) {
            case 1 -> { // Rajada Gélida
                int dano = (int) (this.getAtk() * 1.8) - alvo.getDef();
                if (dano < 0) dano = 0;
                alvo.setPv(alvo.getPv() - dano);
                System.out.println(this.getNome() + " conjura um Disparo Arcano!");
                System.out.println(alvo.getNome() + " sofre " + dano + " de dano!");
            }

            case 2 -> { // Nevasca Congelante
                System.out.println(this.getNome() + " invoca pedaços de gelo verdadeiro no inimigo!");
                System.out.println(alvo.getNome() + " tem seu ataque e defesa reduzidos!");
                alvo.setAtk(Math.max(1, alvo.getAtk() - 3));
                alvo.setDef(Math.max(0, alvo.getDef() - 2));
                turnosDebuff = 2;
            }

            case 3 -> { // Escudo de Cristal
                if (turnosBuff == 0) {
                    System.out.println(this.getNome() + " se reveste em CRISTAL!");
                    this.setDef(this.getDef() + 5);
                    turnosBuff = 3;
                } else {
                    System.out.println("O escudo de Aurora já está ativo!");
                }
            }

            default -> System.out.println("Habilidade inválida, Aurora perde o turno!");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());

        }

    private void aplicarPassiva() {
        ataquesConsecutivos++;
        if (ataquesConsecutivos >= 3) {
            ataquesConsecutivos = 0;
            int cura = (int) (this.getPvMax() * 0.15);
            this.setPv(this.getPv() + cura);
            System.out.println(" Coração de Gelo: Aurora canaliza o frio e recupera " + cura + " PV!");
        }
    }
}
