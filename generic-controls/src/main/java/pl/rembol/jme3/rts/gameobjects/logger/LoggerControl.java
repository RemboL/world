package pl.rembol.jme3.rts.gameobjects.logger;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoggerControl extends AbstractControl {

    private List<LogLine> logLines = new ArrayList<>();

    public void addLine(String string) {
        logLines.add(new LogLine(string));
    }

    public List<String> getLog() {
        return logLines.stream()
                .sorted(LogLine::compare)
                .map(LogLine::getMessage)
                .collect(Collectors.toList());
    }

    @Override
    protected void controlUpdate(float tpf) {

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
