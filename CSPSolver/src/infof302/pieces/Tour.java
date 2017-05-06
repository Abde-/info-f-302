package infof302.pieces;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

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
	public void checkIndependency(Piece[] pieces){
		
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
	public Constraint inDomain(int caseX, int caseY){
		
		// renvoit le domaine tel que la case (X,Y) se trouve dans le domaine de la tour.
		return model.or(
				model.arithm(coordx, "=", caseX),
				model.arithm(coordy, "=", caseY)
				);
	}

}
