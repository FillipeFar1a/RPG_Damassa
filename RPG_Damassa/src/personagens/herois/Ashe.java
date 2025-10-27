package personagens.herois;

import personagens.Personagem;
import itens.base.EfeitoPorTurno;

<<<<<<< Updated upstream
public class Ashe extends Personagem {
=======
import java.util.Random;
import java.util.Scanner;

public class Ashe extends Personagem {

    // Estado das flechas congeladas (é ligado/desligado automaticamente pelo efeito temporário)
    private boolean flechasCongeladasAtivas = false;

    private final Random random = new Random();

>>>>>>> Stashed changes
    public Ashe() {
        super("Ashe", "Arqueira", 30, 18, 7, 4, 1, 20);
    }
    @Override public String[] intro() {
        return new String[]{
                "Ashe sente o vento cortar o gelo de Freljord.",
                "As tribos estão inquietas; os Flagelos tomam as rotas de caça.",
                "Se o trovão despertar, não restará reino para liderar."
        };
    }
<<<<<<< Updated upstream
=======

    @Override
    public void usarHabilidade(Personagem alvo) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEscolha uma habilidade:");
        System.out.println("1 - Enxurrada de Flechas (3 tiros, 70% do ATK cada; pode congelar se estiver encantada) - 4 PM");
        System.out.println("2 - Flechas Congeladas (encanta por 3 turnos; ataques têm 25% de congelar) - 6 PM");
        System.out.println("3 - Disparo Preciso (golpe pesado ~180% ATK; 50% de congelar) - 10 PM");
        System.out.print("Digite o número da habilidade: ");

        int escolha = scanner.nextInt();

        int custo = switch (escolha) {
            case 1 -> 4;
            case 2 -> 6;
            case 3 -> 10;
            default -> 0;
        };

        if (this.getPm() < custo) {
            System.out.println("Mana insuficiente! (" + this.getPm() + "/" + this.getPmMax() + ")");
            return;
        }

        this.gastarMana(custo);

        switch (escolha) {
            case 1 -> usarEnxurradaDeFlechas(alvo);
            case 2 -> ativarFlechasCongeladas();
            case 3 -> usarDisparoPreciso(alvo);
            default -> System.out.println("Ashe hesita e abaixa o arco...");
        }

        System.out.println("PM restante: " + this.getPm() + "/" + this.getPmMax());
    }

    // ================== Habilidades ==================

    /** 3 tiros de ~70% do ATK cada, enviando dano BRUTO para receberDano. */
    private void usarEnxurradaDeFlechas(Personagem alvo) {
        System.out.println(this.getNome() + " dispara uma ENXURRADA DE FLECHAS!");

        for (int i = 1; i <= 3; i++) {
            int danoBruto = (int) Math.round(this.getAtkEfetivo() * 0.70);
            // (opcional) pequena variação: +/- 1
            danoBruto += random.nextInt(3) - 1;

            if (danoBruto < 1) danoBruto = 1;

            alvo.receberDano(danoBruto);
            System.out.println("Flecha " + i + " acerta " + alvo.getNome() + " causando " + danoBruto + " de dano bruto!");

            // Chance de congelar se estiver com flechas encantadas
            if (flechasCongeladasAtivas) {
                int chance = random.nextInt(100); // 0..99
                if (chance < 25) {
                    alvo.setCongelado(true);
                    System.out.println(alvo.getNome() + " foi CONGELADO pelas flechas gélidas!");
                    // se congelou, não precisa interromper os tiros (mas pode, se quiser)
                }
            }

            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Encanta o arco por 3 turnos. Enquanto ativo, ataques têm 25% de congelar.
     * Usa um EfeitoPorTurno temporário para manter/desligar automaticamente.
     */
    private void ativarFlechasCongeladas() {
        System.out.println(this.getNome() + " encanta o arco com gelo! Ataques podem congelar por 3 turnos.");

        // Liga imediatamente
        flechasCongeladasAtivas = true;

        // Registra um efeito temporário de 3 turnos que mantém ligado e desliga ao expirar
        this.adicionarEfeito(new EfeitoPorTurno() {
            @Override
            public void aoInicioDoTurno(Personagem self, Personagem adversario) {
                // Enquanto o efeito existir, mantém ativo
                flechasCongeladasAtivas = true;
            }

            @Override
            public String toString() {
                return "Encantamento: Flechas Congeladas";
            }
        }, 3);
    }

    /**
     * Golpe pesado (~180% ATK) com 50% de chance de congelar.
     * Envia dano BRUTO para receberDano; DEF é aplicada lá dentro (evitando “dupla DEF”).
     */
    private void usarDisparoPreciso(Personagem alvo) {
        int danoBruto = (int) Math.round(this.getAtkEfetivo() * 1.80);
        // pequena variação aleatória
        danoBruto += random.nextInt(3) - 1;
        if (danoBruto < 1) danoBruto = 1;

        System.out.println(this.getNome() + " dispara uma FLECHA PRECISA!");
        alvo.receberDano(danoBruto);
        System.out.println(alvo.getNome() + " sofreu " + danoBruto + " de dano bruto!");

        // 50% de chance de congelar
        if (random.nextInt(100) < 50) {
            alvo.setCongelado(true);
            System.out.println(alvo.getNome() + " foi CONGELADO pelo impacto congelante!");
        }
    }
>>>>>>> Stashed changes
}
