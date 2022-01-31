package com.android.stepcounter.model;

public class EventItem extends ListEvent{

        private GpsTrackerModel eventModel;

        public EventItem(GpsTrackerModel eventModel) {
            this.eventModel = eventModel;
        }

        public GpsTrackerModel getEventModel() {
            return eventModel;
        }

        @Override
        public int getType() {
            return TYPE_ITEM;
        }
    }