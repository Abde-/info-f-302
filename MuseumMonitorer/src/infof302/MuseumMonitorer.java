package infof302;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class MuseumMonitorer {

	// les directions sont representés avec des numéros
	// (vu qu'on peut utiliser que des IntVar)
	private static int NORD = 1;
	private static int SUD = 2;
	private static int EST = 3;
	private static int OUEST = 4;
	private static IntVar[][] camerasorient;
	private static IntVar[][] camerasexist;
	
	private static boolean[][] murs = null;
	private static int n = 0;
	private static int m = 0;
	
	/**
	 * Méthode pour rajouter élement e à l'array a.
	 * 
	 * @param a
	 * @param e
	 * @return
	 */
	public static <T> T[] addElement(T[] a, T e) {
	    a  = Arrays.copyOf(a, a.length + 1);
	    a[a.length - 1] = e;
	    return a;
	}
	
	/**
	 * On passe le nom du fichier où se trouve la carte en paramètre.
	 * 
	 * @param filename
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] filename) throws NumberFormatException, IOException {
		//check if file in param
		if(filename.length < 1){
			System.out.println("Veuillez entrer le nom du fichier");
			return;
		}
		parseFile(filename[0]);
		
		Model model = new Model("Museum monitoring");
		
		// orientation des cameras
		camOrientation(model);
		
		// existance des cameras
		camExistence(model);
		
		// variable à minimiser (la somme des existances de caméra)
		IntVar minimize = model.intVar("Nombre de caméras",0,n*n);
		
		// première contrainte: existance des caméras = 1 ssi orientation des cameras != NULL
		for(int i = 0; i < n; ++i){
			for(int j = 0; j < m; ++j){
				model.or(
					model.and(
						model.arithm(camerasorient[i][j], "=", 0),
						model.arithm(camerasexist[i][j], "=", 0)
						),
					model.and(
						model.arithm(camerasorient[i][j], "!=", 0),
						model.arithm(camerasexist[i][j], "=", 1)
						)
				).post();
			}
		}
		
		
		// deuxieme contrainte: il ne peut pas avoir des caméras dans les murs
		noCamInWalls(model);
		
		// troisième contrainte: chaque pièce doit être occupée par une caméra ou dominée par une
		constraintOnCameras(model);
		
		// convertir en array à 1D car la fonction sum prend que des array
		// à une dimension
		IntVar[] cases_to_minimize = new IntVar[n*m];
		if(m < n){
			for (int i = 0; i < n; ++i){
				for (int j = 0; j < m; ++j){
					cases_to_minimize[j*n+i] = camerasexist[i][j];
				}
			}
		} else {
			for (int i = 0; i < n; ++i){
				for (int j = 0; j < m; ++j){
					cases_to_minimize[i*n+j] = camerasexist[i][j];
				}
			}
		}
		
		// la somme de chevaliers
		model.sum(cases_to_minimize, "=", minimize).post();
		
		// on minimise le nombre maximum de chevaliers
		model.setObjective(Model.MINIMIZE, minimize);
		
		// trouver solution
		Solver solver = model.getSolver();
		
		while(solver.solve()){
			System.out.println();
			System.out.println(minimize.getValue());
			printSolution(camerasorient);
		}
		
	}

	private static void constraintOnCameras(Model model) {
		for(int i = 0; i < n; ++i){
			for(int j = 0; j < m; ++j){
				
				if(!murs[i][j]){
					Constraint[] contraintes = new Constraint[]{};
					
					
					Constraint[] nordconts = new Constraint[]{};
					for(int k = n-1; k > i; --k){
						
						boolean flagL = true;
						// les contraintes pour le L qui se trouve entre la piece et le k
						Constraint[] contsL = new Constraint[]{};
						for(int l = k-1; l > i; --l){
							if(murs[l][j]){
								flagL = false;
								break;
							}
							
							contsL = addElement(contsL,
									model.arithm(camerasexist[l][j],"=",0)
									);
						}
						
						// si jamais toutes les cases de L à K sont libres
						if (flagL){
							if (contsL.length > 0 )
								nordconts = 
								addElement(nordconts,model.and(
										model.arithm(camerasorient[k][j], "=", NORD),
										model.and(contsL)
										)
									);
							
							else
								nordconts = 
									addElement(nordconts,
											model.arithm(camerasorient[k][j], "=", NORD));
						}
					}
					
					
					
					// il existe au moins une piece au sud qui pointe vers le nord -> on rajoute
					if(nordconts.length > 0)
					contraintes = addElement(contraintes, model.or(nordconts));
					
					
					
					Constraint[] sudconts = new Constraint[]{};
					for(int k = 0; k < i; ++k){
						
						boolean flagL = true;
						// les contraintes pour le L qui se trouve entre la piece et le k
						Constraint[] contsL = new Constraint[]{};
						for(int l = k+1; l < i; ++l){
							if(murs[l][j]){
								flagL = false;
								break;
							}
							
							contsL = addElement(contsL,
									model.arithm(camerasexist[l][j],"=",0)
									);
						}
						
						// si jamais toutes les cases de L à K sont libres
						if (flagL ){
							if(contsL.length > 0)
								sudconts =
								addElement(sudconts,
										model.and(
											model.arithm(camerasorient[k][j], "=", SUD),
											model.and(contsL)
											)
										);
							else
								sudconts =
								addElement(sudconts,
								model.arithm(camerasorient[k][j], "=", SUD));
						}
					}
					
					// il existe au moins une piece au nord qui pointe vers le sud -> on rajoute
					if(sudconts.length > 0)
					contraintes = addElement(contraintes, model.or(sudconts));
					
					
					Constraint[] estconts = new Constraint[]{};
					for(int k = 0; k < j; ++k){
						
						boolean flagL = true;
						// les contraintes pour le L qui se trouve entre la piece et le k
						Constraint[] contsL = new Constraint[]{};
						for(int l = k+1; l < j; ++l){
							if(murs[i][l]){
								flagL = false;
								break;
							}

							contsL = addElement(contsL,
									model.arithm(camerasexist[i][l],"=",0)
									);
						}
						
						// si jamais toutes les cases de L à K sont libres
						if (flagL){
							if(contsL.length > 0)
								estconts =
								addElement(estconts,
										model.and(
											model.arithm(camerasorient[i][k], "=", EST),
											model.and(contsL)
											)
										);
							else
								estconts =
								addElement(estconts,
								model.arithm(camerasorient[i][k], "=", EST));
						}
					}
					
					// il existe au moins une piece au ouest qui pointe vers l'est -> on rajoute
					if(estconts.length > 0)
					contraintes = addElement(contraintes, model.or(estconts));
					
					
					
					Constraint[] ouestconts = new Constraint[]{};
					for(int k = m-1; k > j; --k){
						
						boolean flagL = true;
						// les contraintes pour le L qui se trouve entre la piece et le k
						Constraint[] contsL = new Constraint[]{};
						for(int l = k-1; l > j; --l){
							if(murs[i][l]){
								flagL = false;
								break;
							}
							
							contsL = addElement(contsL,
									model.arithm(camerasexist[i][l],"=",0)
									);
						}
						
						// si jamais toutes les cases de L à K sont libres
						if (flagL){
							if(contsL.length > 0)
								ouestconts =
								addElement(ouestconts,
										model.and(
											model.arithm(camerasorient[i][k], "=", OUEST),
											model.and(contsL)
											)
										);
							else
								ouestconts =
									addElement(ouestconts,
											model.arithm(camerasorient[i][k], "=", OUEST));
						}
					}
					
					// il existe au moins une piece à l'est qui pointe vers le ouest -> on rajoute
					if(ouestconts.length > 0)
					contraintes = addElement(contraintes, model.or(ouestconts));
					
					
					// on rajoute la contrainte qu'il existe une camera la ou il est
					if(contraintes.length > 0)
						model.or(
							model.or(contraintes),
							model.arithm(camerasexist[i][j], "=", 1)
							).post();
					else
						model.arithm(camerasexist[i][j], "=", 1).post();
					
				}
			}
		}
	}

	private static void noCamInWalls(Model model) {
		for(int i = 0; i < n; ++i){
			for(int j = 0; j < m; ++j){
				if(murs[i][j]){
					model.arithm(camerasexist[i][j],"=",0).post();
				}
			}
		}
	}

	private static void camExistence(Model model) {
		camerasexist = new IntVar[n][m];
		for(int i = 0; i < n; ++i){
			for(int j = 0; j < m; ++j){
				camerasexist[i][j] = model.intVar(Integer.toString(i)+","+Integer.toString(j)+" exists", 0, 1);
			}	
		}
	}

	private static void camOrientation(Model model) {
		camerasorient = new IntVar[n][m];
		for(int i = 0; i < n; ++i){
			for(int j = 0; j < m; ++j){
				camerasorient[i][j] = model.intVar(Integer.toString(i)+","+Integer.toString(j)+" points to", 0, 4);
			}	
		}
	}

	/**
	 * Fonction pour parser le fichier avec la map -> convertir * en TRUE et espace en FALSE (comme exemple pour TEST).
	 * 
	 * @param filename
	 * @return board
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	private static void parseFile(String filename) throws NumberFormatException, IOException {
		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		List<String> lines = Files.readAllLines(Paths.get(filename), Charset.defaultCharset());
		n = lines.size() - 2;

		String line = bufferedReader.readLine(); //pour passer la première ligne
		m = line.length() - 2;
		
		murs = new boolean[n][m];
		for(int i=0; i<n; ++i){
			line = bufferedReader.readLine();
			String[] data = line.split("");
			for(int j=1; j<m+1; ++j){
				murs[i][j-1] = (data[j].equals("*") ? true : false);
			}
		}
		fileReader.close();
	}
	
	private static void printSolution(IntVar[][] cameras) {
		for(int k = 0; k < m+2; ++k){
			System.out.print("*");
		}
		System.out.println();
		
		for(int i = 0; i < n; ++i){
			System.out.print("*");
			for(int j = 0; j < m; ++j){
				
				if(murs[i][j])
					System.out.print("*");
				
				else
					System.out.print(
						cameras[i][j].getValue() == NORD ? "N" : 
							cameras[i][j].getValue() == EST ? "E" :
								cameras[i][j].getValue() == OUEST ? "O" : 
									cameras[i][j].getValue() == SUD ? "S" : " "
						);
			}
			System.out.print("*");
			System.out.println();
		}
		
		for(int k = 0; k < m+2; ++k){
			System.out.print("*");
		}
		System.out.println();

	}
}
