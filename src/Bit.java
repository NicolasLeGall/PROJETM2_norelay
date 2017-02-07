
public class Bit {

	
	
	
	public int produceBit(User user[], int actualTime, double nb_bit_moy_genere){
		int bitsGeneres = 0;
		int total_bitsGeneres = 0;
		MRG32k3a mrg = new MRG32k3a();
		boolean continuer = true;	
		int i = 0;
		// Création d'un nouveau packet 
		Paquet packet = new Paquet(-1, -1, null);
		for(i = 0; i<15; i++){
			continuer = true;
			// on fait en sorte que quans un user tire une valeur il la garde pendant 3 tour.
			if(user[i].getCompteur_bitsGeneres() == 0){
				//c'est de la magie noire mais sa génére en moyenne 150.5 bit. Sa utilise une fonction logarithmique pour générer les bit. 0.00666666 c'est l'element qu'on change pour faire varier la moyenne ici sa fait 150.5 de moyenne
				bitsGeneres=(int) (((int)((-nb_bit_moy_genere) *(Math.log( 1 - mrg.rand())))));
				user[i].setBitsGeneres(bitsGeneres);	
				user[i].setCompteur_bitsGeneres(10);
			}else{
				bitsGeneres = (int) (((int)((-user[i].getBitsGeneres()) *(Math.log( 1 - mrg.rand())))));;
				user[i].setCompteur_bitsGeneres(user[i].getCompteur_bitsGeneres()-1);
			}
			
			//Mais dans un premier temps pour simplifier on genre juste de maniére alétoire le nombre de bit. Avec comme moyenne nb_bit_moy_genere qui en envoiyer en paramatre
			//bitsGeneres=(int)(mrg.rand()*(nb_bit_moy_genere*2));
		//System.out.println("utilisateur: "+i+" bit geneere "+bitsGeneres);
			total_bitsGeneres = total_bitsGeneres + bitsGeneres;
			packet = user[i].getLePaquet();
			
			user[i].setBit_en_trop(user[i].getBit_en_trop()+bitsGeneres);
	        //Remplissage des paquets 
		
	        while(continuer){

				// si le buffer est vide (il reste rien a envoyer comme bit)
	        	if(user[i].isBufferVide()){
					
					// si le nombre de bit généré est plus grand que la taille d'un paquet
					if(user[i].getBit_en_trop() > 40){
						//on dit que maintenant le buffer sera plus vide
						user[i].setBufferVide(false);
						//en réaliter le buffer n'est jamais vide il a forcement un paquet donc on met nos valeur dedans
						packet.setDateCreation(actualTime);
						packet.setBitsRestants(40);
						user[i].setBit_en_trop(user[i].getBit_en_trop()-40);
						
						user[i].setSommePaquet(user[i].getSommePaquet()+1);
				//System.out.println("utilisateur: "+i+" bit geneere"+user[i].getBit_en_trop());
					}else{//si le nombre de bit dans Bit_en_trop est insuffisant pour les mettre en paquet on les garde en mémoire et on les mettra en paquet quand on en auras plus de 100
						continuer = false;
	
					}
				}else{//si le buffer contient quelque chose (n'est pas vide)
					packet = user[i].getLePaquet();
					// on parcourt les paquet pour arriver au dernier
					while(packet.getNextPaquet() != null){
						packet = packet.getNextPaquet();
					}
					// si le nombre de bit généré est plus grand que la taille d'un paquet
					if(user[i].getBit_en_trop() > 40){
						//on créer un nouveau paquet et on met dedans 100bit et sa date de création
						packet.setNextPaquet(new Paquet(-1, -1, null));
						packet.getNextPaquet().setDateCreation(actualTime);
						packet.getNextPaquet().setBitsRestants(40);
						//on met a jour la varaible bit_en_trop
						user[i].setBit_en_trop(user[i].getBit_en_trop()-40);
						
						/*
						user[i].setLePaquet(packet);
						packet = packet.getNextPaquet();
						*/
						//on met a jour la varaible du nombre de paquet créer
						user[i].setSommePaquet(user[i].getSommePaquet()+1);
					}else{//si le nombre de bit dans Bit_en_trop est insuffisant pour les mettre en paquet on les garde en mémoire et on les mettra en paquet quand on en auras plus de 100
						continuer = false;
					}
				}
	        }
        }
	
		return total_bitsGeneres;
	}
	
