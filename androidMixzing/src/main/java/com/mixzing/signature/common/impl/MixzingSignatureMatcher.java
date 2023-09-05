package com.mixzing.signature.common.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.lang.StringBuilder;

import com.mixzing.signature.common.MixzingPeak;

public class MixzingSignatureMatcher {

	
	private static final double NEAR_THRESHOLD = 44d * 2/4; // 0.25 ms for every 100ms in superwin
	
	private static final double EQUIVALENCE_THRESHOLD = NEAR_THRESHOLD / 5;
    private static final int CORRECTION_OFFSET_SLACK = 10;
    private static final boolean USE_MAX_SPAN_FOR_DIVISOR  = true;
    
	//private List<MixzingPeak> myPeaks;

	private List<Double> offsets;
	private List<DiffSpan> diffs = new ArrayList<DiffSpan>();
	private List<Double> energy;

	
	public MixzingSignatureMatcher(List<Double> off, List<Double> ene) {
		this.offsets = off;
		this.energy = ene;
	}
	

	public List<Double> getOffsets() {
		return offsets;
	}

	public List<Double> getEnergy() {
		return energy;
	}
	
	public MixzingSignatureMatcher(List<MixzingPeak> peaks) {
		
		//myPeaks = peaks;
		
		ArrayList<Double> l = new ArrayList<Double>();
		ArrayList<Double> e = new ArrayList<Double>();
		
		for( MixzingPeak p : peaks) {
			l.add((double) p.getOffset());
			e.add(p.getEnergy());
		}
		this.offsets = l;
		this.energy = e;
	}
	
	/*
	 * Convenience constructor to pluck signatures from the client or server log files
	 */
	public MixzingSignatureMatcher(String sigOffsets) {
		ArrayList<Double> l = new ArrayList<Double>();
		StringTokenizer tok = new StringTokenizer(sigOffsets,"|");
		while(tok.hasMoreTokens()) {
			String s = tok.nextToken();
			l.add(Double.valueOf(s));
		}
		offsets = l;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		int num = offsets.size();
		for (int i = 0; i < num; ++i) {
			sb.append(String.format("%s %d : %.1e",
				i == 0 ? "" : ",", Math.round(offsets.get(i)), energy.get(i)));
		}
		return sb.toString() + " ]";
	}
	
    protected double getNearThreshold() {
    	return NEAR_THRESHOLD;
    }
    
    protected double getEquivalenceThreshold() {
    	return EQUIVALENCE_THRESHOLD;
    }
    
    protected boolean isUseMaxSpan() {
    	return USE_MAX_SPAN_FOR_DIVISOR;
    }

    public class SigMatch  implements Comparable<SigMatch>{
		
		private static final double UNIVERSE_SIZE = 1e7;
		private ArrayList<MatchIndex> matchIndex;
		private double offset;
		private int possible;
		private int matches;
		private double error;
		private double correction;
		private double totalCorrection;
		private List<Double> cref;
		private int numPeaks;
		private int numPeaksOther;
		private double maxSpan;
		private int maxDistance = 0;
		private int totalDistance = 0;
		private int exactEnergyMatches = 0;
		private int consecMatches = 0;
		private int totConsecMatches = 0;
		private int maxConsecMatch = 0;
		private double avgDistance = 0;

		
		public SigMatch(double off, int mat, int pos, double er, double cor, List<Double> cr, int np, int npo, double maxSpan, ArrayList<MatchIndex> matchIndex) {
			this.offset = off;
			this.possible = pos;
			this.matches = mat;
			this.error = er;
			this.correction = cor;
			this.cref = cr;
			this.numPeaks = np;
			this.numPeaksOther = npo;
			this.maxSpan = maxSpan;
			this.matchIndex = matchIndex;
			this.computeUncertainity();
		}
		
