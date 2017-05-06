package infof302.pieces;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;


public class Cavalier extends Piece{

	/**
	 * @param model
	 */
	public Cavalier(Model model, int n) {
		super(model);
		
		coordx = model.intVar("Cavalier(x)", 1, n);
		coordy = model.intVar("Cavalier(y)", 1, n);
	}
	
	@Override
	public void checkDependency(Piece[] pieces){
		
		
		// contrainte 4: i.x != j.x et i.y != j.y'
		// pour toute pièce j tel que i != j
		int[] k = {-2,2};
		int[] l = {-2,2};
		
		for(Piece piece : pieces){
			if (this != piece){
				
				// contrainte 1: checker que les 2 pieces se trouvent dans des positions différentes
				checkEqual(piece);
				
				for (int i : k){
					for (int j : l){
						Constraint cstxa = model.arithm(
							model.intOffsetView(coordx, i),
							"!=",
							piece.coordx
						);
				
						Constraint cstya = model.arithm(
							model.intOffsetView(coordy, j),
							"!=",
							piece.coordy
						);
				
						// i != i'+k et j != j'+l
						model.and(
							cstxa,cstya
						).post();
				
						cstxa = model.arithm(
							model.intOffsetView(coordx, j),
							"!=",
							piece.coordx
						);
				
						cstya = model.arithm(
							model.intOffsetView(coordy, i),
							"!=",
							piece.coordy
						);
				
						// i != i'+l et j != j'+k
						model.and(
							cstxa,cstya
						).post();				
			
					}
				}
			}
		}
	}
	
	@Override
	public void checkIndependency(Piece[] piece, int caseX, int caseY){
		int[] k = {-2,2};
		int[] l = {-2,2};
		for(Piece pieces : piece){
			if (this != pieces){
				checkEqual(pieces);
				
				for (int i : k){
					for (int j : l){
						Constraint cstxa = model.arithm(
							model.intOffsetView(coordx, i), "=", caseX
						);
				
						Constraint cstya = model.arithm(
							model.intOffsetView(coordy, j),"=",caseY
						);
				
						// i != i'+k et j != j'+l
						model.and(
							cstxa,cstya
						).post();
				
						cstxa = model.arithm(
							model.intOffsetView(coordx, j),"=",caseX
						);
				
						cstya = model.arithm(
							model.intOffsetView(coordy, i),"=",caseY
						);
				
						// i != i'+l et j != j'+k
						model.or(
							cstxa,cstya
						).post();				
			
					}
				}
			}
		}
	}
}
