package cs.threephase;

import java.io.*;
import java.util.Random;

public class Tools {
	private static void read(int[] arr, DataInput in) throws IOException {
		for (int i=0, len=arr.length; i<len; i++) {
			arr[i] = in.readInt();
		}
	}

	private static void write(int[] arr, DataOutput out) throws IOException {
		for (int i=0, len=arr.length; i<len; i++) {
			out.writeInt(arr[i]);
		}
	}

	private static void read(int[][] arr, DataInput in) throws IOException {
		for (int i=0, leng=arr.length; i<leng; i++) {
			for (int j=0, len=arr[i].length; j<len; j++) {
				arr[i][j] = in.readInt();
			}
		}
	}

	private static void write(int[][] arr, DataOutput out) throws IOException {
		for (int i=0, leng=arr.length; i<leng; i++) {
			for (int j=0, len=arr[i].length; j<len; j++) {
				out.writeInt(arr[i][j]);
			}
		}
	}

	static Random r = new Random();

	public static String randomCube() {
		return randomCube(r);
	}

	public static String randomCube(Random r) {
		FullCube c = new FullCube(r);
		byte[] f = new byte[96];
		c.toFacelet(f);
		StringBuffer sb = new StringBuffer();
		for (byte i: f) {
			sb.append("URFDLB".charAt(i));
		}
		return sb.toString();
	}

	public synchronized static void initFrom(DataInput in) throws IOException {
		if (Search.inited) {
			return;
		}

		Center1.initSym();
		Center1.initSym2Raw();
		read(Center1.ctsmv, in);
		Center1.createPrun();

		Center2.init();

		Center3.init();

		Edge3.initMvrot();
		Edge3.initRaw2Sym();
		read(Edge3.eprun, in);
		Edge3.done = Edge3.N_EPRUN;

		Search.inited = true;
	}

	public synchronized static void saveTo(DataOutput out) throws IOException {
		if (!Search.inited) {
			Search.init();
		}
		write(Center1.ctsmv, out);
		write(Edge3.eprun, out);
	}
}