		public String toString() {
			String cr = "\tCref : \n";
			for(double d : cref) {
				cr += "\t\t" + d + "\n";
			}
			String mi = "\t MatchIndexes : \n";
			for(MatchIndex m : this.matchIndex) {
				mi += " [" + m.mine + "," + m.other + "]";
			}
			mi += "\n";
			return "\tOffset: " + offset + "\n" +
			       "\tPossible: " + possible + "\n"+
				    "\tMatches: " + matches + "\n" +
				    "\tError: "   + error + "\n" +
				    "\tCorrection: " + correction + "\n" +
				    cr +
				    mi +
				    "\tnumPeaks: " + numPeaks + " " + numPeaksOther + "\n" +
				    "\tmaxspan: " + maxSpan + "\n" +
				    String.format("\tuncertainity: %.1e\n", uncertainity); 
		}
		
		public String toStringShort() {
			StringBuilder sb = new StringBuilder();
			String delim = "";
			for (double cor : cref) {
				sb.append(String.format("%s%.0f", delim, cor));
				delim = ", ";
			}
			return String.format("offset = %.0f, unc = %.1e, possible = %d, matches = %d, correction = %.0f, maxSpan = %.0f, cref = [%s]",
				offset, uncertainity, possible, matches, correction, maxSpan, sb.toString());
		}

		public double getCorrection() {
			return correction;
		}
		
		private double uncertainity = 1;

		private void computeUncertainity() {
			double overallOdds = 1;
			int np = this.numPeaks;
			int npo = this.numPeaksOther;
			
			//Collections.sort(this.cref);
			ArrayList<Double> copy = new ArrayList<Double>();
			for(double d: cref) {
				double cor = Math.abs(d);
				copy.add(cor);
				totalCorrection += cor;
			}
			Collections.sort(copy);
			
	        //throw away the smallest difference because it was the anchor, XXX: is this before or after sort ?
			if (copy.size() != 0) {
				copy.remove(0);
			}
			
			np--;
			npo--;
			
			double divisor = 1;
            
            if(isUseMaxSpan()) {
                divisor = this.maxSpan;
            } else {
                // XXX: try dividing by 30 seconds
                divisor = 44100 * 30;
            }
			
			for(double cor : copy) {
				double matchOddsOnePeak = ((Math.abs(cor)) + 1) / divisor;
				double matchOddsAnyPeak = matchOddsOnePeak * np * npo;
				overallOdds *= matchOddsAnyPeak;
			}
			uncertainity = overallOdds * UNIVERSE_SIZE;			
		}
		
		public synchronized double getUncertainity() {
			return uncertainity;
		}

		public double getOffset() {
			return offset;
		}

		public int getPossible() {
			return possible;
		}

		public int getMatches() {
			return matches;
		}

		public double getError() {
			return error;
		}

		public double getTotalCorrection() {
			return totalCorrection;
		}

		public List<Double> getCref() {
			return cref;
		}

		public int getNumPeaks() {
			return numPeaks;
		}

		public int getNumPeaksOther() {
			return numPeaksOther;
		}

		public double getMaxSpan() {
			return maxSpan;
		}

		public int getMaxDistance() {
			return maxDistance;
		}

		public void setMaxDistance(int maxDistance) {
			this.maxDistance = maxDistance;
		}

		public int getTotalDistance() {
			return totalDistance;
		}

		public void setTotalDistance(int totalDistance) {
			this.totalDistance = totalDistance;
		}

		public int getExactEnergyMatches() {
			return exactEnergyMatches;
		}

		public void setExactEnergyMatches(int exactEnergyMatches) {
			this.exactEnergyMatches = exactEnergyMatches;
		}

		public int getConsecMatches() {
			return consecMatches;
		}

		public void setConsecMatches(int consecMatches) {
			this.consecMatches = consecMatches;
		}

		public int getTotConsecMatches() {
			return totConsecMatches;
		}

		public void setTotConsecMatches(int totConsecMatches) {
			this.totConsecMatches = totConsecMatches;
		}

		public int getMaxConsecMatch() {
			return maxConsecMatch;
		}

		public void setMaxConsecMatch(int maxConsecMatch) {
			this.maxConsecMatch = maxConsecMatch;
		}

		public double getAvgDistance() {
			return avgDistance;
		}

		public void setAvgDistance(double avgDistance) {
			this.avgDistance = avgDistance;
		}

		public int compareTo(SigMatch o) {
			if(this.uncertainity > o.getUncertainity()) {
				return 1;
			}
			if(this.uncertainity == o.getUncertainity())
				return 0;
			
			return -1;
		}
		public ArrayList<MatchIndex> getMatchIndex() {
			return matchIndex;
		}
		
	}



