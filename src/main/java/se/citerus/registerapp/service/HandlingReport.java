package se.citerus.registerapp.service;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HandlingReport {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final Date completionTime;
    private final String trackingId;
    private final String eventType;
    private final String unloCode;
    private final String voyageNumber;

    /**
     * Register an cargo handling event.
     *
     * @param completionTime time when event occurred, for example the loading of cargo was completed
     * @param trackingId     tracking id of the cargo
     * @param voyageNumber   voyage number, if applicable
     * @param unloCode       United Nations Location Code for the location where the event occurred
     * @param eventType      type of event
     */
    public HandlingReport(Date completionTime, String trackingId, String eventType, String unloCode, String voyageNumber) {
        this.completionTime = completionTime;
        this.trackingId = trackingId;
        this.eventType = eventType;
        this.unloCode = unloCode;
        this.voyageNumber = voyageNumber;
    }

    public String toJson() { // TODO upgrade Spring and replace this with Jackson ObjectMapper
        if (voyageNumber != null) {
            return String.format("{\"completionTime\":\"%s\",\"trackingIds\":[\"%s\"],\"type\":\"%s\",\"unLocode\":\"%s\",\"voyageNumber\":\"%s\"}",
                    DATE_FORMAT.format(completionTime),
                    trackingId,
                    eventType,
                    unloCode,
                    voyageNumber
            );
        }
        return String.format("{\"completionTime\":\"%s\",\"trackingIds\":[\"%s\"],\"type\":\"%s\",\"unLocode\":\"%s\"}",
                DATE_FORMAT.format(completionTime),
                trackingId,
                eventType,
                unloCode
        );
    }
}
