
public class TileExtended extends Tile{
	
	Targhets targhets
	//Tile source; //comparable con distanza in source 
	int costo;
	
	//funzione che incrementa il costo
	updateCost(){
		costo +=1;
	}
	
	TileExtended(Tile e){
		
		//passare targhets 
		
		super(e);
		costo = 0;
	}
	
	heuristicValue( ){
		
	}
	
	//comparable o comparator
	//costo + heuristicValue
}