	public class DiffSpan implements Comparable<DiffSpan>{
		private double span;
		private double leftOffset;
		private double energy;
		
		public DiffSpan(double s, double o) {
			this.span = s;
			this.leftOffset = o;
		}
		
		public double getSpan() {
			return span;
		}

		public double getLeftOffset() {
			return leftOffset;
		}

		public int compareTo(DiffSpan o) {
			
			if( this.getSpan() > o.getSpan()) {
				return -1;
			}
			
			if( o.getSpan() == this.getSpan()) {
				return 0;
			}			
			
			return 1;
		}
	}
	

	HashMap<String, SigMatch> checkedOffsets = new HashMap<String, SigMatch>();
	
	public SigMatch match(MixzingSignatureMatcher other) {
		/*
		if(myPeaks != null) {
			editDistance(other.myPeaks);
		}
		*/
		
		checkedOffsets = new HashMap<String, SigMatch>();
		List<DiffSpan> myDiffs = this.diffs();
		List<DiffSpan> otherDiffs = other.diffs();
		int myDiffSize = myDiffs.size();
		int maxDiffsTried = myDiffSize;
		int otherDiffSize = otherDiffs.size();
		MYSPAN:
			//for(int myInd = 0; myInd < maxDiffsTried; myInd++) {
			//DiffSpan mySpan = myDiffs.get(myInd);
			for(DiffSpan mySpan : myDiffs) {	
				//System.out.println("mySpan: " + mySpan.getSpan() + " " + mySpan.getLeftOffset());
				boolean isNear = false;
				DiffSpan otherSpan;
				int otherInd = 0;
				double limit = mySpan.getSpan(); // XXX - NEAR_THRESHOLD;
				do {
					try {
						otherSpan = otherDiffs.get(otherInd);
					}
					catch (IndexOutOfBoundsException e) {
						break MYSPAN;
					}
					isNear = this.nearAbs(mySpan.getSpan(), otherSpan.getSpan());
					if(isNear) {
						
					} else {
						if(++otherInd >= otherDiffSize) {
							continue MYSPAN;
						}
					}
					
				} while(otherSpan.getSpan() > limit && (!isNear));
				// XXX: TODO why was this check above was otherSpan > mySpan in perl code, did it mean leftoFF or Span ?
				// Also what happens if the other span is less, we break and go to the outer loop. 
				// When other > my shouldn't we just break if the otherSpan is > myspan and the offset > nearabs ?
				//System.out.println("is_near : " + isNear + " other_span: " + otherSpan.getSpan() + " my_span: " + mySpan.getSpan());
				
				// We found spans in the the two signatures that are within an interesting threshold
				if(isNear) {
					double offset = mySpan.getLeftOffset() - otherSpan.getLeftOffset();
					String ofs_key = this.ofsKey(offset);
					if(checkedOffsets.get(ofs_key) == null) {
						SigMatch m1 = this.tryOffset(other,offset);
						checkedOffsets.put(ofs_key, m1);
						if(Math.abs(m1.getCorrection()) > CORRECTION_OFFSET_SLACK) { 
							double correctedOffset = offset + m1.getCorrection();
							SigMatch m2 = this.tryOffset(other, correctedOffset);
							checkedOffsets.put("c_" + ofs_key, m2);
						}
						
					}
				}
			}
		
			SigMatch best = null;
		    for(SigMatch m : checkedOffsets.values()) {
		    	if(best == null) {
		    		best = m;
		    	} else {
		    		if(betterMatch(m,best)) {
		    			best = m;
		    		} 
		    	}
		    }
		
		    //computeEnergyMatches(other, best);
		    
		    return best;
		    
	}
	
	private void editDistance(List<MixzingPeak> otherPeaks) {
		//String mydistString = computeDistString(myPeaks);
		//String otherdistString = computeDistString(otherPeaks);		
		//int dist = new LevenshteinDistance().LD(mydistString, otherdistString);
		//System.out.println("LEV DIST: " + dist + " LEV RATIO: " + ((double) dist)/myPeaks.size());
	}


