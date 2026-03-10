package cyv.app.game.blueprints.contents;

import com.badlogic.gdx.graphics.Texture;
import cyv.app.Skydouser;
import cyv.app.game.blueprints.AbstractBlueprint;
import cyv.app.game.components.player.common.UnitDropletTurret;

public class BlueprintDropletTurret extends AbstractBlueprint<UnitDropletTurret> {
    public BlueprintDropletTurret(Skydouser game) {
        super(game, "Droplet Turret");
    }

    @Override
    public int getCost() {
        return 10;
    }

    @Override
    public int getCooldown() {
        return 20 * 5;
    }

    @Override
    public Texture getTexture() {
        return getGame().getAssets().getTexture("blueprint_droplet_turret");
    }

    @Override
    public UnitDropletTurret produce(float x, float y) {
        return new UnitDropletTurret(x, y);
    }

    @Override
    public String getHologramRendererName() {
        return "unit_droplet_turret";
    }
}
