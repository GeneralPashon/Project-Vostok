package megalul.projectvostok;

import glit.Glit;
import glit.context.ContextListener;

public class Main implements ContextListener{

    public static void main(String[] args){
        Glit.create("Project Vostok", 1280, 720);
        Glit.init(new Main());
    }


    public void init(){

    }

    public void render(){

    }

    public void resize(int i, int i1){

    }

    public void dispose(){

    }

}
