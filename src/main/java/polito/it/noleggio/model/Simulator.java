package polito.it.noleggio.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import polito.it.noleggio.model.Event.EventType;

public class Simulator {

	//coda eventi
	private PriorityQueue <Event> coda= new PriorityQueue<>();
	
	//parametri simulazione
	private int NC=10 ;//numero auto 
	private Duration  T_IN= Duration.of(10, ChronoUnit.MINUTES); //intervallo clienti 
	
	
	private final LocalTime oraApertura=LocalTime.of(8,00);
	private final LocalTime oraChiusura=LocalTime.of(17,00);

	//variabili mondo
	private int nAuto; //Auto disponibili deposito (tra 0 ed NC)
	
	
	//valori da calcolare
	private int clienti;
	private int insoddisfatti;
	
	//metodi impostare parametri 
	public void setNumCars( int N) {
		this.NC=N;
	}
	
	public void setClientFrequency(Duration d) {
		this.T_IN=d;
	}
	//per restituire
	public int getInsoddisfatti() {
		return insoddisfatti;
	}
	
	public int getClienti() {
		return clienti;
	}
	
	//simulazione eventi 
	public void run() {
		//preparazione iniziale (mondo + coda eventi)
		this.nAuto=NC;
		this.clienti=0;
		this.insoddisfatti=0;
	
		this.coda.clear();
		LocalTime oraArrivoCliente= this.oraApertura;
		//creazione coda
		do {
			Event e=new Event (oraArrivoCliente,EventType.NEW_CLIENT);
			this.coda.add(e);
			oraArrivoCliente= oraArrivoCliente.plus(this.T_IN);
		}while(oraArrivoCliente.isBefore(oraChiusura)) ;
		
		//esecuzione ciclo simulazione 
		while(!this.coda.isEmpty()) {
			Event e =this.coda.poll();
			//System.out.println(e);
			processEvent(e);
		}
		
	}

	private void processEvent(Event e) {
		switch(e.getType()) {
		case NEW_CLIENT: 
			
			if(this.nAuto>0) {
				//cliente servito 
				
				
				//1.aggiorna modello mondo
				this.nAuto--;
				//2.aggiorna risultati
				this.clienti++;
				//3. genera nuovi eventi 
				double num=Math.random();// [0;1)
				Duration travel;
				if(num<1.0/3.0)
					travel =Duration.of(1, ChronoUnit.HOURS);
				if(num<2.0/3.0)
					travel =Duration.of(2, ChronoUnit.HOURS);
				else
					travel =Duration.of(3, ChronoUnit.HOURS);
				
				Event nuovo=new Event(e.getTime().plus(travel),EventType.CAR_RETURNED);
				this.coda.add(nuovo);
				
			}else {
				//cliente insoddisfatto
				this.clienti++;
				this.insoddisfatti++;
			}
			
			break;
			
		case CAR_RETURNED:
			
			//Aggiornare stato 
			this.nAuto++;
			
			break;
		}
	}

















}
