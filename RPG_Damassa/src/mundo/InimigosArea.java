package mundo;

import personagens.inimigos.*;
import personagens.Personagem;
import java.util.*;

/**
 * Controla quais inimigos aparecem em cada área e suas chances de spawn.
 */
public class InimigosArea {

    /** Estado dinâmico do mundo (pode futuramente ser salvo no progresso do jogador). */
    private static boolean lissandraDerrotada = false;
    private static int inimigosDerrotadosAposLissandra = 0;
    private static boolean sylasDerrotado = false;
    private static boolean dariusDerrotado = false;
    private static boolean trundleDerrotado = false;

    private static class InimigoChance {
        Class<? extends Personagem> tipo;
        double chance; // chance em porcentagem

        InimigoChance(Class<? extends Personagem> tipo, double chance) {
            this.tipo = tipo;
            this.chance = chance;
        }
    }

    public static void marcarLissandraDerrotada() {
        lissandraDerrotada = true;
        inimigosDerrotadosAposLissandra = 0;
        System.out.println("A queda de Lissandra abala as terras gélidas... O portão se abre.");
    }

    public static void registrarInimigoDerrotado() {
        if (lissandraDerrotada && inimigosDerrotadosAposLissandra < 10) {
            inimigosDerrotadosAposLissandra++;
        }
    }

    /**
     * Retorna o inimigo sorteado com base nas probabilidades da área.
     * Kindred tem 1,5% de chance de aparecer em qualquer área.
     * Volibear só aparece após Lissandra ser derrotada, com chance crescente.
     */
    public static Personagem sortearInimigo(int numeroArea) {
        Random random = new Random();

        // --- Chance global de Kindred (1,5%) ---
        if (random.nextDouble() * 100 < 1.5) {
            try {
                System.out.println("Um presságio sombrio se aproxima... Kindred apareceu!");
                return new Kindred();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // --- Caso contrário, sorteia inimigo normal da área ---
        List<InimigoChance> chances = getChancesDaArea(numeroArea);

        // Chance dinâmica de Volibear
        if (lissandraDerrotada) {
            double chanceVolibear = Math.min(inimigosDerrotadosAposLissandra * 10, 100); // +10% por inimigo até 100%
            chances.add(new InimigoChance(Volibear.class, chanceVolibear));
        }

        double total = chances.stream().mapToDouble(c -> c.chance).sum();
        double roleta = random.nextDouble() * total;
        double acumulado = 0.0;

        for (InimigoChance c : chances) {
            acumulado += c.chance;
            if (roleta <= acumulado) {
                try {
                    return c.tipo.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Define os inimigos e suas chances por área.
     */
    private static List<InimigoChance> getChancesDaArea(int numeroArea) {
        List<InimigoChance> lista = new ArrayList<>();

        switch (numeroArea) {
            case 3 -> {
                lista.add(new InimigoChance(FlageloArqueiro.class, 15));
                lista.add(new InimigoChance(FlageloGuerreiro.class, 15));
                lista.add(new InimigoChance(FlageloGigante.class, 10));
                lista.add(new InimigoChance(FlageloMago.class, 10));
                lista.add(new InimigoChance(Darius.class, 15));
                lista.add(new InimigoChance(Trundle.class, 15));
                lista.add(new InimigoChance(FlageloSupremo.class, 20));
            }
            case 4 -> {
                lista.add(new InimigoChance(FlageloArqueiro.class, 10));
                lista.add(new InimigoChance(FlageloGuerreiro.class, 10));
                lista.add(new InimigoChance(FlageloGigante.class, 10));
                lista.add(new InimigoChance(FlageloMago.class, 10));
                lista.add(new InimigoChance(Lissandra.class, 20));
                lista.add(new InimigoChance(Sylas.class, 20));
                lista.add(new InimigoChance(FlageloSupremo.class, 30));
                // Volibear será adicionado dinamicamente após Lissandra
            }
        }

        return lista;
    }
}
