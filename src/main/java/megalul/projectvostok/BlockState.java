package megalul.projectvostok;

public class BlockState{

    private final short state; // 8bits=data, 8bits=id
    public final int id;
    public final byte extraData;

    public BlockState(short state){
        this.state = state;

        id = state & 0xFF;
        extraData = (byte) (state >> 8);
    }

    public BlockState(int id, byte extraData){
        this.id = id;
        this.extraData = extraData;

        state = (short) ((id & 0xFF | (((short) extraData) << 8)));
    }

    public short getState(){
        return state;
    }

}
