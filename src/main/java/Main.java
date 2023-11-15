import engine.Window;

public class Main {

    // Software Enter Point
    public static void main(String[] args) {
        //Singelton
        Window window = Window.get();
        window.run();
    }
}
