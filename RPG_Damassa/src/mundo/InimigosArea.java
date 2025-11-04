package mundo;

import personagens.inimigos.*;
import personagens.Personagem;
import java.util.*;

/**
 * Controla quais inimigos aparecem em cada área e suas chances de spawn.
 * Bosses únicos: Darius, Trundle, Sylas e Lissandra só aparecem 1x.
 */
public class InimigosArea {

    // ===== Estado dinâmico do mundo =====
    private static boolean lissandraDerrotada = false;
    private static int inimigosDerrotadosAposLissandra = 0;
    private static boolean sylasDerrotado = false;
    private static boolean dariusDerrotado = false;
    private static boolean trundleDerrotado = false;

    private static class InimigoChance {
        Class<? extends Personagem> tipo;
        double chance; // porcentagem
        InimigoChance(Class<? extends Personagem> tipo, double chance) {
            this.tipo = tipo;
            this.chance = chance;
        }
    }

    // ==== Hooks públicos para eventos de história/combate ====

    public static void marcarLissandraDerrotada() {
        lissandraDerrotada = true;
        inimigosDerrotadosAposLissandra = 0;
        System.out.println("A queda de Lissandra abala as terras gélidas... O portão se abre.");
    }

    /** Chame ao fim de um combate VENCIDO pelo jogador, com o inimigo morto. */
    public static void notificarMorte(Personagem inimigo) {
        if (inimigo == null) return;

        // Marca bosses únicos
        if (inimigo instanceof Darius) {
            dariusDerrotado = true;
        } else if (inimigo instanceof Trundle) {
            trundleDerrotado = true;
        } else if (inimigo instanceof Sylas) {
            sylasDerrotado = true;
        } else if (inimigo instanceof Lissandra) {
            if (!lissandraDerrotada) marcarLissandraDerrotada();
        }

        // Contagem dinâmica pós-Lissandra (para Volibear) — até 10
        if (lissandraDerrotada && inimigosDerrotadosAposLissandra < 10) {
            inimigosDerrotadosAposLissandra++;
        }
    }

    // ===== Sorteio principal =====
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

        // --- Sorteia inimigo da área (com bosses únicos filtrados) ---
        List<InimigoChance> chances = getChancesDaArea(numeroArea);

        // Chance dinâmica de Volibear após Lissandra
        if (lissandraDerrotada) {
            double chanceVolibear = Math.min(inimigosDerrotadosAposLissandra * 10, 100); // +10% por inimigo até 100%
            chances.add(new InimigoChance(Volibear.class, chanceVolibear));
        }

        // Se por algum motivo ficou sem opções, fallback para mobs básicos
        if (chances.isEmpty()) {
            chances.add(new InimigoChance(FlageloGuerreiro.class, 30));
            chances.add(new InimigoChance(FlageloArqueiro.class, 30));
            chances.add(new InimigoChance(FlageloMago.class, 30));
            chances.add(new InimigoChance(FlageloGigante.class, 10));
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

    // ===== Tabela de spawn por área (filtrando bosses já derrotados) =====
    private static List<InimigoChance> getChancesDaArea(int numeroArea) {
        List<InimigoChance> lista = new ArrayList<>();

        switch (numeroArea) {
            case 3 -> {
                lista.add(new InimigoChance(FlageloArqueiro.class, 15));
                lista.add(new InimigoChance(FlageloGuerreiro.class, 15));
                lista.add(new InimigoChance(FlageloGigante.class, 10));
                lista.add(new InimigoChance(FlageloMago.class, 10));
                if (!dariusDerrotado)  lista.add(new InimigoChance(Darius.class, 15));
                if (!trundleDerrotado) lista.add(new InimigoChance(Trundle.class, 15));
                lista.add(new InimigoChance(FlageloSupremo.class, 20));
            }
            case 4 -> {
                lista.add(new InimigoChance(FlageloArqueiro.class, 10));
                lista.add(new InimigoChance(FlageloGuerreiro.class, 10));
                lista.add(new InimigoChance(FlageloGigante.class, 10));
                lista.add(new InimigoChance(FlageloMago.class, 10));
                if (!lissandraDerrotada) lista.add(new InimigoChance(Lissandra.class, 20));
                if (!sylasDerrotado)     lista.add(new InimigoChance(Sylas.class, 20));
                lista.add(new InimigoChance(FlageloSupremo.class, 30));
            }
            default -> {
                // Outras áreas (se existirem) podem ter mobs genéricos
                lista.add(new InimigoChance(FlageloGuerreiro.class, 40));
                lista.add(new InimigoChance(FlageloArqueiro.class, 30));
                lista.add(new InimigoChance(FlageloMago.class, 30));
            }
        }
        return lista;
    }

    public static boolean isLissandraDerrotada() { return lissandraDerrotada; }
    public static boolean isSylasDerrotado() { return sylasDerrotado; }
    public static boolean isDariusDerrotado() { return dariusDerrotado; }
    public static boolean isTrundleDerrotado() { return trundleDerrotado; }
    public static int getInimigosDerrotadosAposLissandra() { return inimigosDerrotadosAposLissandra; }
}