	private String computeDistString(List<MixzingPeak> myPeaks2) {
		Collections.sort(myPeaks2);
		String s ="";
		double prevEnergy = -100d;
		for(MixzingPeak p : myPeaks2) {
			double ene = p.getEnergy();
			if(ene > prevEnergy) {
				s += "U";
			} else {
				s += "D";
			}
			prevEnergy = ene;
		}
		//System.out.println(s);
		return s;
	}


	private boolean betterMatch(SigMatch m, SigMatch best) {
		return   m.getUncertainity() < best.getUncertainity();
	}

	public class MatchIndex {
		int mine, other;
		public MatchIndex(int mine, int other) {
			this.mine = mine;
			this.other = other;
		}
		public int getMine() {
			return mine;
		}
		public int getOther() {
			return other;
		}
	}
	
	private SigMatch tryOffset(MixzingSignatureMatcher other, double offset) {
		
		//System.out.println("trying offset " + offset);
		
		ArrayList<Double> shifted2 = new ArrayList<Double>();
		for(double d : other.offsets) {
			shifted2.add(d + offset);
		}

		int matches = 0;
		ArrayList<Double> corrections = new ArrayList<Double>();
		ArrayList<MatchIndex> matchIndex = new ArrayList<MatchIndex>();
		double correction = 0;
		int numPeaks = this.offsets.size();
		int numPeaksOther = other.offsets.size();
		int maxMatches = Math.max(numPeaks, numPeaksOther);
		double error = 0;
		int ind1 = 0, ind2 = 0;
		MYPEAK:
		while(ind1 < numPeaks && ind2 < numPeaksOther) {
			// If the peaks in the two signatures are not within our error tolerance distance
			// examine the next peak from one of the signatures
			if(!this.nearAbs(this.offsets.get(ind1), shifted2.get(ind2))) {
				if(this.offsets.get(ind1) > shifted2.get(ind2)) {
					ind2++;
				} else {
					ind1++;
				}
				continue MYPEAK;
			}
			// We found two peaks that after accounting for any offset are within our tolerance distance
			// lets make sure we are considering two closest peaks, by peeking forward in the other list
			// advancing the other index if any subsequent peaks are closer
			OTHERPEAK:
				for(double nearest = Math.abs(this.offsets.get(ind1) - shifted2.get(ind2));
					ind2 < numPeaksOther - 1; ind2++) {
					double nextDistance = Math.abs(this.offsets.get(ind1) - shifted2.get(ind2+1));
					if(nextDistance > nearest) {
						break OTHERPEAK;
					} else {
						nearest = nextDistance;
					}
				}
			
			// So now ind1 ad ind2 point to indexes of two peaks that are closest to each other and within 
			// our tolerance distance
			matches++;
			double displacement = shifted2.get(ind2) - this.offsets.get(ind1);
			
			matchIndex.add(new MatchIndex(ind1,ind2));
			
			// With correction being signed it can cancel each other out giving false positives ?
			// Lets make it unsigned and try. Trying it this way was not very successful
			// displacement = Math.abs(displacement);
			
			corrections.add(-displacement);
			correction -= displacement;
			
			error += displacement * displacement;
			ind1++;
			ind2++; 
		}

		double relError = matches > 0 ? Math.sqrt(error/matches) : 1e300;
		correction = matches > 0 ? correction / matches : 0;
		
		SigMatch m = new SigMatch
			(offset, matches, maxMatches, relError, correction, corrections, numPeaks, numPeaksOther, Math.max(this.maxSpan(), other.maxSpan()),matchIndex);
		//System.out.println("tried: " + m.toStringShort());
		return m;
	}

	private double maxSpan() {
		return this.offsets.get(this.offsets.size() -1) - this.offsets.get(0);
	}

	private String ofsKey(double offset) {
		return (int) (offset/getEquivalenceThreshold()) + "";
	}
	
	protected synchronized List<DiffSpan> diffs() {
		if(diffs.size() == 0) {
			for(int l=0;l<offsets.size();l++) {
				for(int r=offsets.size() -1;r>l;r--) {
					double df = offsets.get(r) - offsets.get(l);
					diffs.add(new DiffSpan(df,offsets.get(l)));
				}
			}
			Collections.sort(diffs);
		}
		
		/*

		*/
		return diffs;
	}
	
