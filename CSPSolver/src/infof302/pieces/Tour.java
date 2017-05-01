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
	public void checkDependency(Piece piece){
		super.checkEqual(piece);
		
		// contrainte 2: i.x != j.x et i.y != j.y'
		// pour toute pièce j tel que i != j
		if(this != piece){
			model.and(
				model.arithm(coordx, "!=", piece.coordx),
				model.arithm(coordy, "!=", piece.coordy)
				).post();
		}
	}
	
	@Override
	public void checkIndependency(Piece piece){
		
	}
}
