package megalul.projectvostok.block;

public enum BlockType{

    AIR,
    DIRT,
    GRASS_BLOCK;


    public final short id; // order

    BlockType(){
        this.id = (short) ordinal();
    }

    public static BlockType fromID(int id){
        return BlockType.values()[id];
    }

}