	public void printAllUncertainities(boolean full) {
	    ArrayList<SigMatch> matches = new ArrayList<SigMatch>(checkedOffsets.values());
	    Collections.sort(matches);
	    String uncert = "Uncertainity Sorted: ";
	    if(full) {
	    	System.out.println(uncert);
	    }
	    for(SigMatch m : matches) {
	    	if(full)
	    		System.out.println(m);
	    	else
	    		uncert += m.getUncertainity() + " : ";
	    }
	    if(!full)
	    	System.out.println(uncert);		
	}
	
	public void printDiffSpans() {
		Formatter f = new Formatter();
		
		System.out.format("DiffSpans:");
		for(DiffSpan d : diffs) {
			System.out.format("%08.0f:", d.getSpan());
		}
		System.out.format("\n");		
	}
	
	protected boolean nearAbs(double one, double two) {		
		double diff = Math.abs(one - two);
		return diff < getNearThreshold();	
	}	
	
	private void computeEnergyMatches(MixzingSignatureMatcher other, SigMatch m)  {
		
		if(m == null) {
			return;
		}
		editDistance(this,other);
		
		ArrayList<MatchIndex> x = m.getMatchIndex();

		ArrayList<MatchTracker> newMine = new ArrayList<MatchTracker>();
		ArrayList<MatchTracker> newOther = new ArrayList<MatchTracker>();

		
		List<Double> loe = this.getEnergy();
		List<Double> aoe = other.getEnergy();

		if(loe == null)
			return;
		
		String mineStr = "";
		String yourStr = "";
		
		double prevMine = -100d, prevYour = -100d;
		
		for(MatchIndex mi : x) {
			int myIdx = mi.getMine();
			int yourIdx = mi.getOther();
			
			double myNewVal = loe.get(myIdx);
			double yrNewVal = loe.get(yourIdx);
			
			if(myNewVal > prevMine) {
				mineStr += "U";
			} else {
				mineStr += "D";		
			}
			if(yrNewVal > prevYour) {
				yourStr += "U";
			} else {
				yourStr += "D";		
			}			
			prevMine = myNewVal;
			prevYour = yrNewVal;
		}
		
        /*
		LevenshteinDistance ld = new LevenshteinDistance();
		int dist = ld.LD(mineStr, yourStr);
		if(x.size() < 5) {
			dist = 100;
		}
        */
		//System.out.println("Edit distance = " + dist  + " Ratio = " + ((double)dist) / (x.size() > 0 ? x.size() : 0.0001));
		
		
		
		double maxMine = -100, maxOther = -100;
		for (double d : loe) {
			if(d > maxMine) 
				maxMine = d;
		}
		for (double d : aoe) {
			if(d > maxOther) 
				maxOther = d;
		}		

		int index = 0;
		for (MatchIndex mi : x) {
			int mine = mi.getMine();
			int oth = mi.getOther();

			newMine.add(new MatchTracker(index,loe.get(mine)/maxMine));
			newOther.add(new MatchTracker(index,aoe.get(oth)/maxOther));

			index++;
		}

		Collections.sort(newMine);
		Collections.sort(newOther);

		String out = "";
		for(MatchTracker mi : newMine) {
			out += mi;
		}
		//System.out.println(out);
		out = "";
		for(MatchTracker mi : newOther) {
			out += mi;
		}
		//System.out.println(out);

		int maxDistance = -1;
		int totalDistance = 0;
		int exactEnergyMatches = 0;
		int consecMatches = 0;
		int totConsecMatches = 0;
		int maxConsecMatch = 0;

		for(int i=0;i<newMine.size();i++) {
			int distance = Math.abs(newMine.get(i).pos - newOther.get(i).pos);
			if(distance == 0) {
				exactEnergyMatches++;
				consecMatches++;
			} else {
				if(consecMatches > maxConsecMatch) {
					maxConsecMatch = consecMatches;
				}
				if(consecMatches > 1) {
					totConsecMatches += consecMatches;
				}
				consecMatches = 0;
			}
			totalDistance += distance;
			if(distance > maxDistance) {
				maxDistance = distance;
			}
		}
		double avgDistance = totalDistance / m.getMatches();
		totalDistance -= maxDistance;
		
		m.setConsecMatches(consecMatches);
		m.setExactEnergyMatches(exactEnergyMatches);
		m.setMaxConsecMatch(maxConsecMatch);
		m.setMaxDistance(maxDistance);
		m.setTotalDistance(totalDistance);
		m.setTotConsecMatches(totConsecMatches);
		m.setAvgDistance(avgDistance);

		//if(exactEnergyMatches >= 3 && m.getUncertainity() < 1E-8) {
		//System.out.println(" :Distance ; " + totalDistance + " Avg Distance: " + avgDistance + " exactEnergyMatches = " + exactEnergyMatches + "  of " + m.getMatches()  + " total_consec: " + totConsecMatches + " maxString: " + maxConsecMatch);
	}
	
