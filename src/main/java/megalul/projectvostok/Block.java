package megalul.projectvostok;

public enum Block{

    AIR,
    DIRT,
    GRASS_BLOCK;


    public final short id; // order

    Block(){
        this.id = (short) ordinal();
    }

    public static Block fromID(int id){
        return Block.values()[id];
    }

}
