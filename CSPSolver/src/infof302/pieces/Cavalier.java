package infof302.pieces;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import infof302.CSPSolver;


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
	public void checkIndependency(Piece[] pieces){
		
		
		// contrainte 4: i.x != j.x et i.y != j.y'
		// pour toute pièce j tel que i != j
		int[] k = {-2,2};
		int[] l = {-1,1};
		
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
				
						// i != i'+k ou j != j'+l
						model.or(
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
				
						// i != i'+l ou j != j'+k
						model.or(
							cstxa,cstya
						).post();				
			
					}
				}
			}
		}
	}

	@Override
	public Constraint inDomain(int caseX, int caseY) {
		Constraint[] contraintes = new Constraint[]{};
		
		// contrainte 4: i.x != j.x et i.y != j.y'
		// pour toute pièce j tel que i != j
		int[] k = {-2,2};
		int[] l = {-1,1};
				
		// contrainte 1: checker que les 2 pieces se trouvent dans des positions différentes
				
		for (int i : k){
			for (int j : l){
				Constraint cstxa = 
					model.arithm(coordx,
					"=",
					caseX+i
				);
			
				Constraint cstya = model.arithm(
					coordy,
					"=",
					caseY+j
				);
				
				// i = i'+k et j = j'+l
				contraintes =
						CSPSolver.addElement(
								contraintes,
								model.and(
										cstxa,cstya
										)
								);
				
				cstxa = 
					model.arithm(coordx,
						"=",
						caseX+j
					);
				
				cstya = 
					model.arithm(
						coordy,
						"=",
						caseY+i
					);
				
				// i = i'+l et j = j'+k
				contraintes =
						CSPSolver.addElement(
								contraintes,
								model.and(
										cstxa,cstya
										)
								);
				
			}
		}
		
		// renvoit la contrainte tel que la case (X,Y) se trouve dans le domaine du cavalier
		return model.or(contraintes);	
	}
	
}