	private void editDistance(MixzingSignatureMatcher one, MixzingSignatureMatcher two) {
		List<Double> loe = one.getEnergy();
		List<Double> aoe = two.getEnergy();

		String mineStr = "";
		String yourStr = "";
		
		double prevMine = -100d, prevYour = -100d;
		
		if(loe == null)
			return;
		
		for(double myNewVal : loe) {

			
			if(myNewVal > prevMine) {
				mineStr += "U";
			} else {
				mineStr += "D";		
			}	
			prevMine = myNewVal;
		}

		for(double yrNewVal : aoe) {
	
		
			if(yrNewVal > prevYour) {
				yourStr += "U";
			} else {
				yourStr += "D";		
			}			
			prevYour = yrNewVal;
		}
		
        /*
		LevenshteinDistance ld = new LevenshteinDistance();
		int dist = ld.LD(mineStr, yourStr);
		int numSigs = Math.max(loe.size(), aoe.size());
		*/
		//System.out.println("Edit distance for fullSig = " + dist  + " Ratio = " + ((double)dist) / (numSigs > 0 ? numSigs : 0.0001));
			
	}
	
 	public class MatchTracker implements Comparable<MatchTracker>{
		int pos;
		double val;
		public MatchTracker(int pos, double val) {
			this.pos = pos;
			this.val = val;
		}
		public int compareTo(MatchTracker o) {
			if(this.val > o.val) 
				return 1;
			if(this.val == o.val) 
				return 0;
			return -1;
		}

		public String toString() {
			return "[" + pos + ":" + val +  "]";
		}
	}
	
