package com.ya0ne.web.security;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ya0ne.core.domain.track.Track;

public class HyperlinkMapping implements Serializable {
	private static final long serialVersionUID = -2011016815710653498L;
	private static Logger logger = Logger.getLogger(HyperlinkMapping.class);

	@Autowired AccessMap<Track> tracksAccessMap;
	
	public HyperlinkMapping() {
	    logger.info(tracksAccessMap);
	}

	public String getTrackId(final Track track) {
        return tracksAccessMap.getIndirectReference(track);
    }

    public Track getTrack(final String indirectId) {
        return tracksAccessMap.getDirectReference(indirectId);
    }
}
