package infof302.pieces;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import infof302.CSPSolver;

public class Fou extends Piece{

	private int domain;
	
	/**
	 * @param model
	 */
	public Fou(Model model, int n) {
		super(model);
		
		this.domain = n;
		
		coordx = model.intVar("Fou(x)", 1, n);
		coordy = model.intVar("Fou(y)", 1, n);

	}
	
	@Override
	public void checkIndependency(Piece[] pieces){
		
		// contrainte 3: pour toute piece i fou implique que les autres pieces j
		// tq j != i -> i.x != j.x + k ou i.y != j.x +k
		for(Piece piece : pieces){
			
			if(this != piece){
				
				// contrainte 1: checker que les 2 pieces se trouvent dans des positions différentes
				checkEqual(piece);
			
				for(int i = -this.domain; i < this.domain; ++i){
					if(i != 0){
						model.or(
							model.arithm(coordx, "!=", piece.coordx, "+", i),
							model.arithm(coordy, "!=", piece.coordy, "+", i)
						).post();
			
						model.or(
							model.arithm(coordx, "!=", piece.coordx, "+", i),
							model.arithm(coordy, "!=", piece.coordy, "-", i)
						).post();
					}
				}
			}
		}
	}

	@Override
	public Constraint inDomain(int caseX, int caseY) {
		Constraint[] contraintes = new Constraint[]{};
		
		for(int i = -this.domain; i < this.domain; ++i){
			if(i != 0){
				contraintes = CSPSolver.addElement(contraintes,
						model.and(
								model.arithm(coordx, "=", caseX+i),
								model.arithm(coordy, "=", caseY+i)
								)
						);
				
				contraintes = CSPSolver.addElement(contraintes,
						model.and(
							model.arithm(coordx, "=", caseX+i),
							model.arithm(coordy, "=", caseY-i)
						)
				);
				
			}
		}
		
		// renvoit la contrainte tel que la case (X,Y) se trouve dans le domaine du cavalier
		return model.or(contraintes);	
	}

}
