package infof302.pieces;

import org.chocosolver.solver.Model;

public class Tour extends Piece{

	/**
	 * @param model
	 */
	public Tour(Model model, int n) {
		super(model);
		
		coordx = model.intVar("Tour(x)", 1, n);
		coordy = model.intVar("Tour(y)", 1, n);

	}

	@Override
	public void checkDependency(Piece[] pieces){
		
		// contrainte 2: i.x != j.x et i.y != j.y'
		// pour toute pièce j tel que i != j
		for(Piece piece : pieces){
			
			// contrainte 1: checker que les 2 pieces se trouvent dans des positions différentes
			checkEqual(piece);
			
			if(this != piece){
				model.and(
					model.arithm(coordx, "!=", piece.coordx),
					model.arithm(coordy, "!=", piece.coordy)
				).post();
			}
		}
	}
	
	@Override
	public void checkIndependency(Piece[] pieces, int caseX, int caseY){

		for(Piece piece : pieces){
			checkEqual(piece);
			if(this != piece){
				model.or(
					model.arithm(coordx, "=", caseX),
					model.arithm(coordy, "=", caseY)
				).post();
			}
		}
	}
}
