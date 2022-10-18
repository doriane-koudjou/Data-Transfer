package pis.Client.com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

//import pis.hue2.common.Ausgabe;
//import pis.hue2.common.Instruction;
import pis.global.com.Ausgabe;
import pis.global.com.Instruction;
/**
 * 
 * @author bautrelle fotso
 * Die Klasse ist ein Multithread Client die mit verschiedenen Server kommuniziert.
 * Es implementiert die Schnittstelle Ausgabe.
 *
 */
public class LaunchClient implements Ausgabe
{
	JFrame frame;
	JPanel jpanel;
	JLabel jlabel;
	JButton connection, disconnection, list, getData, deleteData, putData;
	JTextArea chatArea;
	JTextField port, host, userName, warning;
	JScrollPane scrollChatArea, scrollServerList, scrollClientList;
	JList<String> elemListServer;
	JList<String> elemListClient;
	DefaultListModel<String> modelServer ;
	DefaultListModel<String> modelClient ;
	List<String> arrayElementServer = new ArrayList<String>();
	List<String> arrayElementClient = new ArrayList<String>();
	Socket socket;
	//LaunchClient startClient;
	OutputStream out;
	PrintWriter writer;
	InputStream in;
	BufferedReader reader;

	/**
	 * Bei der Methode clientGui wird alle Komponente, die zum Kommunikation gebraucht wird deklariert.
	 */
	public void clientGui()
	{
		frame = new JFrame();
		frame.setTitle("Datei-Client");
		frame.setSize(800, 700);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setLocationRelativeTo(null);

		connection = new JButton("Connect");
		disconnection = new JButton("Disc");
		list = new JButton("List");
		getData = new JButton("Get");
		deleteData = new JButton("Del");
		putData = new JButton("Put");
		
		modelServer = new DefaultListModel<String>();
		elemListServer = new JList<String>(modelServer);
		elemListServer.setVisible(true);
		
		modelClient = new DefaultListModel<String>();
		elemListClient = new JList<String>(modelClient);
		elemListClient.setVisible(true);
		
		chatArea = new JTextArea("Start Chat here...", 20, 60);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);

		port = new JTextField("PortNumber : 13", 10);
		port.setForeground(Color.BLACK);
		port.setBackground(Color.WHITE);
		port.setPreferredSize(new Dimension(40, 40));
		port.setEditable(true);

		userName = new JTextField("Username", 10);
		userName.setForeground(Color.BLACK);
		userName.setBackground(Color.WHITE);
		userName.setPreferredSize(new Dimension(40, 40));
		userName.setEditable(true);

		host = new JTextField("127.0.0.1", 10);
		host.setForeground(Color.BLACK);
		host.setBackground(Color.WHITE);
		host.setPreferredSize(new Dimension(40, 40));
		host.setEditable(true);

		warning = new JTextField("warning");
        warning.setForeground(Color.BLACK);
        warning.setBackground(Color.RED);
        warning.setPreferredSize(new Dimension(500,40));
        warning.setEditable(true);
        
		jpanel = new JPanel();
		jlabel = new JLabel();
		
		scrollChatArea = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollChatArea.setViewportView(chatArea);
		
