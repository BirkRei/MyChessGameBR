package me.Birk.chess.swing;

import me.Birk.chess.Main;
import me.Birk.chess.core.ChessPiece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class ChessboardPanel extends JPanel {
    private final int boardSize = 8, squareSize = 80;
    private int[] activeSquare = new int[]{-1,-1};
    private ChessPiece activeChessPiece;
    private int selectedPieceIndex = -1;

    public ChessboardPanel() {
        final MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                ChessPiece selectedPiece =
                        Main.chessGame.getSelectedChessPiece( x / squareSize,  y / squareSize);
                int index = Main.chessGame.getSelectedPieceIndex( x / squareSize,y / squareSize);
                if (selectedPieceIndex == -1) {
                    if (index != -1 &&
                            Main.chessGame.getPieces().get(index).getPieceColor() == Main.chessGame.getLastPieceColor()) {
                        return;
                    }
                    activeSquare[0] = x / squareSize;
                    activeSquare[1] = y / squareSize;
                    activeChessPiece = selectedPiece;
                    selectedPieceIndex = index;
                } else {
                    boolean movePiece = Main.chessGame.isMovePossibleAllRules(selectedPieceIndex, x / squareSize, y / squareSize);
                    if (movePiece) {
                        Main.chessGameOld = Main.chessGame.copy();
                        Main.chessGame.applyMoveInkludingEvents(selectedPieceIndex,  x / squareSize, y / squareSize);
                    }
                    activeSquare = new int[]{-1,-1};
                    selectedPieceIndex = -1;
                }
                repaint();
            }
        };
        addMouseListener(mouseAdapter);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        drawCheckerBoard(graphics);
        updateInteractive(graphics);
        drawAllPieces(graphics);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(boardSize*squareSize, boardSize*squareSize);
    }

    private void drawCheckerBoard(Graphics graphics) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (Main.isEven(col + row)) {
                    graphics.setColor(Color.WHITE);
                } else graphics.setColor(Color.DARK_GRAY);
                graphics.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
            }
        }
    }
    private void updateInteractive(Graphics graphics) {
        graphics.setColor(Color.GREEN);
        graphics.fillRect(activeSquare[0]*squareSize, activeSquare[1]*squareSize, squareSize, squareSize);
        if (isPieceSelected()){
            List<int[]> locations= Main.chessGame.possibleChessPieceLocations(selectedPieceIndex);
            for (int[] location : locations) {
                graphics.fillOval(location[0] * squareSize+squareSize/4, location[1] * squareSize + squareSize/4,
                        squareSize/2, squareSize/2);
            }
        }
    }
    private void drawAllPieces(Graphics graphics) {
        for (ChessPiece piece: Main.chessGame.getPieces()){
            File file = new File("C:\\Users\\DrBirkReichenbach\\Java Code\\08 - Schach\\recources\\" + piece.getImage());
            try {
                final BufferedImage image = ImageIO.read(file);
                graphics.drawImage(image, piece.getLocationX() * squareSize, piece.getLocationY() *
                        squareSize, squareSize, squareSize, this);
            }
            catch (Exception e) {
                System.out.println("mist");
                e.printStackTrace();
            }
        }
    }

    private boolean isPieceSelected() {
        return activeChessPiece != null;
    }

}
