package cyv.app.game.blueprints.contents;

import com.badlogic.gdx.graphics.Texture;
import cyv.app.BubbleGame;
import cyv.app.game.blueprints.AbstractBlueprint;
import cyv.app.game.components.player.common.UnitRippleTurret;

public class BlueprintRippleTurret extends AbstractBlueprint<UnitRippleTurret> {
    public BlueprintRippleTurret(BubbleGame game) {
        super(game);
    }

    @Override
    public int getCost() {
        return 15;
    }

    @Override
    public int getCooldown() {
        return 20 * 5;
    }

    @Override
    public Texture getTexture() {
        return getGame().getAssets().getTexture("blueprint_empty");
    }

    @Override
    public UnitRippleTurret produce(float x, float y) {
        return new UnitRippleTurret(x, y);
    }

    @Override
    public String getHologramRendererName() {
        return "unit_ripple_turret";
    }
}