	public int consumeBit(User user, int subCarrier, int actualTime){
		int bitConsommes = 0;

		int SNRSubcarrier[] = user.getSNRSubcarrier();
		user.setSommeUR(user.getSommeUR()+1);
		//Si on consomme plus de bits que le paquet en contient. C'est a dire qu'on va consommer tout les bit du paquet et meme si possible quelque un du paquet suivant
		if(user.getLePaquet().getBitsRestants() <= SNRSubcarrier[subCarrier]){
			//Mise à jour pour les statistiques. Comme le paquet est supprimer on regarde sont delais entre le moment ou il a était crée et le moment ou il a était supprimer
			user.setSommeDelais(user.getSommeDelais() + (actualTime - (user.getLePaquet().getDateCreation())));  
			/*si le delais est supérieurs a seuil du PDOR Pour le calcul du PDOR*/
			if((actualTime - (user.getLePaquet().getDateCreation())) >= user.getSeuilPDOR()){
				//on incremente la varaible qui compte le nombre de paquet qui sont arriver avec le sueil du PDOR qu'on a fixer
				user.setSommeDelaisPDOR(user.getSommeDelaisPDOR()+1);
			}
			// si il reste plusieurs packet dans la chaine exemple de chaine (64=>100=>100=>NULL)
			if((user.getLePaquet().getNextPaquet() != null)){
				user.setSommePaquets_consommer(user.getSommePaquets_consommer()+1);
				//On soustrait au prochain paquet le SNR moins le contenu du paquet actuel 
				bitConsommes = SNRSubcarrier[subCarrier];
		//System.out.println(user.getLePaquet().getNextPaquet().getBitsRestants()+" SNRSubcarrier "+SNRSubcarrier[subCarrier]+" premier paquet bit restant "+user.getLePaquet().getBitsRestants());
				//on met le nombre de bit dans le paquet actuel a 0 puis on enleve dans le paquet suivant le nombre de bit qu'on peux enlever avec le SNR qui nous était attribuer.
				user.getLePaquet().getNextPaquet().setBitsRestants((user.getLePaquet().getNextPaquet().getBitsRestants()) - (SNRSubcarrier[subCarrier] - (user.getLePaquet().getBitsRestants())));
				//Puis on supprime le paquet actuel
				user.setLePaquet(user.getLePaquet().getNextPaquet());
			}else{//si il rester qu'un packet dans la chaine. Le nb de bit consommer c'est le nb de bit qu'il rester dans le paquet. On supprimer pas le paquet on met juste ces variable à 0.
				user.setSommePaquets_consommer(user.getSommePaquets_consommer()+1);
				bitConsommes = user.getLePaquet().getBitsRestants();
				user.getLePaquet().setBitsRestants(0);
				user.getLePaquet().setDateCreation(-1);
				user.setBufferVide(true);
		//System.out.println(bitConsommes);
			}
		}else{//Si il y a assez de bits dans ce paquet. C'est par ici qu'on passe 95% du temps le reste de la fonctino c'est des cas particulier
			//on fait bit_restant - SNRSubcarrier dans le paquet. 
			user.getLePaquet().setBitsRestants((user.getLePaquet().getBitsRestants())-(SNRSubcarrier[subCarrier]));
			bitConsommes = SNRSubcarrier[subCarrier];
		}

		// On retourne le nombre de bits consommer 
		
		return bitConsommes;
	}
}
