import engine.Window;

public class Main {

    public static void main(String[] args) {
        //Singelton
        Window window = Window.get();
        window.run();
    }
}
