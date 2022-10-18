package pis.Server.com;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import pis.global.com.Ausgabe;
import pis.global.com.Instruction;

//import PIpis.hue2.common.Ausgabe;
//import pis.hue2.common.Instruction;

//import pis.hue2.server.LaunchServer.fromClient;

/**
 * 
 * @author Doriane Koudjou
 * Die Klasse ist ein Multithread Server die mit verschiedenen Client kommuniziert.
 * Es implementiert die Schnittstelle Ausgabe.
 *
 */
public class LaunchServer implements Ausgabe
{
	ServerSocket ss;
	Socket clientSocket;
	PrintWriter writeToClient;
	InputStream in;
	BufferedReader readerFromClient;
	OutputStream out;
	Thread fClient;
	File[] list;
	Iterator it;
	String fileName;
	String deletedName;
	int countClient = 1;
	int enumClient;
	
	/**
	 * Die Methode establish_a_Connection() bereitet die KommunikationsSocket des Servers vor.
	 * @return true wenn die Kommunikationsaufbau erfolgreich ist . Andersfalls false und wirft eine Exception
	 * Exception falls die Kommunikationssockets des Servers beim oeffnen fehlgeschlagen sind. 
	 */
	@Override
	public boolean establish_a_Connection()
	{
		try
		{
			ss = new ServerSocket(13);
			System.out.println("Server started");
			System.out.println("waiting for a Client...");
			return true;
		} catch (IOException e)
		{
			System.err.println("error by starting the Server");
			return false;
		}
	}
	/**
	 * Die Methode connection() erlaubt alle KommunikationsSocket zum Aufbau der Verbindung mit jedem Client .
	 */
	@Override
	public  void connection()
	{
		if (establish_a_Connection())
		{
					try
					{
						while (true)
						{
						clientSocket = ss.accept();
						out = clientSocket.getOutputStream();
						writeToClient = new PrintWriter(out, true);
						fClient = new Thread(new fromClient(clientSocket));
						fClient.start();
						}
					}	catch (IOException e)
					{
						System.err.println("No connection!!");
					}

			
		}
	}
	
/**
 * Die Methode ConfirmationMessageToClient() bestaetigt eine Nachricht mittels ACK.
 * @return true wenn die Bestaetigung erfolgreich ist .
 */
	
	public boolean ConfirmationMessageToClient()
	{
		writeToClient.println(Instruction.ACK);
		writeToClient.flush();
		return true;
	}

/**
 * Die Methode DND_Message() gibt ein DND zurueck wenn eine Aktion  nicht erfolgreich wird.
 */
	public void DND_Message()
	{
		writeToClient.println(Instruction.DND);
		writeToClient.flush();
		System.out.println("Action Denied");
	}
	
	/**
	 * Bei der Methode wir ein DAT gesendet bevor ein Packet(Byte Array) geschickt wird.
	 */

	public  void sendDAT()
	{
		writeToClient.println(Instruction.DAT);
	}

	/**
	 * Die Methode sendet ein File mit der Name des Uebergabeparameters.
	 * 
	 * @param datName
	 *            ist der Name der zusendete File.
	 */
	@Override
	public  void giveData(String datName)
	{
		try
		{
			File file = new File("List/"+datName);
			FileInputStream fis = new FileInputStream(file);
			byte[] dat = new byte[4096];
			BufferedInputStream bis = new BufferedInputStream (fis);
			int fileRead = 0;
			while((fileRead = bis.read(dat, 0, dat.length)) != -1)
			{
			out.write(dat, 0, dat.length);
			out.flush();
			}
			System.out.println("Done!!!");
		} catch (FileNotFoundException e)
		{
			System.err.println("file not Found!!");
		}catch (IOException ie)
		{
			System.err.println("\nProblem with OutputStream");
		}
		
		
	}
	
	/**
	 * Die Methode bekommt ein File mit der Name des Uebergabeparameters.
	 * 
	 * @param data
	 *            ist der Name der File der bekommen wird.Die Datei wird mit der
	 *            gleichen Name erstellt.
	 * Exception 
	 *                wird geworfen falls eine Datei im System nicht existiert.
	 * Exception 
	 *                wird geworfen falls ein Input bzw. Output nicht erfolgreich
	 *                gefangen bzw geschickt wird
	 *
	 */
	
