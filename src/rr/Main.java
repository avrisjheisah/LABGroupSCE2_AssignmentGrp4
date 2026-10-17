package rr;

import rr.control.GameController;
import rr.domain.ArrivalSchedule;
import rr.domain.GameConfig;
import rr.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        GameController game = new GameController(GameConfig.base(), ArrivalSchedule.base());
        new ConsoleUI(game).run();
    }
}
