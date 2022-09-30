import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

class Server
{
	public static void main(String args[]) throws Exception
	{
		Hashtable<String,Integer> hash=new Hashtable<>();
		Hashtable<String,Integer>hashport=new Hashtable<>();
		File fi=new File("/Users/suriya-14937/Suriya/nio/ServerFiles"); //add a file name in hashtable
		String[] list=fi.list();
		FileWriter fw=new FileWriter("/Users/suriya-14937/Suriya/nio/portdetails.txt",true);
		Scanner sc2=new Scanner(new File("/Users/suriya-14937/Suriya/nio/portdetails.txt"));
		while (sc2.hasNextLine())
		{
			Scanner s2 = new Scanner(sc2.nextLine());
			int i=0;
			String filename="";
			while (s2.hasNext())
			{
				String s = s2.next();
				if(i==0)
				{
					filename=s;
					hash.put(s,0);
				}
				if(i==2)
				{
					int p=Integer.parseInt(s);
					hashport.put(filename,p);
				}
				i++;
			}


		}
		Thread t1=new Server1(hash,hashport,fw);
		t1.start();
		Thread t2=new Server2(hash,hashport);
		t2.start();

	}
}
class Server1 extends Thread
{
	static Hashtable<String,Integer>hash;
	static Hashtable<String,Integer>hashport;
	static FileWriter fw;
	Server1(Hashtable<String,Integer>hash,Hashtable<String,Integer>hashport,FileWriter fw)
	{
		this.fw=fw;
		this.hash=hash;
		this.hashport=hashport;
	}
	public void run()
	{
		try
		{
			Selector selector=Selector.open();
			System.out.println("Opening for COnenction(Selector) :"+ selector.isOpen());

			ServerSocketChannel ss=ServerSocketChannel.open();
			InetSocketAddress host=new InetSocketAddress("localhost",8000);
			ss.bind(host);

			ss.configureBlocking(false);
			int ops=ss.validOps();
			System.out.println("ops: " +ops);
			SelectionKey selectky=ss.register(selector,ops,null);

			for(;;)
			{
				System.out.println("waiting for select operation---8000 ");
				int noOfSelectedKeys=selector.select();
				System.out.println("no of slected keys : "+noOfSelectedKeys);

				Set selectedKeys=selector.selectedKeys();
				Iterator itr=selectedKeys.iterator();

				while(itr.hasNext())
				{
					SelectionKey ky=(SelectionKey)itr.next();

					if(ky.isAcceptable())
					{
						SocketChannel client =ss.accept();
						//	int port=client.getPort();
						client.configureBlocking(false);



						ByteBuffer buffer=ByteBuffer.allocate(256);
						client.register(selector,SelectionKey.OP_READ);
						System.out.println("New connection is accepted :"+ client);


					}
					if(ky.isReadable())
					{

						SocketChannel client=(SocketChannel)ky.channel();
						System.out.println("Readble");
						ByteBuffer buff=ByteBuffer.allocate(256);
						client.read(buff);
						buff.clear();
						//buff.flip();
						System.out.println(buff.position());
						String temp=new String(buff.array()).trim();
						int n=Integer.parseInt(temp);
						System.out.println("temp---n "+n);
						switch(n)
						{
							case 1:
								System.out.println("Upload a file to server");
								receiveFile(ky,client);
								break;
							case 2:
								System.out.println("Download a file from server");
								sendFile(ky,client);
								break;
							case 3:
								System.out.println("Closing Client "+ client + "Connection");
								//flag=false;
								client.close();
								break;
						}


					}

					itr.remove();
				}



			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


	}
	public static void receiveFile(SelectionKey ky, SocketChannel client) throws Exception
	{
		fw=new FileWriter("portdetails.txt",true);
		ByteBuffer buff=ByteBuffer.allocate(256);
		while(client.read(buff)==0)
		{
			Thread.sleep(1);
		}
		System.out.println("readable");
		client.read(buff);
		String temp=new String(buff.array()).trim();
		System.out.println("temp--"+temp);
		buff.clear();
		hash.put(temp,0); //put Filename and Download initializes to zero;
		Path path=Paths.get("/Users/suriya-14937/Suriya/nio/ServerFiles/"+temp);
		FileChannel fc=FileChannel.open(path,EnumSet.of(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE));

		while(client.read(buff)>0)
		{
			buff.flip();

			String out=new String(buff.array()).trim();
			fc.write(buff);
			buff.clear();
		}
		Socket socket = client.socket();
		int port= socket.getPort();
		System.out.println("port "+port);
		if(!hashport.containsKey(temp))
		{
			hashport.put(temp,port);//alread exitsing file uploaded it doesnt the change the client port and also not put in hashport
			fw.write(temp +" - "+String.valueOf(port)+"\n");
		}
		if(!hash.containsKey(temp))
		{
			hash.put(temp,0);//put Filename and Download initializes to zero;
		}
		fc.close();
		fw.close();
		client.close();
	}
	public static void sendFile(SelectionKey ky,SocketChannel client) throws Exception
	{
		System.out.println("server SendFile method");
		ByteBuffer buff=ByteBuffer.allocate(256);
		while(client.read(buff)==0)
		{
			Thread.sleep(1);
		}
		client.read(buff);
		String temp=new String(buff.array()).trim();
		buff.clear();
		System.out.println("server temp--"+temp);
		Path path=Paths.get("/Users/suriya-14937/Suriya/nio/ServerFiles/"+temp);

		FileChannel fc=FileChannel.open(path);
		//	FileChannel fc=FileChannel.open(path,EnumSet.of(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE));

		while(fc.read(buff)>0)
		{
			buff.flip();
			client.write(buff);
			buff.clear();

		}
		if(hash.containsKey(temp))
		{
			Integer a=hash.get(temp);
			hash.put(temp,a+1);

		}
		System.out.println(hash);
		fc.close();
		client.close();

	}


}

class Server2 extends Thread
{
	Hashtable<String,Integer>hash=new Hashtable<>();
	Hashtable<String,Integer>hashport=new Hashtable<>();
	Server2(Hashtable<String,Integer>hash,Hashtable<String,Integer>hashport)
	{
		this.hash=hash;
		this.hashport=hashport;
	}
	public void run()
	{
		try
		{
			Selector selector=Selector.open();
			System.out.println("Opening for Conenction(Selector) :"+ selector.isOpen());

			ServerSocketChannel ss=ServerSocketChannel.open();
			InetSocketAddress host=new InetSocketAddress("localhost",8001);
			ss.bind(host);

			ss.configureBlocking(false);
			int ops=ss.validOps();
			System.out.println("ops: " +ops);
			SelectionKey selectky=ss.register(selector,ops,null);

			for(;;)
			{
				System.out.println("waiting for select operation---8001");
				int noOfSelectedKeys=selector.select();
				System.out.println("no of slected keys : "+noOfSelectedKeys);

				Set selectedKeys=selector.selectedKeys();
				Iterator itr=selectedKeys.iterator();

				while(itr.hasNext())
				{
					SelectionKey ky=(SelectionKey)itr.next();

					if(ky.isAcceptable())
					{
						SocketChannel client =ss.accept();
						client.configureBlocking(false);

						client.register(selector,SelectionKey.OP_WRITE);
						System.out.println("New connection is accepted :"+ client);


					}
					if(ky.isWritable())
					{
						System.out.println("read");
						SocketChannel client=(SocketChannel)ky.channel();
						displayMeta(ky,client);
					}

					itr.remove();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void displayMeta(SelectionKey ky,SocketChannel client) throws Exception
	{
		try
		{
			Socket socket=client.socket();
			InetAddress addr = socket.getInetAddress();
			Enumeration<String> e = hash.keys();
			while(e.hasMoreElements())
			{
				String temp=e.nextElement();
				String s="Filename : "+ temp +" \t "+" no of Downloads : "+hash.get(temp)+" \t "+"Client address :"+ addr +" Client port : "+ hashport.get(temp)+"\n";
				//	System.out.println(s);
				byte[] msg=new String(s).getBytes();
				ByteBuffer buffer=ByteBuffer.wrap(msg);
				String st=new String(buffer.array()).trim();
				System.out.println(st);
				client.write(buffer);
				buffer.clear();


			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		client.close();

	}

}
