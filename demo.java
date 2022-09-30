/*import java.net.*;
import java.nio.*;
import java.io.*;
import java.util.*;
import java.nio.channels.*;
class demo
{
	public static  void main(String args[])
	{
		try
		{
			RandomAccessFile aFile = new RandomAccessFile("/Users/suriya-14937/Downloads/Java-Training.pdf" , "r");
			FileChannel inChannel = aFile.getChannel();


			long fileSize = inChannel.size();

			//Create buffer of the file size
			ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
			inChannel.read(buffer);
			buffer.flip();

			// Verify the file content
			for (int i = 0; i < fileSize; i++) 
			{
				System.out.print((char) buffer.get());
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}*/


import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class demo
{
    public static void main(String[] args) throws IOException
    {
        //RandomAccessFile aFile = new RandomAccessFile("", "r");
			RandomAccessFile aFile = new RandomAccessFile("/Users/suriya-14937/Downloads/Test.txt" , "r");

        FileChannel inChannel = aFile.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while(inChannel.read(buffer) > 0)
        {
            buffer.flip();
            for (int i = 0; i < buffer.limit(); i++)
            {
                System.out.print((char) buffer.get());
            }
            buffer.clear(); // do something with the data and clear/compact it.
        }

        inChannel.close();
        aFile.close();
    }
}
