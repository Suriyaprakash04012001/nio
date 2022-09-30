import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.file.*;


class Client
{
	static void Client1() throws Exception
	{
		InetSocketAddress host=new InetSocketAddress("localhost",8000);
		SocketChannel client=SocketChannel.open(host);
		System.out.println(" Press--->1 Upload A File into Server");
		System.out.println(" Press--->2 Download A File from Server");
		System.out.println(" Press--->3 Exit");
		System.out.println("Enter the Choice");
		Scanner sc=new Scanner(System.in);
		int n=sc.nextInt();
		String st=Integer.toString(n);
		byte[]msg=new String(st).getBytes();
		ByteBuffer buffer=ByteBuffer.wrap(msg);
		client.write(buffer);
		String temp;			

		switch(n)
		{
			case 1: 
				System.out.println("Type the File name with Path");
				Scanner sca=new Scanner(System.in);
				temp=sca.nextLine();
				sendFile(client,temp);
				break;
			case 2:
				System.out.println("------List the ServerFiles-----");
				String[] filename;
				File f=new File("/Users/suriya-14937/Suriya/nio/ServerFiles");
				filename=f.list();
				for(String Filename:filename)
				{
					System.out.println(" "+Filename+" ");
				}
				System.out.println("Enter the Filename ");
				Scanner scan=new Scanner(System.in);
				String s=scan.nextLine();
				receiveFile(client,s);
				break;
			case 3:
				System.out.println("Close the Connection");
				client.close();
				break;


		}
}
static void Client2() throws Exception
{
	InetSocketAddress host=new InetSocketAddress("localhost",8001);
	SocketChannel client=SocketChannel.open(host);
	ByteBuffer buff=ByteBuffer.allocate(256);
	Path path=Paths.get("/Users/suriya-14937/Suriya/nio/MetaOperation.txt");
	FileChannel fc=FileChannel.open(path,EnumSet.of(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE));
	while(client.read(buff)>0)
	{
		buff.flip();
		String out=new String(buff.array()).trim();
		fc.write(buff);
		System.out.println(out);
		buff.clear();
	}
	client.close();
	fc.close();


}

public static void sendFile(SocketChannel client, String temp) throws Exception
{
	ByteBuffer buff=ByteBuffer.allocate(256);
	Path path=Paths.get(temp);
	Path filename=path.getFileName();

	String t=filename.toString();
	byte[] msg=new String(t).getBytes();
	ByteBuffer buffer=ByteBuffer.wrap(msg);
	client.write(buffer);
	buffer.clear();

	FileChannel fc=FileChannel.open(path);

	while(fc.read(buff)>0)
	{
		buff.flip();
		client.write(buff);
		buff.clear();

	}
	fc.close();
	client.close();


}
public static  void receiveFile(SocketChannel client,String s) throws Exception
{
	ByteBuffer buff=ByteBuffer.allocate(256);
	byte[] msg=new String(s).getBytes();
	ByteBuffer buffer=ByteBuffer.wrap(msg);
	client.write(buffer);
	buffer.clear();
	System.out.println("receiveFile..buff.clear()");
	Path path=Paths.get("/Users/suriya-14937/Suriya/nio/ClientFiles/"+s);
	FileChannel fc=FileChannel.open(path,EnumSet.of(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE));


	while(client.read(buff)>0)
	{
		buff.flip();

		String out=new String(buff.array()).trim();
		fc.write(buff);
		buff.clear();
	}
	fc.close();
	client.close();
}


public static void main(String args[]) throws Exception
{
	int n=Integer.parseInt(args[0]);
	if(n==8000)
	{
		Client1();

	}
	if(n==8001)
	{
		Client2();
	}
}

}
