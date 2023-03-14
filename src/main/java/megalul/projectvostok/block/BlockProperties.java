package megalul.projectvostok.block;

import java.util.HashMap;

public abstract class BlockProperties{

    private static final HashMap<Integer, BlockProperties> propertiesList = new HashMap<>();


    private final int id;

    protected BlockProperties(int id){
        this.id = id;

        propertiesList.put(id, this);
    }

    public int getID(){
        return id;
    }

    public abstract boolean isSolid();


    public static BlockProperties getProperties(int id){
        return propertiesList.getOrDefault(id, propertiesList.get(0));
    }

    public static void applyProperties(BlockProperties properties){
        propertiesList.put(properties.getID(), properties);
    }

}
