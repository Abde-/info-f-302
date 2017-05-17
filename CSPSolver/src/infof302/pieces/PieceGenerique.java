package infof302.pieces;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import infof302.CSPSolver;

/**
 * 
 * M�thode qui repr�sente une piece g�n�rique (BONUS).
 *
 */
public class PieceGenerique extends Piece{
	
	/**
	 * Les domaines que la piece peut dominer
	 */
	private PieceDomaine[] domaines;
	
	public PieceGenerique(Model model, PieceDomaine[] domaines, int n) {
		super(model);
		
		coordx = model.intVar("Generique(x)", 1, n);
		coordy = model.intVar("Generique(y)", 1, n);
		
		this.domaines = domaines;
	}

	@Override
	public void checkIndependency(Piece[] pieces) {
		
		for (Piece piece: pieces){
			
			if(this != piece){
				
				// contrainte 1: checker que les 2 pieces se trouvent dans des positions diff�rentes
				checkEqual(piece);
				Constraint[] contraintes = new Constraint[]{};
			
				for (PieceDomaine domaine: domaines){
					Constraint x = domaine.apply(this, piece, model);
					contraintes = CSPSolver.addElement(contraintes,x);
				}
				// piece se trouve dans aucun domaine admissible (contrainte).
				model.and(contraintes).post();
			}
		}
	}

	@Override
	public Constraint inDomain(int caseX, int caseY) {
		
		Constraint[] contraintes = new Constraint[]{};
		for (PieceDomaine domaine: domaines){
			Constraint x = domaine.inDomain(this, caseX,caseY, model);
			contraintes = CSPSolver.addElement(contraintes,x);
		}
		
		return model.or(contraintes);
	}

}
