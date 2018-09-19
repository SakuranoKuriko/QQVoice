class SILK24  { 
	/* 
	 *                 | fs (Hz) | BR (kbps)
	 * ----------------+---------+---------
	 * Narrowband	   | 8000    | 6 -20
	 * Mediumband      | 12000   | 7 -25
	 * Wideband        | 16000   | 8 -30
	 * Super Wideband  | 24000   | 12 -40
	 *
	 * Table 1: fs specifies the audio sampling frequency in Hertz (Hz); BR
	 * specifies the adaptive bit rate range in kilobits per second (kbps).
	 * 
	 * Complexity can be scaled to optimize for CPU resources in real-time,
	 * mostly in trade-off to network bit rate. 0 is least CPU demanding and
	 * highest bit rate. 
	 */
	private static final int DEFAULT_COMPLEXITY = 0;
 
	void load() {
		System.loadLibrary("silk24_jni");
	}  
 
	public native int open(int compression);
	public native int decode(byte encoded[], short lin[], int size);
	public native int encode(short lin[], int offset, byte encoded[], int size);
	public native void close();
 
}