		scrollServerList = new JScrollPane(elemListServer, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollServerList.setViewportView(elemListServer);
		
		scrollClientList = new JScrollPane(elemListClient, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollClientList.setViewportView(elemListClient);
		
		jpanel.add(connection);
		jpanel.add(disconnection);
		jpanel.add(list);
		jpanel.add(getData);
		jpanel.add(deleteData);
		jpanel.add(putData);
		jpanel.add(port);
		jpanel.add(userName);
		jpanel.add(host);
		jpanel.add(jlabel);
		jpanel.add(scrollChatArea);
		jpanel.add(scrollServerList);
		jpanel.add(scrollClientList);
		jpanel.add(warning);
		frame.add(jpanel);
	}
	
	/**
	 * Die Methode ButtonsInit() ist dafür zuständig, alle Komponente der Oberflaeche des Clients zum Laufen zu bringen.
	 * Die Komponente getData, putData und deleteData benutzen als Uebergabeparameter die im JList (elementListServer und ElementListClient) ausgewaehlte Elemente.
	 * Falls der Benutzer auf dem falschen List klickt, die sich auf der Oberflaeche befindet, wird ein warning geworfen.
	 */
	public void ButtonsInit() {
					
				clientGui();
				
						connection.addActionListener(new ActionListener()
						{
							
							@Override
							public void actionPerformed(ActionEvent e)
							{
								if (establish_a_Connection())
								{
								Thread connect = new Thread(new Runnable()
								{
									
									@Override
									public void run()
									{
										connection();
									}
								});
								connect.start();
								
								}
							}
						});
						
					

						disconnection.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								if (establish_a_Connection())
								{
								Thread disc = new Thread(new Runnable()
								{
									
									@Override
									public void run()
									{
										warning.setText("warning");
										disconnection();	
									}
								});
								disc.start();
							}
							}
						});

						list.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								if (establish_a_Connection())
								{
								Thread list = new Thread(new Runnable()
								{
									
									@Override
									public void run()
									{
										warning.setText("warning");
										askList();
									}
								});
								list.start();
							}
							}
						});
						
						getData.addActionListener(new ActionListener()
						{
							
							@Override
							public void actionPerformed(ActionEvent e)
							{
								if (establish_a_Connection())
								{
								Thread download = new Thread(new Runnable()
								{
									
									@Override
									public void run()
									{
										if(!(elemListServer.isSelectionEmpty()))
										{
											warning.setText("warning");
											getData(elemListServer.getSelectedValue());
										}else {
											warning.setText("YOU ARE NOT ON THE SERVER TABLE!!!! SERVER TABLE: LEFT SIDE");
											elemListClient.clearSelection();
											
										}
									}
								});
								download.start();
								}
							}
						});
					
					putData.addActionListener(new  ActionListener()
					{
						
						@Override
						public void actionPerformed(ActionEvent e)
						{
							if (establish_a_Connection())
							{
							Thread upload = new Thread(new Runnable()
							{
								
								@Override
								public void run()
								{
									if(!(elemListClient.isSelectionEmpty()))
									{
										warning.setText("warning");
										putData(elemListClient.getSelectedValue());
									}else {
										warning.setText("YOU ARE NOT ON THE CLIENT TABLE!!! CLIENT TABLE: RIGHT SIDE");
										elemListServer.clearSelection();
										
									}
								}
							});
							upload.start();
							}
						}
					});	
					
					deleteData.addActionListener(new ActionListener()
					{
						
						@Override
						public void actionPerformed(ActionEvent e)
						{
							
							if (establish_a_Connection())
							{
							Thread deleteData = new Thread(new Runnable()
							{
								
								@Override
								public void run()
								{
									if(!(elemListServer.isSelectionEmpty()))
									{
										warning.setText("warning");
										sendDelete(elemListServer.getSelectedValue());
									}else {
										warning.setText("YOU ARE NOT ON THE SERVER TABLE!!! SERVER TABLE: LEFT SIDE");
										elemListClient.clearSelection();
										warning.setText("warning");
									}
								}
							});
							deleteData.start();
							}
						}
					});
						
				
		}
			

	/**
	 * Die Methode establish_a_Connection() bereitet die KommunikationsSocket des
	 * Clients vor.
	 * 
	 * return true wenn die Kommunikationsaufbau erfolgreich ist . Andersfalls
	 *         false und wirft eine Exception Exception falls Hostname falsch
	 *         Exception falls die Kommunikationssockets des Servers nicht offen
	 *         sind.
	 */
	@Override
	public boolean establish_a_Connection()
	{

		try
		{
			socket = new Socket(host.getName(), 13);
			out = socket.getOutputStream();
			writer = new PrintWriter(out, true);
			in = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			return true;
		} catch (UnknownHostException e)
		{
			chatArea.append(" No Connection established\n");
			return false;
		} catch (IOException e)
		{
			chatArea.append(" No Connection established\n");
			return false;
		}
	}
	
	/**
	 * Schickt ein CON zum Server, um nach einer Verbindung zu fragen.
	 */
	@Override
	public void connection()
	{
		int count = 0;
		File client = new File("ClientDatas");
		String[] filesC = client.list();
		chatArea.append("\nLIST OF ALL FILES OF CLIENT "+userName.getText()+" IN THE RIGHT TABLE");
		for (String f : filesC)
		{
			arrayElementClient.add("ClientDatas/"+f);
			modelClient.addElement(arrayElementClient.get(count));
			count++;
		}
				Instruction con = Instruction.CON;
				writer.println(con);
				writer.flush();
				chatArea.append("\n Request sent to Server : " + con + "\n");
				try
				{
					String line = null;
					while ((line = reader.readLine()) != null)
					{

						if (line.equals(Instruction.ACK.toString()))
						{
							chatArea.append(line+"\n");
							chatArea.append("connection succesfull!!!");
						}
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				
	}

	/**
	 * Die Methode ConfirmationMessageToServer() bestaetigt eine Nachricht mittels ACK.
	 * return true wenn die Bestaetigung erfolgreich ist .
	 */
		
	public void confirmationMessageToServer()
	{
		writer.println(Instruction.ACK);
		writer.flush();
	}
	
	/**
	 * Bei der Methode wir ein DAT gesendet bevor ein Packet(Byte Array) geschickt wird.
	 */
	public void sendDAT()
	{
		writer.println(Instruction.DAT);
	}
	
	/**
	 * Die Methode getData() fordet eine Datei zum Server mittels GET.
	 * Es wird durch ein Thread verwaltet damit die Aktion einwandfrei funktionniert. D.h alle Befehle werden in der run() Methode implementiert.
	 * @param data die Name der Datei, die gefragt wird.
	 */
	public synchronized void getData(String data)
	{
		Thread tG = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Instruction get = Instruction.GET;
				writer.println(get);
				writer.println(data);
				writer.flush();
				chatArea.append("\n Request sent to Server : " + get + "\n");

				try
				{
					String line;
					while ((line = reader.readLine()) != null)
					{

						if (line.equals(Instruction.ACK.toString()))
						{
							chatArea.append("GET confirmed : "+line+"\n");
							confirmationMessageToServer();
							while ((line = reader.readLine()) != null)
							{
								if (line.equals(Instruction.DAT.toString()))
								{
									chatArea.append(line+"\n");
									confirmationMessageToServer();
									chatArea.append("data is sent!!");
									String[] parts = data.split("/");
									String file = parts[1];
									if(!(modelClient.contains("ClientDatas/" + file)))
									{
										modelClient.addElement("ClientDatas/" + file);
										receiveData(file);
									}
								}
							}
						}
					}

				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		tG.start();

	}
	
	/**
	 * Die Methode putData() schlaegt eine Datei zum Server mittels GET vor.
	 * Es wird durch ein Thread verwaltet damit die Aktion einwandfrei funktionniert. D.h alle Befehle werden in der run() Methode implementiert.
	 * @param data die Name der Datei, die gefragt wird.
	 */
	public synchronized void putData(String data)
	{
		Thread tP = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				Instruction put = Instruction.PUT;
				writer.println(put);
				writer.println(data);
				writer.flush();
				chatArea.append("\n Request sent to Server : " + put + "\n");
				
				try
				{
					String line = null;
					while ((line = reader.readLine()) != null)
					{
						if (line.equals(Instruction.ACK.toString()))
						{
							chatArea.append("first confirmation: "+line+"\n");
							sendDAT();
							giveData(data);
							while ((line = reader.readLine()) != null)
							{
								if (line.equals(Instruction.ACK.toString()))
								{
									chatArea.append("\nsecond confirmation: "+line+"\n");
									String[] parts = data.split("/");
									String file = parts[1];
									modelServer.addElement("List/"+file);
									chatArea.append("Dat of PUT confirmed\n");
								}
							}
						}else if(line.equals(Instruction.DND.toString()))
						{
							chatArea.append(line+"\n");
							warning.setText("THE SERVER ALREADY HAVE THE FILE!!");
						}
					}
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		tP.start();
	}
	
	/**
	 * Die Methode sendet ein File mit der Name des Uebergabeparameters.
	 * 
	 * @param data
	 *            ist der Name der zusendete File.
	 */
	@Override
	public void giveData(String data)
	{
		try
		{
			File file = new File(data);
			FileInputStream fis = new FileInputStream(file);
			byte[] dat = new byte[4096];
			BufferedInputStream bis = new BufferedInputStream(fis);
			int fileRead = 0;
			while ((fileRead = bis.read(dat, 0, dat.length)) != -1)
			{
				out.write(dat, 0, dat.length);
				out.flush();
			}
			chatArea.append("Total of bytes writen : " + dat.length);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException ie)
		{
			ie.printStackTrace();
		}
	}
	
	/**
	 * Die Methode bekommt ein File mit der Name des Uebergabeparameters.
	 * 
	 * @param fileName
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
	public void receiveData(String fileName)
	{
		File myFile = new File("ClientDatas\\" + fileName);
		try
		{
			out = new FileOutputStream(myFile);
			byte dat[] = new byte[4096];
			int byteRead = 0;
			while ((byteRead = in.read(dat, 0, dat.length)) != -1)
			{
				out.write(dat, 0, dat.length);
				chatArea.append("\nData from Server good received!");

			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Die Methode askList() fordet eine Liste zum Server mittels LST.
	 * Es wird durch ein Thread verwaltet damit die Aktion einwandfrei funktionniert. D.h alle Befehle werden in der run() Methode implementiert.
	 */
	public synchronized void askList()
	{
		Thread tL = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Instruction lst = Instruction.LST;
				writer.println(lst);
				writer.flush();
				chatArea.append("\n Request sent to Server : " + lst + "\n");

				try
				{
					String line;
					while ((line = reader.readLine()) != null)
					{

						if (line.equals(Instruction.ACK.toString()))
						{
							chatArea.append("LST confirmed"+line+"\n");
							confirmationMessageToServer();
							while ((line = reader.readLine()) != null)
							{
								if (line.equals(Instruction.DAT.toString()))
								{
									chatArea.append(line+"\n");
									confirmationMessageToServer();
									chatArea.append("List sent!!\n");
									if (receiveList())
										chatArea.append("List received!");
								}
							}
						}
					}

				} catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		});
		tL.start();

	}

	/**
	 * 
	 * @return true false die Aktion erfolgreich wird. Andersfalls false.
	 */
	public boolean receiveList()
	{
				try
				{
					String line = null;
					int i = 0;
					chatArea.append("\nLIST OF SERVER'S FILES IS SHOWN IN THE LEFT TABLE...\n");
					while ((line = reader.readLine()) != null)
					{
						arrayElementServer.add(line);
						if (!(modelServer.contains(arrayElementServer.get(i))))
							modelServer.addElement(arrayElementServer.get(i));
						i++;
					}

					return true;
				} catch (IOException e)
				{
					e.printStackTrace();
					return false;
				}

		
	}

	/**
	 * Die Methode sendDelete() fordet eine Datei zum Server mittels DEL.
	 * Es wird durch ein Thread verwaltet damit die Aktion einwandfrei funktionniert. D.h alle Befehle werden in der run() Methode implementiert.
	 * @param delDat die Name der Datei, die gefragt wird.
	 */
	public synchronized void sendDelete(String delDat)
	{
		Thread tD = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				Instruction del = Instruction.DEL;
				writer.println(del);
				writer.println(delDat);
				writer.flush();
				chatArea.append("\n Request sent to Server : " + del + "\n");
				try
				{
					String line = null;
					while ((line = reader.readLine()) != null)
					{

						if (line.equals(Instruction.ACK.toString()))
						{
							chatArea.append(line+"\n");
							modelServer.removeElement(delDat);
							chatArea.append("succesfully deleted!!\n");
						}else if(line.equals(Instruction.DND.toString()))
						{
							chatArea.append(line+"\n");
							warning.setText("MAY BE THE FILE HAVE BEEN DELETED FROM ANOTHER CLIENT");
						}
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				
			}
		});
		tD.start();

	}

	/**
	 * Baut die Kommunikationsverbindung mit dem Server ab beim Schicken von DSC.
	 * Wenn das Schließen nicht Erfolgreich wird, wird eine Exception geworfen.
	 * 
	 */
	@Override
	public boolean disconnection()
	{
		Instruction dsc = Instruction.DSC;
		writer.println(dsc);
		writer.flush();
		chatArea.append("\n Request sent to Server : " + dsc + "\n");
		try
		{
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				chatArea.append("warte...\n");
				if (line.equals(Instruction.DSC.toString()))
				{
					chatArea.append("From Server : " + line + "\n");
					writer.close();
					reader.close();
					socket.close();
				}
			}
		} catch (IOException e)
		{
			chatArea.append("Client disconnected!!");
		}

		return true;
	}


	public static void main(String[] args)
	{
		Runnable eventClient = new Runnable()
		{
			public void run()
			{
				LaunchClient Gc = new LaunchClient();
				Gc.ButtonsInit();
			}
		};
		SwingUtilities.invokeLater(eventClient);
	}
}

