
public class Paquet {

	private int dateCreation;
	private int bitsRestants;
	private Paquet nextPaquet;
	
	public Paquet(int datecreation, int bitrestant, Paquet nextpaquet){
		
		dateCreation=datecreation;
		bitsRestants=bitrestant;
		nextPaquet=nextpaquet;
		
	}

	public int getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(int dateCreation) {
		this.dateCreation = dateCreation;
	}

	public int getBitsRestants() {
		return bitsRestants;
	}

	public void setBitsRestants(int bitsRestants) {
		this.bitsRestants = bitsRestants;
	}

	public Paquet getNextPaquet() {
		return nextPaquet;
	}

	public void setNextPaquet(Paquet nextPaquet) {
		this.nextPaquet = nextPaquet;
	}
}
