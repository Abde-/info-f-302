package infof302.pieces;

import org.chocosolver.solver.Model;

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
	public void checkDependency(Piece[] pieces){
		
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
	public void checkIndependency(Piece[] pieces, int caseX, int caseY){
		for(Piece piece : pieces){
			checkEqual(piece);
			if(this != piece){
				for(int i = -this.domain; i < this.domain; ++i){
					if(i != 0){
						model.and(
							model.arithm(coordx, "=", piece.coordx, "+", i),
							model.arithm(coordy, "!=", piece.coordy, "+", i)
						).post();
			
						model.and(
							model.arithm(coordx, "!=", piece.coordx, "+", i),
							model.arithm(coordy, "!=", piece.coordy, "-", i)
						).post();
					}
				}
			}
		}
	}
}
