package net.javaci.mobile.bomberman.core.view.widget;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import net.peakgames.libgdx.stagebuilder.core.ICustomWidget;
import net.peakgames.libgdx.stagebuilder.core.assets.AssetsInterface;
import net.peakgames.libgdx.stagebuilder.core.assets.ResolutionHelper;
import net.peakgames.libgdx.stagebuilder.core.services.LocalizationService;

import java.util.Map;

public class GameListWidgetConfig extends WidgetGroup implements ICustomWidget {
    private Map<String, String> attributes;
    private ResolutionHelper resolutionHelper;

    @Override
    public void build(Map<String, String> attributes, AssetsInterface assetsInterface, ResolutionHelper resolutionHelper, LocalizationService localizationService) {
        this.attributes = attributes;
        this.resolutionHelper = resolutionHelper;
        setVisible(false);
        setTouchable(Touchable.disabled);
    }

    public String getString(String key) {
        return attributes.get(key);
    }

    public int getInt(String key) {
        return Integer.valueOf(attributes.get(key));
    }

    public float getSizeMultFloat(String key) {
        return Integer.valueOf(attributes.get(key)) * resolutionHelper.getSizeMultiplier();
    }


    public float getPositionMultFloat(String key) {
        return Integer.valueOf(attributes.get(key)) * resolutionHelper.getPositionMultiplier();
    }
}
