package components;

import engine.Component;

public class SpriteRender extends Component {

    private boolean firstTime = false;

    @Override
    public void start() {
        System.out.println("I am staritng");
    }

    @Override
    public void update(float dt) {
        if (!firstTime){
            System.out.println("I am updating");
            firstTime = true;
        }

    }
}
