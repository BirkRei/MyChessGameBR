package me.Birk.chess.core;

import me.Birk.chess.events.ChessEvent;          // Grün
import me.Birk.chess.events.ChessEventManager;   // Grün
import me.Birk.chess.events.ChessEventListener;  // Grün

import java.util.ArrayList;
import java.util.List;

public class ChessGame {
    private final ChessEventManager eventManager = new ChessEventManager();  // Grün
    final private List<ChessPiece> pieces = new ArrayList<>();
    public boolean captured = false;
    private PieceColor lastPieceColor = PieceColor.BLACK;
    private int valueOfGame;
    boolean rochadeWhiteAllowed = true, rochadeBlackAllowed = true;

    public ChessGame() {
        initializeBoard();
    }

    public void addEventListener(ChessEventListener listener) {  // Grün
        eventManager.addListener(listener);
    }

    public void removeEventListener(ChessEventListener listener) {  // Grün
        eventManager.removeListener(listener);
    }

    public ChessGame copy(){
        ChessGame copy = new ChessGame();
        copy.captured = captured;
        copy.lastPieceColor = lastPieceColor;
        copy.valueOfGame = valueOfGame;
        copy.pieces.clear();
        for (ChessPiece piece : pieces) {
            copy.pieces.add(piece.copy());
        }
        return copy;
    }

    public void resetBoard(){
        pieces.clear();
        initializeBoard();
        lastPieceColor = PieceColor.BLACK;
        captured = false;
    }

