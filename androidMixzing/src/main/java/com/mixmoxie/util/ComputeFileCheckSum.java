package com.mixmoxie.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class ComputeFileCheckSum {

	// prevent instantiation
	private ComputeFileCheckSum () {
	}

	public static String computeCheckSum(String file) {
		try {
			FileInputStream f = new FileInputStream(file);
			CRC32 crc = new CRC32();
			f.skip(1024);
			byte[] b = new byte[4096];

			f.read(b);
			crc.update(b);
			long c1 = crc.getValue();

			f.read(b);
			crc.update(b);
			long c2 = crc.getValue();

			f.read(b);
			crc.update(b);
			long c3 = crc.getValue();

			f.close();

			return c1 + "" + c2 + "" + c3;

		} catch (IOException e) {
		}
		return null;
	}

	public static long computeOverFullFile(String f)   {
		long sum = -1;
		long start = System.currentTimeMillis();
		try {
			FileInputStream file = new FileInputStream(f);
			//CheckedInputStream check =  new CheckedInputStream(file, new CRC32());
			
			BufferedInputStream in =  new BufferedInputStream(file, 512*1024);
			byte[] buffer = new byte[512*1024];
			
			while (in.read(buffer) != -1) {
				// Read file in completely
			}
			
			in.close();
			//sum = check.getChecksum().getValue();
		} catch (IOException e) {

		}
		long elapsed = System.currentTimeMillis() - start;
//		Log.i("MIXZING", elapsed + " - Time to generate checksum : " + f + " sum=" + sum);
		return sum;
	}

}
