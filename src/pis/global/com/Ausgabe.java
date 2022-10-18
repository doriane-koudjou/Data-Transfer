package pis.global.com;

public interface Ausgabe {
	public boolean establish_a_Connection();
	public boolean disconnection();
	public void connection();
	public void giveData(String data);
	public void receiveData(String data );

}
