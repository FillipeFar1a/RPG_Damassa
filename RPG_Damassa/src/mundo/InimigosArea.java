package mundo;

import personagens.inimigos.*;

import java.util.ArrayList;
import java.util.List;

public class InimigosArea {

    public static List<Class<?>> getInimigosDaArea(int numeroArea) {
        List<Class<?>> inimigos = new ArrayList<>();

        switch (numeroArea) {
            case 1:
                inimigos.add(FlageloArqueiro.class);
                inimigos.add(FlageloGuerreiro.class);
                inimigos.add(FlageloGigante.class);
                inimigos.add(FlageloMago.class);
                inimigos.add(Kindred.class);
                break;

            case 2:
                inimigos.add(FlageloArqueiro.class);
                inimigos.add(FlageloGuerreiro.class);
                inimigos.add(FlageloGigante.class);
                inimigos.add(FlageloMago.class);
                inimigos.add(Kindred.class);
                break;

            case 3:
                inimigos.add(FlageloArqueiro.class);
                inimigos.add(FlageloGuerreiro.class);
                inimigos.add(FlageloGigante.class);
                inimigos.add(FlageloMago.class);
                inimigos.add(Darius.class);
                inimigos.add(Trundle.class);
                inimigos.add(FlageloSupremo.class);
                inimigos.add(Kindred.class);
                break;

            case 4:
                inimigos.add(FlageloArqueiro.class);
                inimigos.add(FlageloGuerreiro.class);
                inimigos.add(FlageloGigante.class);
                inimigos.add(FlageloMago.class);
                inimigos.add(Lissandra.class);
                inimigos.add(Sylas.class);
                inimigos.add(Volibear.class);
                inimigos.add(FlageloSupremo.class);
                inimigos.add(Kindred.class);
                break;

            default:
                // Áreas anteriores ou futuras podem ter outros inimigos
                break;
        }

        return inimigos;
    }
}
