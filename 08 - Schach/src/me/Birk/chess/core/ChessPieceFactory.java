package me.Birk.chess.core;

public class ChessPieceFactory {
    private String image;
    final private PieceColor pieceColor;
    final private PieceType pieceType;
    private int locationX, locationY;

    public ChessPieceFactory(PieceColor pieceColor, PieceType pieceType) {
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        image = pieceType.toString().toLowerCase() + "-";

        switch (pieceColor) {
            case WHITE:
                this.image += "w";
                break;
            case BLACK:
                this.image += "b";
                break;
        }
        image += ".png";
    }
    public ChessPieceFactory withLocation(int x, int y) {
        this.locationX = x;
        this.locationY = y;
        return this;
    }
    public ChessPieceFactory withLocationX(int x) {
        this.locationX = x;
        return this;
    }
    public ChessPieceFactory withLocationY(int y) {
        this.locationY = y;
        return this;
    }

    public ChessPiece build(){
        return new ChessPiece(image, pieceColor, pieceType, locationX, locationY);
    }
}
