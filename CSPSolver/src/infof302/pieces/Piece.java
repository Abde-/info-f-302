package infof302.pieces;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

/**
 * 
 * @author Abde-
 *
 */
public abstract class Piece {
	
	Model model;
	IntVar coordx;
	IntVar coordy;
	
	/**
	 * @param model ModËle du problËme ‡ rÈsoudre
	 */
	public Piece(Model model){
		this.model = model;
	}
	
	/**
	 * MÈthode qui met ‡ jour le model `model` avec les contraintes de la piece en question
	 * tel que Áa respecte l'indÈpendence.
	 * 
	 * @param piece piece avec laquelle checker l'ind√©pendence
	 */
	public abstract void checkIndependency(Piece[] pieces);
	
	/**
	 * M√©thode qui renvoit une Constraint qui v√©rifie si la case (caseX, caseY) se trouve
	 * dans le domaine de la pi√®ce.
	 * 
	 * @param caseX
	 * @param caseY 
	 */	
	public abstract Constraint inDomain(int caseX, int caseY);

	/**
	 * M√©thode qui renvoit une Constraint qui v√©rifie si la pi√®ce se trouve dans la case (caseX, caseY).
	 * 
	 * @param caseX
	 * @param caseY
	 */
	public Constraint checkPieceExists(int caseX, int caseY){
		
		// renvoit si coordx=caseX and coordy=caseY
		return model.and(
				model.arithm(coordx, "=", caseX),
				model.arithm(coordy, "=", caseY)
				);
	}
	
	/**
	 * Rajoute contrainte dans le 
	 * 
	 * @param piece
	 */
	public void checkEqual(Piece piece){	
		// contrainte 1: piece x diff√©rente piece y tq x != y
		
		if(this != piece)
			model.or(
				model.arithm(coordx, "!=", piece.coordx),
				model.arithm(coordy, "!=", piece.coordy)
				)
			.post();
		
	}
		
	public IntVar getCoordX(){
		return coordx;
	}
	public IntVar getCoordY(){
		return coordy;
	}
}