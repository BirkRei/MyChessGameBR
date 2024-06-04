package me.Birk.chess.core;

public class ChessPiece {
    private final String image;
    private final PieceColor pieceColor;
    private final PieceType pieceType;
    private int locationX, locationY;

    public ChessPiece(String image, PieceColor pieceColor, PieceType pieceType, int locationX, int locationY) {
        this.image = image;
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.locationX = locationX;
        this.locationY = locationY;

    }
    public String getImage(){
        return image;
    };

    public PieceColor getPieceColor(){
        return pieceColor;
    }

    public PieceType getPieceType(){
        return pieceType;
    }

    public int getLocationX(){
        return locationX;
    }
    public int getLocationY(){
        return locationY;
    }
    public void setLocation(int locationX, int locationY) {
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public ChessPiece copy() {
        return new ChessPiece(image, pieceColor, pieceType, locationX, locationY);
    }
}
