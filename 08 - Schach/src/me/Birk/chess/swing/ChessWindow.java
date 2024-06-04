package me.Birk.chess.swing;

import me.Birk.chess.Main;
import me.Birk.chess.core.ChessGame;
import me.Birk.chess.core.PieceColor;
import me.Birk.chess.events.ChessEvent;
import me.Birk.chess.events.ChessEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ChessWindow extends JFrame  {

    final public JPanel controlPanel;
    final private ChessboardPanel chessboardPanel;


    public ChessWindow() {

        this.setTitle("Mein Schachspiel");
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        controlPanel = controlPanel();
        chessboardPanel = new ChessboardPanel();

        controlPanel.setLayout(new FlowLayout());

        this.add(chessboardPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    public JPanel controlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel chessboardLabel = new JLabel("Spielstand: " + String.valueOf(Main.chessGame.getValueOfGame()));
        final JButton jButtonWhite = new JButton("Make Move White");
        final JButton jButtonBlack = new JButton("Make Move Black");
        final JButton jButtonReset = new JButton("Reset Board");
        final JButton jButtonUndo = new JButton("Undo");

        jButtonWhite.setVisible(true);
        jButtonBlack.setVisible(false);
        jButtonReset.setVisible(true);
        jButtonUndo.setVisible(false);

        controlPanel.add(chessboardLabel);
        controlPanel.add(jButtonWhite);
        controlPanel.add(jButtonBlack);
        controlPanel.add(jButtonReset);
        controlPanel.add(jButtonUndo);

        jButtonReset.addActionListener(e -> {
            Main.chessGame.resetBoard();
            chessboardPanel.repaint();
            jButtonWhite.setVisible(true);
            jButtonBlack.setVisible(false);
            chessboardLabel.setText("Neues Spiel, Spielstand: " + String.valueOf(Main.chessGame.getValueOfGame()));
            jButtonUndo.setVisible(false);
        });
        jButtonUndo.addActionListener(e -> {
            Main.chessGame = Main.chessGameOld.copy();
            registerChessEventListeners(chessboardLabel, jButtonWhite, jButtonBlack,jButtonUndo);
            chessboardLabel.setText("Spielstand: "
                    + Main.chessGame.getValueOfGame());
            chessboardPanel.repaint();
            jButtonUndo.setVisible(false);
        });

        registerChessEventListeners(chessboardLabel, jButtonWhite, jButtonBlack,jButtonUndo);

        return controlPanel;
    }

    private void registerChessEventListeners(JLabel chessboardLabel, JButton jButtonWhite,
                                             JButton jButtonBlack, JButton jButtonUndo) {

        Main.chessGame.addEventListener(new ChessEventListener() {
            @Override
            public void onChessEvent(ChessEvent event) {
                SwingUtilities.invokeLater(() -> {
                    updateChessboardLabel(event,chessboardLabel);
                    updateButtonVisibility(jButtonWhite, jButtonBlack, jButtonUndo, event);
                });
            }
        });
    }

    private void resetGame() {
        //Main.chessGame = new ChessGame();
    }

    private void updateChessboardLabel(ChessEvent event, JLabel chessboardLabel) {
        if (event.getGame().getValueOfGame() > 1000){
            chessboardLabel.setText("Weiß hat gewonnen");
        } else if (event.getGame().getValueOfGame() < -1000){
            chessboardLabel.setText("Schwarz hat gewonnen");
        } else {
            chessboardLabel.setText("Spielstand: "
                    + event.getGame().getValueOfGame());
        }
    }


    private void updateButtonVisibility(JButton jButtonWhite, JButton jButtonBlack,JButton jButtonUndo, ChessEvent event) {
        jButtonUndo.setVisible(true);
        if (Math.abs(event.getGame().getValueOfGame()) > 1000){
            jButtonWhite.setVisible(false);
            jButtonBlack.setVisible(false);
        }
        if (event.getGame().getLastPieceColor() == PieceColor.WHITE) {
            jButtonWhite.setVisible(false);
            jButtonBlack.setVisible(true);
        } else {
            jButtonWhite.setVisible(true);
            jButtonBlack.setVisible(false);
        }

    }


}


