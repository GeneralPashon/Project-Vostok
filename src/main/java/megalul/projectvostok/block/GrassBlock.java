package megalul.projectvostok.block;

public class GrassBlock extends BlockProperties{

    protected GrassBlock(int id){
        super(id);
    }

    @Override
    public boolean isSolid(){
        return true;
    }

}