package cyv.app.game.blueprints.contents;

import com.badlogic.gdx.graphics.Texture;
import cyv.app.Skydouser;
import cyv.app.game.blueprints.AbstractBlueprint;
import cyv.app.game.components.player.common.UnitWaterShield;

public class BlueprintWaterShield extends AbstractBlueprint<UnitWaterShield> {
    public BlueprintWaterShield(Skydouser game) {
        super(game, "Water Shield");
    }

    @Override
    public int getCost() {
        return 5;
    }

    @Override
    public int getCooldown() {
        return 20 * 30;
    }

    @Override
    public Texture getTexture() {
        return getGame().getAssets().getTexture("blueprint_water_shield");
    }

    @Override
    public UnitWaterShield produce(float x, float y) {
        return new UnitWaterShield(x, y);
    }

    @Override
    public String getHologramRendererName() {
        return "unit_water_shield";
    }
}
