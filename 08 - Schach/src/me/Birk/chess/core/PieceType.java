package me.Birk.chess.core;

import me.Birk.chess.util.TriPredicate;

public enum PieceType {
    PAWN((color, x, y) -> pawnMovement(color,x,y), false, 1),
    KNIGHT((color, x, y) -> knightMovement(x,y), true,3),
    BISHOP((color, x, y) -> bishopMovement(x,y), false,3),
    ROOK((color, x, y) -> rookMovement(x,y), false,5),
    QUEEN((color, x, y) -> queenMovement(x, y),false,10),
    KING((color, x, y) -> kingMovement(x,y),false, 100000);

    private final TriPredicate<PieceColor, Integer, Integer> isMovementAllowed;
    private final boolean canJump;
    private final int materialValue;

    PieceType(TriPredicate<PieceColor, Integer, Integer> isMovementAllowed, boolean canJump, int valueOfPiece) {
        this.isMovementAllowed = isMovementAllowed;
        this.canJump = canJump;
        this.materialValue = valueOfPiece;
    }

    private static boolean pawnMovement(PieceColor color, Integer x, Integer y) {
        if (color == PieceColor.WHITE &&
                (((y == -2 || y == -1 ) && x==0) ||
                (y==-1 && Math.abs(x) == 1))) {
            return true;
        }
        if (color == PieceColor.BLACK &&
                (((y == 2 || y == 1 ) && x==0) ||
                (y==1 && Math.abs(x) == 1))) {
            return true;
        }
        return false;
    }
    private static boolean knightMovement(Integer x, Integer y) {
        return (Math.abs(x) == 1 && Math.abs(y) == 2) || (Math.abs(x) == 2 && Math.abs(y) == 1);
    }
    private static boolean rookMovement(Integer x, Integer y) {
        return x == 0 || y == 0;
    }
    private static boolean bishopMovement(Integer x, Integer y) {
        return Math.abs(x) == Math.abs(y);
    }
    private static boolean queenMovement(Integer x, Integer y) {
        return x == 0 || y == 0 || Math.abs(x) == Math.abs(y);
    }
    private static boolean kingMovement(Integer x, Integer y) {
        return Math.abs(x) + Math.abs(y) == 1 || (Math.abs(x) == 1 && Math.abs(y) == 1);
    }



    public boolean isMovementAllowedByChessPieceRule(PieceColor color, int x, int y) {
        return isMovementAllowed.test(color, x, y);
    }
    public boolean getCanJump() {
        return canJump;
    }

    public int getMaterialValue() {
        return materialValue;
    }
}


