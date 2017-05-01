package infof302.pieces;

import java.util.ArrayList;
import java.util.List;

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
	public void checkDependency(Piece piece){
		super.checkEqual(piece);
		
		// contrainte 4: i.x != j.x et i.y != j.y'
		// pour toute pièce j tel que i != j
		
		
		if (this != piece){
			int[] k = {-2,2};
			int[] l = {-2,2};
		
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
	
	@Override
	public void checkIndependency(Piece piece){
		
	}
}
