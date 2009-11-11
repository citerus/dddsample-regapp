package com.reporting2.db;

import static com.reporting2.Constants.US_DATETIME;
import com.reporting2.reports.VoyageReport;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import se.citerus.dddsample.reporting.api.VoyageDetails;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoyageReportRowMapper implements ParameterizedRowMapper<VoyageReport> {
  @Override
  public VoyageReport mapRow(ResultSet rs, int rowNum) throws SQLException {
    VoyageReport voyageReport = new VoyageReport();
    VoyageDetails voyageDetails = new VoyageDetails();
    voyageDetails.setVoyageNumber(rs.getString("voyage_number"));
    voyageDetails.setCurrentStatus(rs.getString("current_status"));
    voyageDetails.setDelayedByMinutes(rs.getInt("delayed_by_min"));
    voyageDetails.setEtaNextStop(US_DATETIME.format(rs.getTimestamp("eta_next_stop")));
    voyageDetails.setLastUpdatedOn(US_DATETIME.format(rs.getTimestamp("last_updated_on")));
    voyageDetails.setNextStop(rs.getString("next_stop"));
    voyageReport.setVoyage(voyageDetails);
    return voyageReport;
  }
}
