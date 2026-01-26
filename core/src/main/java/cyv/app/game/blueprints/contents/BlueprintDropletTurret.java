package cyv.app.game.blueprints.contents;

import cyv.app.game.blueprints.AbstractBlueprint;
import cyv.app.game.components.player.common.UnitDropletTurret;

public class BlueprintDropletTurret extends AbstractBlueprint<UnitDropletTurret> {
    @Override
    public int getCost() {
        return 10;
    }

    @Override
    public UnitDropletTurret produce(float x, float y) {
        return new UnitDropletTurret(x, y);
    }
}
