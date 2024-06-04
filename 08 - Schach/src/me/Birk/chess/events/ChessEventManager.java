package me.Birk.chess.events;

import java.util.ArrayList;
import java.util.List;

public class ChessEventManager {
    private List<ChessEventListener> listeners = new ArrayList<>();

    public void addListener(ChessEventListener listener) {  // Grün
        listeners.add(listener);
    }

    public void removeListener(ChessEventListener listener) {  // Grün
        listeners.remove(listener);
    }

    public void fireEvent(ChessEvent event) {  // Grün
        for (ChessEventListener listener : listeners) {
            listener.onChessEvent(event);
        }
    }
}
