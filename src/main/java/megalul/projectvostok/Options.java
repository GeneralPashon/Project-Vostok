package megalul.projectvostok;

import glit.Glit;
import glit.files.FileHandle;
import glit.io.glfw.Key;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class Options{

    public static int UNLIMITED_FPS_SETTING_THRESHOLD = 256;


    private final Main session;
    private final FileHandle optionsFile;

    private final Map<KeyMapping, Key> keyMappings;
    private int fov = 80;
    private int renderDistance = 32;
    private int maxFramerate = 0;
    private boolean fullscreen = false;
    private boolean showFps = false;
    private float mouseSensitivity = 0.5F;

    public Options(Main session, String gameDirPath){
        this.session = session;

        keyMappings = new HashMap<>();

        optionsFile = new FileHandle(gameDirPath + "options.txt", true);
        optionsFile.create();

        load();
        init();
    }


    private void init(){
        Glit.window().setFullscreen(fullscreen);
        setMaxFramerate(maxFramerate, UNLIMITED_FPS_SETTING_THRESHOLD);
    }

    private void load(){
        String[] lines = optionsFile.readString().split("\n");

        System.out.println(lines.length);

        for(String line: lines){
            String[] parts = line.split(" : ");
            if(parts.length != 2)
                continue;

            String value = parts[1];

            parts = parts[0].split("\\.");
            if(parts.length != 2)
                continue;

            String category = parts[0];
            String key = parts[1];


            switch(category){
                case "graphics" -> {
                    switch(key){
                        case "fov" -> fov = Integer.parseInt(value);
                        case "renderDistance" -> renderDistance = Integer.parseInt(value);
                        case "maxFramerate" -> maxFramerate = Integer.parseInt(value);
                        case "fullscreen" -> fullscreen = Boolean.parseBoolean(value);
                        case "showFps" -> showFps = Boolean.parseBoolean(value);
                    }
                }
                case "key" -> keyMappings.put(KeyMapping.valueOf(key.toUpperCase()), Key.valueOf(value.toUpperCase()));
                case "control" -> {
                    switch(key){
                        case "mouseSensitivity" -> mouseSensitivity = Float.parseFloat(value);
                    }
                }
            }
        }
    }

    public void save(){
        PrintStream out = optionsFile.writer();

        out.println("graphics.fov : " + fov);
        out.println("graphics.renderDistance : " + renderDistance);
        out.println("graphics.maxFramerate : " + maxFramerate);
        out.println("graphics.fullscreen : " + fullscreen);
        out.println("graphics.showFps : " + showFps);

        out.println("control.mouseSensitivity : " + mouseSensitivity);

        for(KeyMapping keyType: KeyMapping.values())
            out.println("key." + keyType.toString().toLowerCase() + " : " + keyMappings.getOrDefault(keyType, keyType.getDefaultKey()).toString().toLowerCase());

        out.close();
    }


    public Key getKey(KeyMapping keyType){
        return keyMappings.getOrDefault(keyType, keyType.getDefaultKey());
    }

    public void setKey(KeyMapping keyType, Key key){
        keyMappings.put(keyType, key);
    }


    public int getFov(){
        return fov;
    }

    public void setFov(int fov){
        this.fov = fov;
        session.getCamera().setFov(fov);
    }


    public int getRenderDistance(){
        return renderDistance;
    }

    public void setRenderDistance(int renderDistance){
        this.renderDistance = renderDistance;
    }


    public int getMaxFramerate(){
        return maxFramerate;
    }

    public void setMaxFramerate(int maxFramerate, int unlimitedThreshold){
        this.maxFramerate = maxFramerate;
        session.getFpsSync().setFps(maxFramerate);

        session.getFpsSync().enable(maxFramerate > 0 && maxFramerate < unlimitedThreshold);
        Glit.window().setVsync(maxFramerate == 0);
    }


    public boolean isFullscreen(){
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen){
        this.fullscreen = fullscreen;

        Glit.window().setFullscreen(fullscreen);
    }


    public boolean isShowFps(){
        return showFps;
    }

    public void setShowFps(boolean showFps){
        this.showFps = showFps;
    }


    public float getMouseSensitivity(){
        return mouseSensitivity;
    }

    public void setMouseSensitivity(float mouseSensitivity){
        this.mouseSensitivity = mouseSensitivity;
    }

}
