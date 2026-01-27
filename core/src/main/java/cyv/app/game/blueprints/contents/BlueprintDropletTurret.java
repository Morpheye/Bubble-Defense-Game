package cyv.app.game.blueprints.contents;

import com.badlogic.gdx.graphics.Texture;
import cyv.app.BubbleGame;
import cyv.app.game.blueprints.AbstractBlueprint;
import cyv.app.game.components.player.common.UnitDropletTurret;
import cyv.app.render.game.RendererRegistry;
import cyv.app.render.game.renders.UnitRenderer;

public class BlueprintDropletTurret extends AbstractBlueprint<UnitDropletTurret> {
    public BlueprintDropletTurret(BubbleGame game) {
        super(game);
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
    public boolean readyOnStart() {
        return true;
    }

    @Override
    public Texture getTexture() {
        return getGame().getAssets().getTexture("blueprint_empty");
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
