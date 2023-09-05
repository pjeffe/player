package com.mixzing.decoder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mixzing.data.CoreMetaData;
import com.mixzing.log.Logger;
import com.mixzing.tags.AudioTagger;


public class MixZingFingerPrinter {
	protected static final Logger log = Logger.getRootLogger();
	private static final String[] supportedTypes = new String[] {
		"mp3",
		"ogg",
		"oga",
		"m4a",
		"m4b",
		"mp4"
	};
	private static final HashSet<String> supported = new HashSet<String>(Arrays.asList(supportedTypes));

	/*
	 * 
	 * Use this interface if you want this class to obtain the tags.
	 * 
	 */
	public static TagQueryResponse generateAndGetResult(String inputfile, long skipMs, long durMs) {
		return generateAndGetResult(inputfile, skipMs, durMs, AudioTagger.readTags(inputfile));
	}

	/*
	 * 
	 * If the caller already has the tags for the inputfile.
	 * 
	 */
	public static TagQueryResponse generateAndGetResult(String inputfile, long skipMs, long durMs, CoreMetaData inputTags) {

		String sigobject = generateFingerPostData(inputfile, skipMs, durMs, inputTags);
		if (sigobject != null) {
			try {
				URL url = new URL("http://ec2-184-73-82-230.compute-1.amazonaws.com/perl/003/finger.pl");
				String data = MixZingFingerServerComm.sendMessage(url, sigobject.getBytes());
				return parseData(data);
			}
			catch (MalformedURLException e) {
				log.error("Exception in generate", e);
			}
		}
		return null;
	}

	protected static TagQueryResponse parseData(String data) {
		TagQueryResponse resp = null;
		if (data != null) {
			try {
				JSONObject jobj = new JSONObject(data);
				resp = new TagQueryResponse(jobj);
			}
			catch (JSONException e) {
				log.error("exception in tagqueryresp", e);
			}
		}
		return resp;
	}

	public static long[] generateFingerValues(String inputfile, long skipMs, long durMs, int[] data) {
		int numsamp = (int)(durMs * 2560L / 30000);
		long[] sampleArray = new long[numsamp + 10];

		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("MixZingFingerPrinter.generateFinger: file = " + inputfile);
		}

		int samples = MusicDecoder.getInstance().generateFingerPrintForFile(inputfile, (int)skipMs, (int)durMs, sampleArray, data);

		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("MixZingFingerPrinter.generateFinger: samples = " + samples);
		}
		
		if (samples > 0) {
			long[] smp = new long[samples];
			for (int i = 0; i < samples; i++) {
				smp[i] = sampleArray[i];
			}
			return smp;
		} 

		return null;
	}

	
	/*
	 * This generates a string that should get posted to the server
	 */
	public static Signature generateFinger(String inputfile, long skipMs, long durMs, CoreMetaData inputTags) {
		int numsamp = (int)(durMs * 2560L / 30000);
		long[] sampleArray = new long[numsamp + 10];
		int[] data = new int[4];

		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("MixZingFingerPrinter.generateFinger: file = " + inputfile + ", meta = " + inputTags);
		}
		Signature sig = null;
		int samples = MusicDecoder.getInstance().generateFingerPrintForFile(inputfile, (int)skipMs, (int)durMs, sampleArray, data);

		if (Logger.IS_DEBUG_ENABLED) {
			log.debug("MixZingFingerPrinter.generateFinger: samples = " + samples);
		}
		if (samples > 0) {
			final StringBuilder str = new StringBuilder();
			for (int i = 0; i < samples; i++) {
				str.append(sampleArray[i]);
				str.append("|");
			}
			sig = new Signature(data[0], data[1], data[2], str.toString());
		}

		return sig;
	}

	public static String generateFingerPostData(String inputfile, long skipMs, long durMs, CoreMetaData inputTags) {
		String sigobject = null;
		final Signature sig = generateFinger(inputfile, skipMs, durMs, inputTags);
		if (sig != null) {
			try {
				sigobject = toJson(inputfile, sig, skipMs, durMs, skipMs, durMs, inputTags);
			}
			catch (JSONException e) {
			}
		}

		return sigobject;

	}

	protected static String toJson(String fileName, Signature sig, long skipMs, long durMs, long actualskipMs, long actualdurMs, CoreMetaData tags) throws JSONException {

		JSONStringer stringer = new JSONStringer();
		stringer.object();

		stringer.key("sigtype");
		stringer.value("fft20");

		stringer.key("filename");
		stringer.value(fileName);

		stringer.key("sig");
		stringer.value(sig.getSig());

		stringer.key("reqskip");
		stringer.value(skipMs);

		stringer.key("reqdur");
		stringer.value(durMs);

		stringer.key("actualskip");
		stringer.value(actualskipMs);

		stringer.key("actualdur");
		stringer.value(actualdurMs);

		stringer.key("len");
		stringer.value(sig.getLength());

		stringer.key("rate");
		stringer.value(sig.getRate());

		stringer.key("chans");
		stringer.value(sig.getChans());

		if (tags != null) {
			if (tags.getArtist() != null) {
				stringer.key("artist");
				stringer.value(tags.getArtist());
			}

			if (tags.getAlbum() != null) {
				stringer.key("album");
				stringer.value(tags.getAlbum());
			}

			if (tags.getTitle() != null) {
				stringer.key("title");
				stringer.value(tags.getTitle());
			}

			if (tags.getGenre() != null) {
				stringer.key("genre");
				stringer.value(tags.getGenre());
			}

			if (tags.getTrackNum() > 0) {
				stringer.key("track");
				stringer.value(tags.getTrackNum());
			}

			if (tags.getYear()> 0) {
				stringer.key("year");
				stringer.value(tags.getYear());
			}

		}

		stringer.endObject();

		return stringer.toString();
	}

	public static boolean isSupported(String path) {
		boolean ret = false;
		final int pos = path.lastIndexOf('.');
		if (pos >= 0) {
			final String ext = path.substring(pos + 1).toLowerCase(Locale.US);
			ret = supported.contains(ext);
		}
		return ret;
	}
}
