package cyv.app.game.components.player.categories;

public interface IWaterProducer {
    long getNextProductionTime();

    int getMinProductionTime();

    int getMaxProductionTime();

    int getStartingProductionTime();
}
