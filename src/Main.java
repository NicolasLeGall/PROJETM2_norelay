import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		
		int nb_tours;
		int choixAlgo;
		int i, j, g;
		
		double nb_bit_moy_genere = 230;
		
		Algorithme scheduler = new Algorithme();
		Bit gestion_de_bit = new Bit();
		Scanner scanInt = new Scanner(System.in);
		User tab_user[] = new User[15];
		
		tab_user[0] = new User(0, 80);
		tab_user[1] = new User(0, 250);
		tab_user[2] = new User(0, 1000);
	
		tab_user[3] = new User(25, 80);
		tab_user[4] = new User(25, 250);
		tab_user[5] = new User(25, 1000);
		
		tab_user[6] = new User(50, 80);
		tab_user[7] = new User(50, 250);
		tab_user[8] = new User(50, 1000);
		
		tab_user[9] = new User(75, 80);
		tab_user[10] = new User(75, 250);
		tab_user[11] = new User(75, 1000);
		
		tab_user[12] = new User(100, 80);
		tab_user[13] = new User(100, 250);
		tab_user[14] = new User(100, 1000);
		
		
		int actualTime = 0;
		double debitTotal = 0;
		double nbBitsgenere = 0;
		double debit_total_simu = 0;
		double total_nbBitsgenere = 0;
		double sommeDelais = 0 ;
		double nbPaquetsTotal = 0 ; 
		double nbPaquetsTotalPDOR = 0 ;
		double nbPaquetsTotalsommePaquets_consommer = 0 ;
		double delais_moyen = 0;
		double PDOR = 0;
		double user_sommeUR = 0;
		double nbPaquetsNonEnvoyes = 0;
		double res_sommeUR = 0;
		double bit_par_UR = 0;
		double taux_remplissage_buffer = 0;
		double somme_bitsRestants = 0;
		
		//initMatriceDebits();
		
		System.out.println("Nombre de tours pour la simulation, chaque tour = 2ms: ");
		nb_tours = scanInt.nextInt();          
		//System.out.println("nombre de tour "+nb_tours);
		System.out.println("Algorithme : 1 pour RR, 2 pour MAXSNR, 3 pour WFO, 4 pour CEI, 5 pour CEI+WFO :");
		choixAlgo = scanInt.nextInt();
		//System.out.println("choix algo "+choixAlgo);
		scanInt.close(); 
		
		/*On écrit dans le fichier csv le nom des colonnes */
		String fichier ="Resultat.csv";
		FileWriter fw = new FileWriter (fichier);
		BufferedWriter bw = new BufferedWriter (fw);
		PrintWriter fichierSortie = new PrintWriter (bw); 
		try {
			
			fichierSortie.println ("nb_tours="+nb_tours+";choixAlgo="+choixAlgo+";\n"); 
			fichierSortie.println ("nb_bit_moy_genere;debit_total_simu;total_nbBitsgenere;debitTotal;delais_moyen;PDOR;res_sommeUR;bit_par_UR;taux_remplissage_buffer;nbPaquetsTotalsommePaquets_consommer;"); 
			
			fichierSortie.close();
			
		}catch (Exception e){
			System.out.println(e.toString());
		}	
		
		
		/*boucle principal on incrément nb_bit_moy_genere de 10 par tour*/
		while(nb_bit_moy_genere < 430){
				
			// un packet qui sert de paquet tampoon pour récuperé des informations
			Paquet packet = new Paquet(0, 0, null);
			
			//boucle du temps
			for(i = 0; i < nb_tours; i++){
				/*On donne a chaque utilisateur un débit pour les 128 subcarrieur qui varie de 0 à 10 et qui a pour moyenne 6*/
				initMatriceDebits(tab_user);	
				
				/*for(i=0; i< 15;i++){
					System.out.println("\n user: "+i);
					for(j=0; j<128; j++){
						System.out.print(tab_user[i].getSNRSubcarrier_case(j)+" ");		
					}	
				}*/
				
				
				/*Initialisation des paquets utilisateurs*/
				/*Le temps de création d'un packet est donnée a chaque packet avec actualTime */
				nbBitsgenere = gestion_de_bit.produceBit(tab_user, actualTime, nb_bit_moy_genere);
				total_nbBitsgenere = total_nbBitsgenere + nbBitsgenere;
				
				
				//listage du contenue des paquets
			/*	for(j=0; j<15;j++){
					System.out.println("utilisateur: "+j+" bit en trop "+tab_user[j].getBit_en_trop());
					packet = tab_user[j].getLePaquet();
					System.out.print(packet.getBitsRestants()+"=>");
					while(packet.getNextPaquet() != null){
						//System.out.println("test3");
						packet = packet.getNextPaquet();
						System.out.print(packet.getBitsRestants()+"=>");
					}
					System.out.println();
				}
				System.out.println();
					*/
	
				/*Application de l'algorithme et ôtage des bits dans les paquets*/
				if(choixAlgo == 1){
					debitTotal += scheduler.RR(tab_user, actualTime);
				}
				else if(choixAlgo == 2){
					debitTotal += scheduler.maxSNR(tab_user, actualTime);
				}
				else if(choixAlgo == 3){
					debitTotal += scheduler.WFO(tab_user, actualTime);
				}
				else if(choixAlgo == 4){
					debitTotal += scheduler.CEI(tab_user, actualTime);
				}
				else if(choixAlgo == 5){
					debitTotal += scheduler.CEI_WFO(tab_user, actualTime);
				}
				else{
					System.out.println("choix de l'algorithme mauvais. Arret. \n");
				}
			
				
				/*Calcul du nombre de bit qui reste dans les paquets non envoyer pour chaque utilisateurs*/
				for(g = 0; g < 15; g++){
					packet = tab_user[g].getLePaquet();
					while(packet.getNextPaquet() != null){
						somme_bitsRestants = somme_bitsRestants + packet.getBitsRestants();
						packet = packet.getNextPaquet();
					}
				}
				
				
				//+2 car on a 5 time slot et qu'on a dit que sa représenter 2ms
				actualTime += 2;
	
	
				/*
				//listage du contenue des paquets
				for(j=0; j<15;j++){
					System.out.println("utilisateur: "+j+" bit en trop "+tab_user[j].getBit_en_trop());
					packet = tab_user[j].getLePaquet();
					System.out.print(packet.getBitsRestants()+"=>");
					while(packet.getNextPaquet() != null){
						//System.out.println("test3");
						packet = packet.getNextPaquet();
						System.out.print(packet.getBitsRestants()+"=>");
					}
					System.out.println();
				}*/
				
			}
			//parcour des utilisateurs
			for(j=0; j<15;j++){
				/*Si il reste des packet non envoyer --> Récupération des délais et nb de paquets restants dans les paquets non envoyes */
				if(tab_user[j].getLePaquet().getBitsRestants() != 0){
					packet = tab_user[j].getLePaquet();
					while(packet.getNextPaquet() != null){
						//System.out.println("test3");
						packet = packet.getNextPaquet();
						nbPaquetsNonEnvoyes++;
					}
						/* Stats globales */
						/*sommeDelais += (monAntenne.actualTime - tmpPacket->dateCreation);*/
				}
				
				/* Récupération des delais et paquets enregistrés */
				sommeDelais += tab_user[j].getSommeDelais();
				nbPaquetsTotal += tab_user[j].getSommePaquet();
				nbPaquetsTotalPDOR+= tab_user[j].getSommeDelaisPDOR();
				nbPaquetsTotalsommePaquets_consommer += tab_user[j].getSommePaquets_consommer();
				user_sommeUR += tab_user[j].getSommeUR();
			}
			
			bit_par_UR = debitTotal/((double)user_sommeUR);
			taux_remplissage_buffer = somme_bitsRestants/actualTime;
			res_sommeUR = ((double)(user_sommeUR)/(double)(5*128*nb_tours))*100;
			debit_total_simu = debitTotal/actualTime;
			delais_moyen = sommeDelais/(nbPaquetsTotalsommePaquets_consommer);
			PDOR=((double)nbPaquetsTotalPDOR/((double)(nbPaquetsTotalsommePaquets_consommer)))*100;
			if(nbPaquetsTotalsommePaquets_consommer == 0){
				PDOR = 100;
			}
			
			System.out.println("nbPaquetsNonEnvoyes : "+nbPaquetsNonEnvoyes);
			System.out.println("nbPaquetsEnvoyes : "+nbPaquetsTotalsommePaquets_consommer );
			System.out.println("NonEnvoyes+Envoyes : "+(nbPaquetsNonEnvoyes+nbPaquetsTotalsommePaquets_consommer)+" || nbPaquetsTotal reel : "+nbPaquetsTotal);
			System.out.println("Pourcentage de bande passante utilisé : "+res_sommeUR);
			System.out.println("Bit par Unité de ressource : "+bit_par_UR);
			System.out.println("somme_bitsRestants/le temps : "+taux_remplissage_buffer);
			System.out.println("PDOR : "+PDOR+" Ce resultat veux rien dire pour l'instant car il ont outs un seuil de PDOR different");
			System.out.println("Nombre total de Bits genere : "+total_nbBitsgenere+" bits || consommer : "+debitTotal+" bits");
			System.out.println("Delai moyen : "+delais_moyen+" ms  || Somme des delais: "+sommeDelais+" ms");
			System.out.println("Débit total de la simulation: "+debit_total_simu+" bits/ms || bit genere : "+nb_bit_moy_genere);
			System.out.println("");
			
			try {
				fw = new FileWriter (fichier, true);
				bw = new BufferedWriter (fw);
				fichierSortie = new PrintWriter (bw); 
				//on écrit dans le fichier les resultat obtenue
				fichierSortie.println (nb_bit_moy_genere+";"+debit_total_simu+";"+total_nbBitsgenere+";"+debitTotal+";"+delais_moyen+";"+PDOR+";"+res_sommeUR+";"+bit_par_UR+";"+taux_remplissage_buffer+";"+nbPaquetsTotalsommePaquets_consommer+";"); 
				
				fichierSortie.close();
			}catch (Exception e){
				System.out.println(e.toString());
			}
			
			
			
			if(delais_moyen > 1200){
				//on incrémente le nb de bit qu'on va générer au prochain tour
				nb_bit_moy_genere = nb_bit_moy_genere + 10;
				
				
			}else{
				//on incrémente le nb de bit qu'on va générer au prochain tour
				nb_bit_moy_genere = nb_bit_moy_genere + 2;
				
			}
			
			
			
			//on réinitialise toute les variables
			debitTotal = 0;
			nbBitsgenere = 0;
			debit_total_simu = 0;
			total_nbBitsgenere = 0;
			sommeDelais = 0 ;
			nbPaquetsTotal = 0 ; 
			nbPaquetsTotalPDOR = 0 ;
			nbPaquetsTotalsommePaquets_consommer = 0 ;
			delais_moyen = 0;
			PDOR = 0;
			user_sommeUR = 0;
			nbPaquetsNonEnvoyes = 0;
			res_sommeUR = 0;
			bit_par_UR = 0;
			taux_remplissage_buffer = 0;
			somme_bitsRestants = 0;
			
			actualTime = 0;
			
			tab_user[0] = new User(0, 80);
			tab_user[1] = new User(0, 250);
			tab_user[2] = new User(0, 1000);
		
			tab_user[3] = new User(25, 80);
			tab_user[4] = new User(25, 250);
			tab_user[5] = new User(25, 1000);
			
			tab_user[6] = new User(50, 80);
			tab_user[7] = new User(50, 250);
			tab_user[8] = new User(50, 1000);
			
			tab_user[9] = new User(75, 80);
			tab_user[10] = new User(75, 250);
			tab_user[11] = new User(75, 1000);
			
			tab_user[12] = new User(100, 80);
			tab_user[13] = new User(100, 250);
			tab_user[14] = new User(100, 1000);
			
		}
		
		System.out.println("Simulation terminer");
	}

	
	
	static void initMatriceDebits(User user[]){
		int SNRActuels[] = new int[128];
		int j = 0;
		int i = 0;
		double mkn = 0;
		int d = 1;
		double alpha = 0;
		double puissance = 222;
		MRG32k3a mrg = new MRG32k3a();
		double somme= 0;
		//parcour des utilisateurs
		for(i = 0; i<15; i++){
			for(j = 0; j<128; j++){//parcour des subcarrier. On parcour pas les timeslots car on considére que le mkn ne varie pas sur un si petit laps de temps. Donc oui les Times slot ne serve a rien.
				//System.out.println("test3 = "+mrg.rand());
				//formule du mkn demander a cédric pour plus de détail moi je m'en rapelle plus. Mais en gros sa prend en compte la distance, la puissance, sa génre des log etc
				alpha =((-1 / 1) *(Math.log( 1 - mrg.rand())));/*alpha = 1 en moyenne*/
				mkn=1+(((puissance)*alpha)/(d*d));
				//System.out.println("test4 = "+((int)((Math.log(mkn)/Math.log(2))-0.5)));
				/*-0.5 pour et (int) pour convertir a l'arrondie inférieur*/
				SNRActuels[j] = ((int)((Math.log(mkn)/Math.log(2))-0.5));// sa fait 6 en moyenne
	
				//somme = somme + SNRActuels[j];
				//System.out.println("SNRActuels sur UR"+j+" = "+SNRActuels[j]);
				user[i].setSNRSubcarrier_case(SNRActuels, j);
			}
			
			//System.out.println("moyenne = "+somme/128);
		}
	}

	
}
