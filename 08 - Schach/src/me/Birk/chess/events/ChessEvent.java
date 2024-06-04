package me.Birk.chess.events;

import me.Birk.chess.core.ChessGame;

public class ChessEvent {
    private ChessGame game;

    public ChessEvent(ChessGame game) {  // Grün
        this.game = game;
    }

    public ChessGame getGame() {  // Grün
        return game;
    }
}