    private void initializeBoard() {
        for(PieceColor pieceColor : PieceColor.values()) {

            this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.ROOK)
                    .withLocation(0,pieceColor == PieceColor.WHITE ? 7:0).build()); //ternärer Operator genutzt um auf Farbe zu checken
            this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.KNIGHT)
                    .withLocation(1,pieceColor == PieceColor.WHITE ? 7:0).build());
            this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.BISHOP)
                    .withLocation(2,pieceColor == PieceColor.WHITE ? 7:0).build());
            this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.QUEEN)
                    .withLocation(3,pieceColor == PieceColor.WHITE ? 7:0).build());
            this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.KING)
                    .withLocation(4,pieceColor == PieceColor.WHITE ? 7:0).build());
            this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.BISHOP)
                    .withLocation(5,pieceColor == PieceColor.WHITE ? 7:0).build());
            this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.KNIGHT)
                    .withLocation(6,pieceColor == PieceColor.WHITE ? 7:0).build());
            this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.ROOK)
                    .withLocation(7,pieceColor == PieceColor.WHITE ? 7:0).build());
            for(int col = 0; col < 8; col++) {
                this.pieces.add(new ChessPieceFactory(pieceColor,PieceType.PAWN)
                        .withLocation(col, pieceColor == PieceColor.WHITE ? 6:1).build());
            }
        }
        valueOfGame = 0;
    }

    private ChessGame getBoardState() {
        ChessGame state = new ChessGame();
        state.pieces.clear();
        state.captured = this.captured;
        state.lastPieceColor = this.lastPieceColor;
        state.valueOfGame = this.valueOfGame;
        for (ChessPiece piece : this.pieces) {
            state.pieces.add(piece.copy());
        }
        return state;
    }
    private void setBoardState(ChessGame state) {
        this.pieces.clear();
        for (ChessPiece piece : state.pieces) {
            this.pieces.add(piece.copy());
        }
        this.captured = state.captured;
        this.lastPieceColor = state.lastPieceColor;
        this.valueOfGame = state.valueOfGame;
    }
    public List<ChessPiece> getPieces() {
        return pieces;
    }
    public PieceColor getLastPieceColor() {
        return lastPieceColor;
    }

    public ChessPiece getSelectedChessPiece(int x, int y) {
        for(ChessPiece chessPiece : pieces) {
            if (chessPiece.getLocationX()==x && chessPiece.getLocationY()==y) {
                return chessPiece;
            }
        }
        return null;
    }
    public int getSelectedPieceIndex(int x, int y) {
        for(int i = 0; i<pieces.size();i++) {
            if (pieces.get(i).getLocationX()==x && pieces.get(i).getLocationY()==y) {
                return i;
            }
        }
        return -1;
    }
    public List<int[]> possibleChessPieceLocations(int selectedPieceIndex) {
        List<int[]> locations= new ArrayList<>();
        if (selectedPieceIndex==-1) {return locations;}
        int x = pieces.get(selectedPieceIndex).getLocationX();
        int y = pieces.get(selectedPieceIndex).getLocationY();
        int movX, movY;
        PieceColor color = pieces.get(selectedPieceIndex).getPieceColor();
        for(int col = 0; col < 8; col++) {
            for(int row = 0; row < 8; row++) {
                movX = col - x;
                movY = row - y;
                if(isMoveLegalOnThisBoard(selectedPieceIndex,col,row)
                        && pieces.get(selectedPieceIndex).getPieceType().isMovementAllowedByChessPieceRule(color,movX,movY)){
                    locations.add(new int[]{col,row});
                }
            }
        }
        return locations;
    }

    public void applyMoveInkludingEvents(int pieceIndex, int targetLocationX, int targetLocationY){
        applyMove(pieceIndex,targetLocationX,targetLocationY);
        changeRochadeBooleans(pieceIndex);
        eventManager.fireEvent(new ChessEvent(this));
    }
    public void applyMove(int pieceIndex, int targetLocationX, int targetLocationY){
        final ChessPiece activePiece = pieces.get(pieceIndex);
        activePiece.setLocation(targetLocationX, targetLocationY);
        lastPieceColor = activePiece.getPieceColor();
        captured = removePossiblePieceAfterItHasBeenCapturedByMovingPiece(pieceIndex, targetLocationX, targetLocationY);
    }
    public boolean isMovePossibleAllRules(int pieceIndex, int targetLocationX, int targetLocationY) {
        final ChessPiece activePiece = pieces.get(pieceIndex);
        final PieceColor activePieceColor = activePiece.getPieceColor();
        final int movementX = targetLocationX - activePiece.getLocationX();
        final int movementY = targetLocationY - activePiece.getLocationY();
        if (activePiece.getPieceType().isMovementAllowedByChessPieceRule(activePieceColor,movementX,movementY)
                && isMoveLegalOnThisBoard(pieceIndex, targetLocationX, targetLocationY)
                && !isMoveMatingOwnKing(pieceIndex, targetLocationX, targetLocationY)){
            return true;
        }
        return false;
    }
    public boolean isMoveLegalOnThisBoard(int pieceIndex, int targetLocationX, int targetLocationY) {
        final ChessPiece activePiece = pieces.get(pieceIndex);
        final int movementX = targetLocationX - activePiece.getLocationX();
        final int movementY = targetLocationY - activePiece.getLocationY();
        boolean isLegal = true;
        if (activePiece.getPieceType() == PieceType.PAWN) {
            if (Math.abs(movementY) == 2){ //initialSchritt Schlagen nicht erlaubt)
                isLegal = false;
                for(ChessPiece piece : pieces) {
                    if (piece.getLocationX() == targetLocationX && piece.getLocationY() == targetLocationY) {
                        return false;
                    }
                }
                if (activePiece.getPieceColor() == PieceColor.WHITE && activePiece.getLocationY() == 6) {
                    isLegal = true;
                } else if (activePiece.getPieceColor() == PieceColor.BLACK && activePiece.getLocationY() == 1) {
                    isLegal = true;
                }
            } else if (Math.abs(movementY) == 1 && Math.abs(movementX) == 1) {//Schlagen Seitschritt
                isLegal = false;
                for(ChessPiece piece : pieces) {
                    if (piece.getPieceColor() != activePiece.getPieceColor() &&
                            piece.getLocationX() == targetLocationX && piece.getLocationY() == targetLocationY) {
                        isLegal = true;
                        break;
                    }
                }
                if (!isLegal) {
                    return false;
                }
            }else if (Math.abs(movementY) == 1){//Bewegen ohne schlagen
                for(ChessPiece piece : pieces) {
                    if (piece.getLocationX() == targetLocationX && piece.getLocationY() == targetLocationY) {
                        return false;
                    }
                }
            }
        }
        //Kollision mit eigenen Figuren:
        //am Ziel:
        for(ChessPiece piece : pieces) {
            if (piece.getPieceColor() == activePiece.getPieceColor()
                    && piece.getLocationX() == targetLocationX && piece.getLocationY() == targetLocationY) {
                return false;
            }
        }
        //auf dem Weg;
        if(collisionInStepsInBetween(pieceIndex, targetLocationX, targetLocationY)){
            return false;
        }

        return isLegal;
    }

    private boolean isMoveMatingOwnKing(int pieceIndex, int targetLocationX, int targetLocationY) {
        ChessGame previousState = this.getBoardState();
        applyMove(pieceIndex,targetLocationX,targetLocationY);
        boolean canKingBeCaptured = canKingOfColorBeCapturedByAnyPieceOnBoard(pieces.get(pieceIndex).getPieceColor());
        this.setBoardState(previousState);
        return canKingBeCaptured;
    }
    public List<int[]> stepsInBetween(int pieceIndex, int targetLocationX, int targetLocationY){
        List<int[]> locations = new ArrayList<>();
        final ChessPiece activePiece = pieces.get(pieceIndex);
        final int movementX = targetLocationX - activePiece.getLocationX();
        final int movementY = targetLocationY - activePiece.getLocationY();
        if (!activePiece.getPieceType().getCanJump()) {
            if(Math.abs(movementX) > 0 && Math.abs(movementY) > 0){
                for (int step = 1; step < Math.abs(movementX); step++) { //brauche nicht auf meinem Fleck zu schauen und nicht am Ziel
                    locations.add(new int[]{activePiece.getLocationX() +  movementX / Math.abs(movementX) * step,
                            activePiece.getLocationY() + movementY / Math.abs(movementY) * step});
                }
            }else if(movementX == 0) {
                for (int step = 1; step < Math.abs(movementY); step++) {
                    locations.add(new int[]{activePiece.getLocationX(),
                            activePiece.getLocationY() + movementY / Math.abs(movementY) * step});
                }
            }else if(movementY == 0) {
                for (int step = 1; step < Math.abs(movementX); step++) {
                    locations.add(new int[]{activePiece.getLocationX() + movementX / Math.abs(movementX) * step,
                            activePiece.getLocationY()});
                }
            }
        }
        return locations;
    }
    public boolean collisionInStepsInBetween(int pieceIndex, int targetLocationX, int targetLocationY){
        List<int[]> inBetweenPositions = stepsInBetween(pieceIndex, targetLocationX, targetLocationY);
        for(int[] inBetweenPosition : inBetweenPositions) {
            for(ChessPiece piece : pieces) {
                if (piece.getLocationX() == inBetweenPosition[0] && piece.getLocationY() == inBetweenPosition[1]) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean removePossiblePieceAfterItHasBeenCapturedByMovingPiece(int pieceIndex, int targetLocationX, int targetLocationY) {
        final ChessPiece activePiece = pieces.get(pieceIndex);
        for(ChessPiece piece : pieces) {
            if (piece.getPieceColor() != activePiece.getPieceColor()
                    && piece.getLocationX() == targetLocationX && piece.getLocationY() == targetLocationY) {
                if(piece.getPieceColor() == PieceColor.WHITE){
                    valueOfGame = valueOfGame - piece.getPieceType().getMaterialValue();
                } else {
                    valueOfGame = valueOfGame + piece.getPieceType().getMaterialValue();
                }

                pieces.remove(piece);
                return true;
            }
        }
        return false;
    }
    public boolean canThisMoveCaptureKing(int pieceIndex, int targetLocationX, int targetLocationY) {
        ChessPiece activePiece = pieces.get(pieceIndex);
        PieceColor activePiecePieceColor = activePiece.getPieceColor();
        ChessPiece kingOfOpponent = null;
        if (activePiecePieceColor == PieceColor.WHITE) {
            kingOfOpponent = pieces.get(indexOfKing(PieceColor.BLACK));
        } else kingOfOpponent = pieces.get(indexOfKing(PieceColor.WHITE));
        return canThisMoveCaptureKing(pieceIndex,targetLocationX,targetLocationY,kingOfOpponent);
    }
    public boolean canThisMoveCaptureKing(int pieceIndex, int targetLocationX, int targetLocationY, ChessPiece kingOfOpponent) {
        boolean canThisMoveMateBoolean = false;
        if (targetLocationX == kingOfOpponent.getLocationX() && targetLocationY == kingOfOpponent.getLocationY()) {
            canThisMoveMateBoolean = true;
        }
        return canThisMoveMateBoolean;
    }
    public boolean canKingOfColorBeCapturedByAnyPieceOnBoard(PieceColor pieceColor){
        return !whichPeacesCanCaptureKingOfColor(pieceColor).isEmpty();
    }
    public List<ChessPiece> whichPeacesCanCaptureKingOfColor(PieceColor kingColor){
        List<ChessPiece> peaces = new ArrayList<>();
        ChessPiece kingOfOpponent = pieces.get(indexOfKing(kingColor));
        int pieceIndex = -1;
        int targetLocationX = kingOfOpponent.getLocationX();
        int targetLocationY = kingOfOpponent.getLocationY();
        for (ChessPiece piece : pieces) {
            pieceIndex += 1;
            if (piece.getPieceColor() != kingColor
                    && piece.getPieceType().isMovementAllowedByChessPieceRule(piece.getPieceColor()
                        ,targetLocationX - piece.getLocationX(),targetLocationY - piece.getLocationY())
                    && isMoveLegalOnThisBoard(pieceIndex, targetLocationX, targetLocationY)){
                peaces.add(piece);
            }
        }
        return peaces;
    }
    public int indexOfKing(PieceColor pieceColor){
        int indexOfKing = -1;
        for(ChessPiece piece : pieces) {
            indexOfKing +=1;
            if (piece.getPieceType() == PieceType.KING && piece.getPieceColor() == pieceColor) {
                break;
            }
        }
        return indexOfKing;
    }
    public ChessPiece getPieceOnLocation(int locationX, int locationY){
        for (ChessPiece piece : pieces) {
            if (piece.getLocationX() == locationX && piece.getLocationY() == locationY) {
                return piece;
            }
        }
        return null;
    }
    public boolean isPieceOnLocation(int locationX, int locationY){
        return getPieceOnLocation(locationX, locationY) != null;
    }
    private void changeRochadeBooleans(int pieceIndex) {
        if (pieces.get(pieceIndex).getPieceType() == PieceType.KING && pieces.get(pieceIndex).getPieceColor() == PieceColor.BLACK) {
            rochadeBlackAllowed = false;
        } else if (pieces.get(pieceIndex).getPieceType() == PieceType.KING && pieces.get(pieceIndex).getPieceColor() == PieceColor.WHITE) {
            rochadeWhiteAllowed = false;
        }
        if (pieces.get(pieceIndex).getPieceType() == PieceType.ROOK && pieces.get(pieceIndex).getPieceColor() == PieceColor.BLACK) {
            if (pieces.get(pieceIndex).getLocationX() == 0 && pieces.get(pieceIndex).getLocationY() == 0) {
                rochadeBlackAllowed = false;
            } else if (pieces.get(pieceIndex).getLocationX() == 7 && pieces.get(pieceIndex).getLocationY() == 0) {
                rochadeBlackAllowed = false;
            }
        } else if (pieces.get(pieceIndex).getPieceType() == PieceType.ROOK && pieces.get(pieceIndex).getPieceColor() == PieceColor.WHITE) {
            if (pieces.get(pieceIndex).getLocationX() == 0 && pieces.get(pieceIndex).getLocationY() == 7) {
                rochadeWhiteAllowed = false;
            } else if (pieces.get(pieceIndex).getLocationX() == 7 && pieces.get(pieceIndex).getLocationY() == 7) {
                rochadeWhiteAllowed = false;
            }
        }
    }
    public int getValueOfGame() {
        return valueOfGame;
    }
}