	@Override
	public void receiveData(String data)
	{
		File myFile = new File("List\\"+data);
		try
		{
			out = new FileOutputStream(myFile);
			byte dat[] = new byte[4096];
			int byteRead = 0;
			while ((byteRead = in.read(dat, 0, dat.length)) != -1)
			{
				out.write(dat, 0, dat.length);
			}
			System.out.println("\nData from Client good received!");
			
		} catch (FileNotFoundException e)
		{
			System.err.println("File not found!!");
		}catch (IOException e)
		{
			System.err.println("\nProblem with OutputStream!");
		}
	}
	
	/**
	 * Die Methode loescht eine Datei auf dem Server nach Befragung des Clients
	 * @param Ddata ist die Datei , zu loeschen. 
	 * Falls die Datei nicht existiert,wird die AKtion nicht weitergeführt.
	 */

	public void deleteData(String Ddata)
	{
		File Dfile = new File("List/" + Ddata);

		if (Dfile.exists())
		{
			if (Dfile.delete())
				System.out.println("good Deleted!");
		}
		
	}
	
	/**
	 * Die Methode gibt die Liste aller auf dem Server verfuegbaren Dateien.
	 */
	
	public void giveList()
	{
		
			System.out.println("Sending files to Client...");
			File files = new File("List");
			list = files.listFiles();
			for ( int i = 0; i< list.length;i++)
			{
				if(list[i].isFile())
				{
					String fileName = list[i].getName();
					writeToClient.println("List/"+fileName);
					writeToClient.flush();
				}
			}
			System.out.println("This is a List of my Files: \n"+Arrays.toString(list));
		
	}

	
	/**
	 * Baut die Kommunikationsverbindung mit dem Client ab beim Schicken von DSC
	 * Wenn das Schließen nicht Erfolgreich wird, wird eine Exception geworfen.
	 * Exception 
	 */
	@Override
	public boolean disconnection()
	{
		Instruction dsc = Instruction.DSC;
		writeToClient.println(dsc);

		try
		{
			writeToClient.close();
			readerFromClient.close();
			clientSocket.close();
			ss.close();
			System.out.println("Client Socket closed!");
			return true;
		} catch (IOException e)
		{
			System.err.println("Close failed!");
			return false;
		}
	}

/**
 * Die Klasse fromClient ist dafuer zustaendig , alle vom Client geschickte Nachrichten zu verarbeiten.
 * @author Doriane Koudjou
 * Alle diese Inputs werden von einem Thread verwaltet . Deswegen implementiert diese Klasse die Runnable Schnittstelle.
 * 
 * Bei einem CON vom Client geschickt , wird der Server falls es weniger als 3 Clients online sind die Verbindung bestaetigen
 * Bei einem DSC vom Client, wird der Server einfach mit einem DSC antworten und die KommunikationsSocket mittels der methode disconnection() schicken.
 * Bei einem LST vom Client, wird eine Liste aller im ServerSystem verfuegbare Dateien vom Server geschickt.
 * Bei einem GET vom Client, wird eine Datei falls es noch existiert(Nicht vom anderen Benutzer geloescht) vom server geschickt .
 * Bei einem PUT vom Client, wird eine der Server falls er noch nicht über die vorgeschlagene Datei verfuegt bestaetigen.
 * Bei einem DEL vom Client, wird der Server die gefordete Datei löschen (falls es existiert und noch nicht von einem anderen Benutzer geloscht wurde)
 */

	public class fromClient implements Runnable
	{
		public fromClient(Socket client)
		{
			try
			{
				in = clientSocket.getInputStream();
				readerFromClient = new BufferedReader(new InputStreamReader(in));
			} catch (IOException e)
			{
				System.err.println("Nothing written on Stream");
			}

		}

