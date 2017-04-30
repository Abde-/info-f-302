package infof302.pieces;


public class Tour extends Piece{

	@Override
	public void checkDependency(Piece piece){
		
		// contrainte 1: i.x != j.x et i.y != j.y'
		// pour toute pièce j tel que i != j
		
		coordx.ne(piece.coordx);
		coordy.ne(piece.coordy);
	}
	
	@Override
	public void checkIndependency(Piece piece){
		
	}
}
