/* 2009036097 Kim dongwon */
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class run {

	static byte[] M;
	static long[] T = new long[64];
	static long[] X = new long[16];
	static long A = 0x67452301;
	static long B = 0xEFCDAB89;
	static long C = 0x98BADCFE;
	static long D = 0x10325476;

	static long temp_A;
	static long temp_B;
	static long temp_C;
	static long temp_D;

	public static long rotate_left(long x,long s){
		return ((x) << (s)) | ((x) >>> (32-s)) & 0xFFFFFFFFL;
	}

	public static long encode(long t){
		return ((t >> 24) & 0xff) | ((t >> 16) & 0xff) << 8 | ((t >> 8) & 0xff) << 16 | (t & 0xff) << 24;
	}

	public static long ff(long a,long b,long c,long d,long k,long s, long i){
		return (b + rotate_left(((a + ((b & c) | ((~b) & d)) + k + i) & 0xFFFFFFFFL),s)) & 0xFFFFFFFFL; 
	}

	public static long gg(long a,long b,long c,long d,long k,long s, long i){
		return (b + rotate_left(((a + ((b & d) | (c & (~d))) + k + i) & 0xFFFFFFFFL),s)) & 0xFFFFFFFFL; 
	}

	public static long hh(long a,long b,long c,long d,long k,long s, long i){
		return (b + rotate_left(((a + (b ^ c ^ d) + k + i) & 0xFFFFFFFFL) , s)) & 0xFFFFFFFFL; 
	}

	public static long ii(long a,long b,long c,long d,long k,long s, long i){
		return (b + rotate_left(((a + (c ^ (b | (~d))) + k + i) & 0xFFFFFFFFL), s)) & 0xFFFFFFFFL; 
	}

	public static byte[] longToByteArray(long value) {
		return new byte[] {
				(byte) (value),
				(byte) (value >> 8),
				(byte) (value >> 16),
				(byte) (value >> 24),
				(byte) (value >> 32),
				(byte) (value >> 40),
				(byte) (value >> 48),
				(byte) (value >> 56)
		};
	}

	public static void table_T(){
		for(int i = 0;i < 64;i++){
			T[i] = (long) (Math.floor(Math.abs(Math.sin(i+1)) * (long)Math.pow(2,32)));
		}
	}

	public static void main(String[] args) throws IOException{

		String inputString = args[0];
		long length = 0;
		int padding_length = 0;
		int i,j;
		byte[] pad;
		int g = 0,div16,k=0;

		File file = new File(inputString);
		length = file.length();

		if(length % 64  < 56){
			padding_length = (int) (56 - length % 64);
		}
		else if(length % 64 > 56){
			padding_length = (int) (64 - (length % 64 - 56));
		}
		else if(length % 64 == 56){
			padding_length = 64;
		}
		try{
			FileInputStream fis = new FileInputStream(inputString);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);

			M = new byte[(int) (length + padding_length + 8)];

			for(i = 0 ; i < length + padding_length; i++){
				if(i < length){
					M[i] = (byte)dis.read();
				}
				else if(i == length){
					M[i] = (byte)128;
				}
				else{
					M[i] = 0;
				}
			}

			pad = longToByteArray(length * 8);

			for(i = 0;i < 8;i++){
				M[(int) (i + length + padding_length)] = (byte)pad[i];
			}

			table_T();
			for(i=0;i<(length+padding_length + 8)/64;i++)
			{
				for(j=0,k=0;j<16;j++,k+=4){
					X[j] = ((int)M[i*64 + k] & 0xFF) | ((int)M[i*64+k+1] & 0xFF) << 8 | ((int)M[i*64+k+2] & 0xFF) << 16 | ((int)M[i*64+k+3] & 0xFF) << 24; 
				}
				temp_A = A;
				temp_B = B;
				temp_C = C;
				temp_D = D;	

				for (j = 0; j < 64; j++)
				{
					div16 = j >>> 4;

					switch (div16)
					{
					case 0:
						g = j;
						if(j % 4 == 0)
						{
							A = ff(A,B,C,D,X[g],7,T[j]);
						}
						else if(j % 4 == 1)
						{
							D = ff(D,A,B,C,X[g],12,T[j]);
						}
						else if(j % 4 == 2)
						{
							C = ff(C,D,A,B,X[g],17,T[j]);
						}
						else if(j % 4 == 3)
						{
							B = ff(B,C,D,A,X[g],22,T[j]);
						}
						break;
					case 1:
						g = (j * 5 + 1) % 16;
						if(j % 4 == 0)
						{
							A = gg(A,B,C,D,X[g],5,T[j]);
						}
						else if(j % 4 == 1)
						{
							D = gg(D,A,B,C,X[g],9,T[j]);
						}
						else if(j % 4 == 2)
						{
							C = gg(C,D,A,B,X[g],14,T[j]);
						}
						else if(j % 4 == 3)
						{
							B = gg(B,C,D,A,X[g],20,T[j]);
						}
						break;
					case 2:
						g = (j * 3 + 5) % 16;
						if(j % 4 == 0)
						{
							A = hh(A,B,C,D,X[g],4,T[j]);
						}
						else if(j % 4 == 1)
						{
							D = hh(D,A,B,C,X[g],11,T[j]);
						}
						else if(j % 4 == 2)
						{
							C = hh(C,D,A,B,X[g],16,T[j]);
						}
						else if(j % 4 == 3)
						{
							B = hh(B,C,D,A,X[g],23,T[j]);
						}
						break;

					case 3:
						g = (j * 7) % 16;
						if(j % 4 == 0)
						{
							A = ii(A,B,C,D,X[g],6,T[j]);
						}
						else if(j % 4 == 1)
						{
							D = ii(D,A,B,C,X[g],10,T[j]);
						}
						else if(j % 4 == 2)
						{
							C = ii(C,D,A,B,X[g],15,T[j]);
						}
						else if(j % 4 == 3)
						{
							B = ii(B,C,D,A,X[g],21,T[j]);
						}
						break;
					}
				}
				A = (A + temp_A) & 0xFFFFFFFFL;
				B = (B + temp_B) & 0xFFFFFFFFL;
				C = (C + temp_C) & 0xFFFFFFFFL;
				D = (D + temp_D) & 0xFFFFFFFFL;
			}

			A = encode(A);
			B = encode(B);
			C = encode(C);
			D = encode(D);

			System.out.format("%x%x%x%x\n",A,B,C,D);
			fis.close();
			bis.close();
			dis.close();
		}
		catch(IOException e){
			System.out.println("There is no such an file.");
		}
	}
}