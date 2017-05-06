package infof302.pieces;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

/**
 * 
 * Méthode qui représente une piece générique (BONUS).
 *
 */
public class PieceGenerale extends Piece{

	public PieceGenerale(Model model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void checkIndependency(Piece[] pieces) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Constraint inDomain(int caseX, int caseY) {
		// TODO Auto-generated method stub
		return null;
	}

}
