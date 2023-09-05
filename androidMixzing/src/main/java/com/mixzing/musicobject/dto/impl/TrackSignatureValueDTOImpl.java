package com.mixzing.musicobject.dto.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.mixzing.derby.UncheckedSQLException;
import com.mixzing.musicobject.dto.TrackSignatureValueDTO;

public class TrackSignatureValueDTOImpl implements TrackSignatureValueDTO {

    public TrackSignatureValueDTOImpl() {
        super();
    }


    public TrackSignatureValueDTOImpl(ResultSet rs) {
        try {
            this.setId(rs.getLong("id"));
            this.setLsid(rs.getLong("lsid"));
			this.setSkip(rs.getInt("skip"));
			this.setDuration(rs.getInt("duration"));
			this.setSuperWindowMs(rs.getInt("super_window_ms"));
			this.setSig(convertToList(rs.getString("signature")));
            this.setChannels(rs.getInt("channels"));
            this.setBitRate(rs.getInt("bitrate"));
            this.setFrequency(rs.getInt("frequency"));
            this.setMsPerFrame(rs.getFloat("ms_per_frame"));
            this.setSetToServer(rs.getInt("sent_to_server") == 1 ? true : false);
            this.setCodeVersion(rs.getString("code_version"));
        } catch ( SQLException e) {
            throw new UncheckedSQLException(e,"Create Object");
        }
    }


    protected long id;
        
    protected long lsid;
    
    protected List<Long> sig;

    protected boolean sentToServer;
    
    protected String codeVersion;
    
    protected int skip, duration, superWinMs, frequency, bitRate, channels;

    protected float msPerFrame;

    /* (non-Javadoc)
     * @see com.mixzing.musicobject.dto.impl.TrackSignatureValueDTO#getId()
     */
    public long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.mixzing.musicobject.dto.impl.TrackSignatureValueDTO#setId(long)
     */
    public void setId(long id) {
        this.id = id;
    }



    /* (non-Javadoc)
     * @see com.mixzing.musicobject.dto.impl.TrackSignatureValueDTO#getLsid()
     */
    public long getLsid() {
        return lsid;
    }

    /* (non-Javadoc)
     * @see com.mixzing.musicobject.dto.impl.TrackSignatureValueDTO#setLsid(long)
     */
    public void setLsid(long lsid) {
        this.lsid = lsid;
    }

    /* (non-Javadoc)
     * @see com.mixzing.musicobject.dto.impl.TrackSignatureValueDTO#getSig()
     */
    public List<Long> getSig() {
        return sig;
    }

    /* (non-Javadoc)
     * @see com.mixzing.musicobject.dto.impl.TrackSignatureValueDTO#setSig(java.util.List)
     */
    public void setSig(List<Long> sig) {
        this.sig = sig;
    }
    
	public int getDuration() {
		return this.duration;
	}

	public int getSkip() {
		return this.skip;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void setFrequency(int dur) {
		this.frequency = dur;
	}
	
	public void setDuration(int dur) {
		this.duration = dur;
		
	}

	public void setSkip(int skip) {
		this.skip = skip;
		
	}
	
    private List<Long> convertToList(String sigs) {
        ArrayList<Long> sig = new ArrayList<Long>();
        if(sigs != null) {
            StringTokenizer tok = new StringTokenizer(sigs,"|");
            while(tok.hasMoreElements()) {
                String s = (String) tok.nextElement();
                Long d = Long.valueOf(s);
                sig.add(d);
            }
        }
        return sig;
    }


	public int getBitRate() {
		// TODO Auto-generated method stub
		return bitRate;
	}


	public int getChannels() {
		// TODO Auto-generated method stub
		return channels;
	}



	public float getMsPerFrame() {
		// TODO Auto-generated method stub
		return msPerFrame;
	}


	public void setBitRate(int freq) {
		bitRate = freq;
	}


	public void setChannels(int freq) {
		channels = freq;
	}


	public void setMsPerFrame(float msFrame) {
		msPerFrame = msFrame;
	}


	public String getCodeVersion() {
		return codeVersion;
	}


	public boolean isSentToServer() {
		return sentToServer;
	}


	public void setCodeVersion(String ver) {
		codeVersion = ver;
	}


	public void setSetToServer(boolean sent) {
		sentToServer = sent;
	}


	public int getSuperWindowMs() {
		return superWinMs;
	}


	public void setSuperWindowMs(int swin) {
		superWinMs = swin;
	}    
}