		@Override
		public void run()
		{

			try
			{
				String line;
				while ((line = readerFromClient.readLine()) != null)
				{
					System.out.println(line);
					if (line.equals(Instruction.CON.toString()))
					{
							if(countClient <= 3) {
									System.out.println("Message received from Client " +countClient +" : " + line);
									if (ConfirmationMessageToClient())
									{
										System.out.println("confirmation given to Client." + "\n");
									}
									enumClient = countClient;
									countClient++;
							}else {
								DND_Message(); 
								disconnection();
								System.out.println("MORE THAN 3 USERS!!!");
								}
								
					} else if (line.equals(Instruction.DSC.toString()))
					{
						System.out.println("Message received from Client " +enumClient +" : " + line);
						System.out.println("Client to be disconnected..." + "\n");
						disconnection();
							
					} else if (line.equals(Instruction.ACK.toString()))
					{
						System.out.println("Message received from Client " +enumClient +" : " + line);

						if (ConfirmationMessageToClient())
							System.out.println("confirmation given to Client." + "\n");
					} else if (line.equals(Instruction.LST.toString()))
					{
						System.out.println("Message received from Client " +enumClient +" : " + line);
						if (ConfirmationMessageToClient())
						{
							System.out.println("confirmation given to Client." + "\n");
							while ((line = readerFromClient.readLine()) != null)
							{
								System.out.println(line);
								if (line.equals(Instruction.ACK.toString()))
								{
									sendDAT();
									giveList();
									System.out.println("Dat sent");
								}
								while ((line = readerFromClient.readLine()) != null)
								{
									if (line.equals(Instruction.ACK.toString()))
									{
										System.out.println("List succesfully sent!!");
									}
								}

							}

						}
					} else if (line.equals(Instruction.GET.toString()))
					{
						System.out.println("Message received from Client " +enumClient +" : " + line);
						while ((line = readerFromClient.readLine()) != null)
						{
							System.out.println(line);
							fileName = line;
							String[] parts = fileName.split("/");
							String file = parts[1];
							File files = new File("List/"+file);
							if(!(files.exists()))
							{
								DND_Message();
								break;
							}
							if (ConfirmationMessageToClient())
							{
								System.out.println("confirmation given to Client." + "\n");
								while ((line = readerFromClient.readLine()) != null)
								{
									System.out.println(line);
									if (line.equals(Instruction.ACK.toString()))
									{
										sendDAT();
										giveData(file);
										System.out.println("Dat sent");
									}
									while ((line = readerFromClient.readLine()) != null)
									{
										if (line.equals(Instruction.ACK.toString()))
										{
											System.out.println("Data succesfully sent!!");
										} else
										{
											DND_Message();
										}
									}
								}
							}
						}
					} else if (line.equals(Instruction.PUT.toString()))
					{
						System.out.println("Message received from Client " +enumClient +" : " + line);
						while ((line = readerFromClient.readLine()) != null)
						{
							System.out.println(line);
							fileName = line;
							String[] parts = fileName.split("/");
							String file = parts[1];
							File files = new File("List/"+file);
							if((files.exists()))
							{
								DND_Message();
								break;
							}
							if (ConfirmationMessageToClient())
							{
								System.out.println("confirmation given to Client." + "\n");
								while ((line = readerFromClient.readLine()) != null)
								{
									if (line.equals(Instruction.DAT.toString()))
									{
										ConfirmationMessageToClient();
										receiveData(file);
									}
								}
							}
						  
						}
					} else if (line.equals(Instruction.DEL.toString()))
					{
						System.out.println("Message received from Client " +enumClient +" : " + line);
						while ((line = readerFromClient.readLine()) != null)
						{
							System.out.println(line);
							deletedName = line;
							String[] parts = deletedName.split("/");
							String file = parts[1];
							File files = new File("List/"+file);
							if(!(files.exists()))
							{
								DND_Message();
								System.out.println("The file was Already deleted!!!");
								break;
							}
							deleteData(file);
							if (ConfirmationMessageToClient())
							{
								System.out.println("confirmation given to Client." + "\n");
							}
						}
					}
				}
			} catch (IOException e)
			{
				
			}

		}

	}
/**
 * Methode zum Ausfuehren des Servers 
 * @param args alle Inputs
 */
	public static void main(String[] args)
	{
		LaunchServer ls = new LaunchServer();
		ls.connection();
	}
}