	public static void main(String[] args) {

		String sig1 = "131062.35|332242.48484848486|372728.3181818182|413263.7368421053|502436.4655172414|573789.4722222222|590906.3846153846|710831.5531914893|871250.8870967742|874642.3571428572|1065903.7246376812|1067581.3111111112|1095960.9285714286|1096839.238095238|1284927.205882353|";
        String sig11 = "131062.35|336202.48484848486|372708.3181818182|413203.7368421053|502406.4655172414|533789.4722222222|590996.3846153846|710891.5531914893|871200.8870967742|874692.3571428572|1065993.7246376812|1067501.3111111112|1095900.9285714286|1096899.238095238|1284997.205882353|";

		String sig2 = "18771.672413793105|63695.64150943396|108413.04|165111.89333333334|288773.8035714286|333139.58461538464|378600.609375|513100.97777777776|648124.3170731707|828359.0833333334|849077.5|853260.0|855227.3191489362|883229.5409836066|1168273.7735849055|";

		String sig3 = "2552|57370|111864|384463|439128|493696|602777|657314|711866|766372|820936|929766|1039079|1093645|1147815|";
        
		String sig4 = "203966|258508|313042|422088|476634|585690|640237|694781|751194|858378|967454|1131083|1161742|1240193|1294688|";		        
        
		String afid_2709541_full = "65924|171748|277762|383467|489293|595306|700915|806806|892694|912523|965495|1018364|1124412|1197557|1230270|";
        String afid_2709541_quick = "393177|446062|498986|551950|604843|710666|816512|869426|922342|975261|1028338|1134059|1186990|1239894|1292733|";
		
        String afid_2706379_quick = "32332|84414|138263|349875|455071|507767|561544|667372|719443|773185|878997|984923|1090714|1142831|1196580|";
        String afid_2706379_full = "23236|129046|181250|234970|340762|392880|446628|604536|658309|764168|816287|870001|975762|1081752|1187594|";
        
        String sigSTTAamazon = "577763.3333333334|627783.4444444445|629504.4444444445|782022.5185185185|835419.0204081633|1053662.0689655172|1118166.6451612904|1120326.0|1120833.0909090908|1122704.4705882352|1135793.0|1161186.6|1173464.3333333333|1186381.6666666667|1258855.4074074074|";
        String sigSTTAsandeep = "341710.87272727274|584486.195652174|634678.9230769231|637477.8181818182|788883.1724137932|842608.0198019802|1060323.0|1061043.65625|1124864.5|1126275.8857142858|1128042.4428571428|1129041.4166666667|1142519.0|1167924.3333333333|1180176.3636363635|";
        
        

        MixzingSignatureMatcher m1 = new MixzingSignatureMatcher(sig1);
        MixzingSignatureMatcher m11 = new MixzingSignatureMatcher(sig11);

        MixzingSignatureMatcher m2 = new MixzingSignatureMatcher(sig2);
		MixzingSignatureMatcher m3 = new MixzingSignatureMatcher(sig3);
		MixzingSignatureMatcher m4 = new MixzingSignatureMatcher(sig4);
		
		MixzingSignatureMatcher sig_2709541_full = new MixzingSignatureMatcher(afid_2709541_full);
		MixzingSignatureMatcher sig_2709541_quick = new MixzingSignatureMatcher(afid_2709541_quick);

		MixzingSignatureMatcher sig_2706379_full = new MixzingSignatureMatcher(afid_2706379_full);
		MixzingSignatureMatcher sig_2706379_quick = new MixzingSignatureMatcher(afid_2706379_quick);

		
		MixzingSignatureMatcher sig_stta_amazon = new MixzingSignatureMatcher(sigSTTAamazon);
        MixzingSignatureMatcher sig_stta_sandeep = new MixzingSignatureMatcher(sigSTTAsandeep);        
        
		SigMatch best;
		
		best = sig_stta_sandeep.match(sig_stta_amazon);
		//System.out.println("m3:m4 ... Best : " + best);
		sig_stta_sandeep.printAllUncertainities(false);
		
		/*  
		best = m3.match(m4);
		System.out.println("m3:m4 ... Best : " + best);
		
		best = m4.match(m3);
		System.out.println("m4:m3 ... Best : " + best);		

		m3.printDiffSpans();
		m4.printDiffSpans();
		
		best = m1.match(m2);
		System.out.println("m1:m2 ... Best : " + best);	

		best = m2.match(m1);
		System.out.println("m2:m1 ... Best : " + best);	
	
		best = m2.match(m3);
		System.out.println("m2:m3 ... Best : " + best);	

		best = m1.match(m3);
		System.out.println("m1:m3 ... Best : " + best);	
		
		best = m2.match(m4);
		System.out.println("m2:m4 ... Best : " + best);	

		best = m1.match(m4);
		System.out.println("m1:m4 ... Best : " + best);	


		best = m1.match(m1);
		System.out.println("m1:m1 ... Best : " + best);	

		best = m1.match(m11);
		System.out.println("m1:m11 ... Best : " + best);			

		best = m5.match(m6);
		System.out.println("m5:m6 ... Best : " + best);	

		best = m6.match(m5);
		System.out.println("m6:m5 ... Best : " + best);	

	
		
		best = m7.match(m8);
		System.out.println("m7:m8 ... Best : " + best);	
	    */	
		
		best = sig_2706379_full.match(sig_2709541_full);
		System.out.println("m8:m7 ... Best : " + best);	
		sig_2706379_full.printAllUncertainities(false);
		
		sig_2706379_full.printDiffSpans();
		sig_2709541_full.printDiffSpans();
		
		
		//m8.printAllUncertainities(true);
		//m8.printAllUncertainities(false);

		//best = sig_2709541_full.match(sig_2706379_full);
		//System.out.println("m8:m7 ... Best : " + best);	
		//sig_2709541_full.printAllUncertainities(false);
		
	}
}
