package com.mixzing.servicelayer;

import java.util.List;

public interface MixzingEventing {
	
	public enum EnumEvent {
		TrackAdd,
		TrackDelete,
		PlaylistAdd,
		PlaylistDelete,
		PlaylistTrackAdd,
		PlaylistTrackDelete,
		RatingAdd,
		RatingDelete,
		ResolutionStart,
		ResolutionCommitPoint,
		ResolutionFinish
	}
	
	public interface MixzingEvent2 {
		public EnumEvent getEvent();
		public Object getEventDataObject();
	}
	
	public interface MixzingEventListener2 {
		public void processEvent(MixzingEvent2 event);
	}
	
	public List<EnumEvent> subscribedEvents();
	
	public interface MixZingEventSource {
		public List<EnumEvent> supportedevents();

		public boolean registerListener(EnumEvent event, MixzingEventListener2 listener);

		public boolean unregisterListener(EnumEvent event, MixzingEventListener2 listener);
	}
}
