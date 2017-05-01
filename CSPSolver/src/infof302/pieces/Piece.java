package infof302.pieces;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

/**
 * Pièce qui représente une pièce générique.
 * 
 * TODO: Cette classe s'occupe de representer une piece générique (cfr question bonus).
 * 
 * @author Abde-
 *
 */
public class Piece {
	
	Model model;
	IntVar coordx;
	IntVar coordy;
	
	/**
	 * @param model Modèle du problème à résoudre
	 */
	public Piece(Model model){
		this.model = model;
	}
	
	/**
	 * Méthode qui met à jour le model `model` avec les contraintes de la piece en question
	 * tel que ça respecte la dépendence.
	 * 
	 * @param piece piece avec laquelle checker la dependence
	 */
	public void checkDependency(Piece piece){
	}
	
	
	/**
	 * Méthode qui met à jour le model `model` avec les contraintes de la piece en question
	 * tel que ça respecte l'indépendence.
	 * 
	 * @param piece piece avec laquelle checker la dependence
	 */	
	public void checkIndependency(Piece piece){}
	
	public void checkEqual(Piece piece){	
		// contrainte 1: piece x différente piece y tq x != y
		
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
