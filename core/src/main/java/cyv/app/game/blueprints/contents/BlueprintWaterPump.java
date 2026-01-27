package cyv.app.game.blueprints.contents;

import com.badlogic.gdx.graphics.Texture;
import cyv.app.BubbleGame;
import cyv.app.game.blueprints.AbstractBlueprint;
import cyv.app.game.components.player.common.UnitDropletTurret;
import cyv.app.game.components.player.common.UnitWaterPump;

public class BlueprintWaterPump extends AbstractBlueprint<UnitWaterPump> {
    public BlueprintWaterPump(BubbleGame game) {
        super(game);
    }

    @Override
    public int getCost() {
        return 5;
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
    public UnitWaterPump produce(float x, float y) {
        return new UnitWaterPump(x, y);
    }

    @Override
    public String getHologramRendererName() {
        return "unit_water_pump";
    }
}
